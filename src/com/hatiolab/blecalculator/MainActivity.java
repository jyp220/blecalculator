package com.hatiolab.blecalculator;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer.Optimum;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularMatrixException;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidplot.xy.SimpleXYSeries;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.hatiolab.blecalculator.kalman.jkalman.JKalman;
import com.hatiolab.blecalculator.madgwickAHRS.MadgwickAHRS;
import com.hatiolab.blecalculator.model.Beacon;
import com.hatiolab.blecalculator.model.Position;
import com.hatiolab.blecalculator.model.Scene;
import com.hatiolab.blecalculator.trilateration.LinearLeastSquaresSolver;
import com.hatiolab.blecalculator.trilateration.NonLinearLeastSquaresSolver;
import com.hatiolab.blecalculator.trilateration.TrilaterationFunction;
import com.hatiolab.blecalculator.widget.BleList;
import com.hatiolab.blecalculator.widget.CustomProgressDialog;
import com.hatiolab.blecalculator.widget.SceneFirebase;

public class MainActivity extends Activity implements SensorEventListener {
	boolean scanning = false;

	private static final int REQUEST_ENABLE_BT = 1;
	private BluetoothManager bluetoothManager;
	// 블루투스 매니저
	// 블루트스 기능을 총괄적으로 관리함.
	private BluetoothAdapter bluetoothAdapter;
	// 블루투스 연결자
	// 블루투스를 스켄하거나, 페어링된장치목록을 읽어들일 수 있습니다.
	// 이를 바탕으로 블루투스와의 연결을 시도할 수 있습니다.
	
	
  
    private SensorManager mSensorManager;
    private Sensor accSensor, gyroSensor, magSensor;
    private long lastUpdate;
    private long rssiUpdate;
    
    
    SimpleXYSeries seriesAccx;
    SimpleXYSeries seriesAccy;
    SimpleXYSeries seriesAccz;
    private static final int HISTORY_SIZE = 500;
    
    private float[] gyro = new float[3];
    private float[] magnet = new float[3];
    private float[] accel = new float[3];
    
    DecimalFormat m_format;
    private float[] gravity_data = new float[3];
    private float[] linear_acceleration = new float[3];
    
    MadgwickAHRS madgwickAHRS = new MadgwickAHRS(0.01f, 0.041f);
    private Timer madgwickTimer = new Timer();
    double lpPitch=0,lpRpll=0,lpYaw=0;
    
    private SceneFirebase sceneFirebase;
    
    private String phoneUUid = "";

	public static class ViewHolder {
		public TextView deviceName;
		public TextView deviceRssi;
	}

	Button button;// 버튼
	TextView location;//, accelerometer, uuid, madgPitch, madgRoll, madgYaw, tvStandardDeviation, gravity;

	ListView listView;// 리스트뷰 객체
	BleList bleList = null;// 리스트 어댑터
	
	private ArrayList<BluetoothDevice> devices;
	private ArrayList<Beacon> beaconSetting;
	private Position avgPosition = new Position();
	private ArrayList<Scene> sceneBeacon = new ArrayList<Scene>();
	
	private Dialog dialog;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Firebase.setAndroidContext(this);
		
		dialog = CustomProgressDialog.initDialog(this, R.style.CustomProgressDialog, "", R.string.loading);
		
		devices = new ArrayList<BluetoothDevice>();
		beaconSetting = new ArrayList<Beacon>();
		
		ActivityCompat.requestPermissions(this,
				new String[] { Manifest.permission.BLUETOOTH }, 1);

		bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		bluetoothAdapter = bluetoothManager.getAdapter();

		if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
			// 블루투스를 지원하지 않거나 켜져있지 않으면 장치를끈다.
			// Toast.makeText(this, "블루투스를 켜주세요", Toast.LENGTH_SHORT).show();
			// finish();
			on();
		}
		
		//센서 매니저 얻기
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //자이로스코프 센서(회전)
        gyroSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        //엑셀러로미터 센서(가속)
        accSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        
        mSensorManager.registerListener(this, accSensor, 0);//every 5ms
        mSensorManager.registerListener(this, gyroSensor, 0);
        mSensorManager.registerListener(this, magSensor, 0);
        
        m_format = new DecimalFormat();
        m_format.applyLocalizedPattern("0.##");
        
		location = (TextView) findViewById(R.id.location);
//		accelerometer = (TextView) findViewById(R.id.accelerometer);
//		gravity = (TextView) findViewById(R.id.gravity);
//		tvStandardDeviation = (TextView) findViewById(R.id.standardDeviation);
//		uuid = (TextView) findViewById(R.id.uuid);
//		madgPitch = (TextView) findViewById(R.id.madgPitch);
//		madgRoll = (TextView) findViewById(R.id.madgRoll);
//		madgYaw = (TextView) findViewById(R.id.madgYaw);

		sceneFirebase = new SceneFirebase("260");
		
		getScene(sceneFirebase.getFirebase());
		
		
		// 리스트뷰 설정
		bleList = new BleList(MainActivity.this, location, sceneFirebase);
		bleList = new BleList(MainActivity.this, sceneFirebase);
		listView = (ListView) findViewById(R.id.listView);
		listView.setAdapter(bleList);

		// 버튼설정
		button = (Button) findViewById(R.id.button);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if (!scanning) {
					bluetoothAdapter.startLeScan(leScanCallback);
					
					lastUpdate = System.currentTimeMillis();
					rssiUpdate = System.currentTimeMillis();
					
					madgwickTimer.scheduleAtFixedRate(new DoMadgwick(), 1000, 1000);
				} else {
					madgwickTimer.notify();
					bluetoothAdapter.stopLeScan(leScanCallback);
					bleList.clear();
					bleList.notifyDataSetChanged();
				}
				scanning = !scanning;
			}
		});
		
		seriesAccx = new SimpleXYSeries("accx");
        seriesAccx.useImplicitXVals();
        seriesAccy = new SimpleXYSeries("accy");
        seriesAccy.useImplicitXVals();
        seriesAccz = new SimpleXYSeries("accz");
        seriesAccz.useImplicitXVals();
		
		phoneUUid = getDevicesUUID(this);
	}
	
	//정확도에 대한 메소드 호출 (사용안함)
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
  
    }
      
      
    //센서값 얻어오기
    public void onSensorChanged(SensorEvent sensorEvent) {
    	if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(sensorEvent.values, 0, accel, 0, 3);
        } else if (sensorEvent.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            System.arraycopy(sensorEvent.values, 0, gyro, 0, 3);

        } else if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(sensorEvent.values, 0, magnet, 0, 3);

        }
        
    }
  
    // 주기 설명
    // SENSOR_DELAY_UI 갱신에 필요한 정도 주기
    // SENSOR_DELAY_NORMAL 화면 방향 전환 등의 일상적인  주기
    // SENSOR_DELAY_GAME 게임에 적합한 주기
    // SENSOR_DELAY_FASTEST 최대한의 빠른 주기
  
      
    //리스너 등록
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, gyroSensor,SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, accSensor,SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, magSensor,SensorManager.SENSOR_DELAY_GAME);
    }
      
    //리스너 해제
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

	// 스켄 이후 장치 발견 이벤트
	private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {

		@Override
		public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
			Beacon beacon;
			Long now = System.currentTimeMillis();
			
			bleList.addDevice(device, rssi, scanRecord, sceneBeacon, now, rssiUpdate);
			bleList.notifyDataSetChanged();
			
			if(now - rssiUpdate < 10000) {
				if (!devices.contains(device)) {
					int avgRssi = Util.avgFilter(1, 0, rssi);
					beacon = Util.recordParser(device, avgRssi, scanRecord, sceneBeacon);
					if(beacon != null) {
						beacon.setRssiCount(2);
						devices.add(device);
						beaconSetting.add(beacon);
					}
				} else {
					int avgRssi = Util.avgFilter(beaconSetting.get(devices.indexOf(device)).getRssiCount(), beaconSetting.get(devices.indexOf(device)).getRssi(), rssi);
					beacon = Util.recordParser(device, avgRssi, scanRecord, sceneBeacon);
					if(beacon != null) {
						beacon.setRssiCount(beaconSetting.get(devices.indexOf(device)).getRssiCount() + 1);
						beaconSetting.set(devices.indexOf(device), beacon);
					}
				}
			} else {
				if (!devices.contains(device)) {
					int lpfRssi = Util.lpfilter(0.9, 0, rssi);
					beacon = Util.recordParser(device, lpfRssi, scanRecord, sceneBeacon);
					if(beacon != null) {
						devices.add(device);
						beaconSetting.add(beacon);
					}
				} else {
					int lpfRssi = Util.lpfilter(0.9, beaconSetting.get(devices.indexOf(device)).getRssi(), rssi);
					beacon = Util.recordParser(device, lpfRssi, scanRecord, sceneBeacon);
					if(beacon != null)
						beaconSetting.set(devices.indexOf(device), beacon);
				}
			}
		}
	};

	public void on() {
		if (!bluetoothAdapter.isEnabled()) {
			Intent turnOnIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(turnOnIntent, REQUEST_ENABLE_BT);
			Toast.makeText(getApplicationContext(), "Bluetooth turned on",
					Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(getApplicationContext(), "Bluetooth is already on",
					Toast.LENGTH_LONG).show();
		}
	}
	
	private String getDevicesUUID(Context mContext){
        final TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String deviceId = deviceUuid.toString();
        return deviceId;
    }
	
	class DoMadgwick extends TimerTask {

		public void run() {
			Long now = System.currentTimeMillis();
			madgwickAHRS.SamplePeriod = (now - lastUpdate) / 1000.0f;
			lastUpdate = now;
			madgwickAHRS.Update(gyro[0], gyro[1], gyro[2], accel[0], accel[1],
					accel[2], magnet[0], magnet[1], magnet[2]);
			if (seriesAccx.size() > HISTORY_SIZE) {
				seriesAccx.removeFirst();
				seriesAccy.removeFirst();
				seriesAccz.removeFirst();
			}

			// add the latest history sample:
			lpPitch = lpPitch * 0.2 + madgwickAHRS.MadgPitch * 0.8;
			lpRpll = lpRpll * 0.2 + madgwickAHRS.MadgRoll * 0.8;
			lpYaw = lpYaw * 0.2 + madgwickAHRS.MadgYaw * 0.8;
			seriesAccx.addLast(null, lpPitch);
			seriesAccy.addLast(null, lpRpll);
			seriesAccz.addLast(null, lpYaw);
			
			
			ArrayList<Beacon> beaconsPosition = new ArrayList<Beacon>();
			beaconsPosition.addAll( beaconSetting );
			beaconsPosition = Util.descendingSort(beaconsPosition);
			
			if(beaconsPosition.size() >= 4) {
				double[][] positions = new double[][] { { beaconsPosition.get(0).getBeaconX() / 100, beaconsPosition.get(0).getBeaconY() / 100 }, 
						{ beaconsPosition.get(1).getBeaconX() / 100, beaconsPosition.get(1).getBeaconY() / 100 }, 
						{ beaconsPosition.get(2).getBeaconX() / 100, beaconsPosition.get(2).getBeaconY() / 100 }, 
						{ beaconsPosition.get(3).getBeaconX() / 100, beaconsPosition.get(3).getBeaconY() / 100 } };
				
				
				double[] distances = new double[] { Util.calculateDistance(beaconsPosition.get(0).getTxPower(), beaconsPosition.get(0).getRssi()), 
						Util.calculateDistance(beaconsPosition.get(1).getTxPower(), beaconsPosition.get(1).getRssi()), 
						Util.calculateDistance(beaconsPosition.get(2).getTxPower(), beaconsPosition.get(2).getRssi()), 
						Util.calculateDistance(beaconsPosition.get(3).getTxPower(), beaconsPosition.get(3).getRssi()) };
	
				NonLinearLeastSquaresSolver solver = new NonLinearLeastSquaresSolver(new TrilaterationFunction(positions, distances), new LevenbergMarquardtOptimizer());
				Optimum optimum = solver.solve();

				// the answer
				double[] centroid = optimum.getPoint().toArray();
				avgPosition.setCenterX(centroid[0] * 100);
				avgPosition.setCenterY(centroid[1] * 100);
//				testResults(expectedPosition, 0.01, optimum, x);
			}
			
			try {
				runOnUiThread(new Runnable() {
					public void run() {
						
						final float alpha = (float) 0.8;
			            
			            gravity_data[0] = alpha * gravity_data[0] + (1 - alpha) * accel[0];
			            gravity_data[1] = alpha * gravity_data[1] + (1 - alpha) * accel[1];
			            gravity_data[2] = alpha * gravity_data[2] + (1 - alpha) * accel[2];
			            
			            
			            linear_acceleration[0] = accel[0] - gravity_data[0];
			            linear_acceleration[1] = accel[1] - gravity_data[1];
			            linear_acceleration[2] = accel[2] - gravity_data[2];
			            
//			            gravity.setText("X : " + m_format.format(linear_acceleration[0]) + " / Y : " + m_format.format(linear_acceleration[1]) + " / Z : " + m_format.format(linear_acceleration[2]));
//			            accelerometer.setText("X : " + m_format.format(accel[0]) + " / Y : " + m_format.format(accel[1]) + " / Z : " + m_format.format(accel[2]));
			            
			            location.setText("X : " + avgPosition.getCenterX() + " / Y : " + avgPosition.getCenterY());
					}
				});
				
				sceneFirebase.setPosition(avgPosition.getCenterX(), avgPosition.getCenterY(), Math.toRadians(madgwickAHRS.MadgPitch), Math.toRadians(madgwickAHRS.MadgRoll), Math.toRadians(madgwickAHRS.MadgYaw), phoneUUid);
			} catch (Exception e) {
			}
		}
	}
	
	public void getScene(Firebase myFirebaseRef) {
		myFirebaseRef.child("260").child("beacons").addValueEventListener(new ValueEventListener() {
	           @Override
	           public void onDataChange(DataSnapshot snapshot) {
	        	   if(snapshot.getValue() != null) {
	        		   
	        		   for (DataSnapshot postSnapshot: snapshot.getChildren()) {
	        			   Scene post = postSnapshot.getValue(Scene.class);
	        			   post.setBeaconId(postSnapshot.getKey());
	        			   
	        			   sceneBeacon.add(post);
	        	       }
	        	   }
	        	   
	        	   dialog.dismiss();
	           }
	           @Override public void onCancelled(FirebaseError error) { }
	      });
	}
	
	private void testResults(double[] expectedPosition, final double delta, Optimum optimum, RealVector x) {

		double[] calculatedPosition = optimum.getPoint().toArray();

		int numberOfIterations = optimum.getIterations();
		int numberOfEvaluations = optimum.getEvaluations();

		StringBuilder output = new StringBuilder("expectedPosition: ");
		for (int i = 0; i < expectedPosition.length; i++) {
			output.append(expectedPosition[i]).append(" ");
		}
		output.append("\n");
		output.append("linear calculatedPosition: ");
		double[] linearCalculatedPosition = x.toArray();
		for (int i = 0; i < linearCalculatedPosition.length; i++) {
			output.append(linearCalculatedPosition[i]).append(" ");
		}
		output.append("\n");
		output.append("non-linear calculatedPosition: ");
		for (int i = 0; i < calculatedPosition.length; i++) {
			output.append(calculatedPosition[i]).append(" ");
		}
		output.append("\n");

		output.append("numberOfIterations: ").append(numberOfIterations).append("\n");
		output.append("numberOfEvaluations: ").append(numberOfEvaluations).append("\n");
		try {
			RealVector standardDeviation = optimum.getSigma(0);
			output.append("standardDeviation: ").append(standardDeviation).append("\n");
			RealMatrix covarianceMatrix = optimum.getCovariances(0);
			output.append("covarianceMatrix: ").append(covarianceMatrix).append("\n");
		} catch (SingularMatrixException e) {
			System.err.println(e.getMessage());
		}

		System.out.println(output.toString());

		// expected == calculated?
		for (int i = 0; i < calculatedPosition.length; i++) {
//			assertEquals(expectedPosition[i], calculatedPosition[i], delta);
		}
	}
}
