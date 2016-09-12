package com.hatiolab.blecalculator;

import java.util.UUID;

import android.Manifest;
import android.app.Activity;
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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;

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
    private Sensor mGyroscope;
    private Sensor accSensor;
    
    private SceneFirebase sceneFirebase;

	static class ViewHolder {
		TextView deviceName;
		TextView deviceRssi;
	}

	Button button;// 버튼
	TextView location, accelerometer, firebase, uuid;

	ListView listView;// 리스트뷰 객체
	BleList bleList = null;// 리스트 어댑터

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Firebase.setAndroidContext(this);
		
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
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        //엑셀러로미터 센서(가속)
        accSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        
		location = (TextView) findViewById(R.id.location);
		accelerometer = (TextView) findViewById(R.id.accelerometer);
		firebase = (TextView) findViewById(R.id.firebase);
		uuid = (TextView) findViewById(R.id.uuid);

		// 리스트뷰 설정
		bleList = new BleList(MainActivity.this, location);
		listView = (ListView) findViewById(R.id.listView);
		listView.setAdapter(bleList);

		// 버튼설정
		button = (Button) findViewById(R.id.button);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if (!scanning) {
					bluetoothAdapter.startLeScan(leScanCallback);
				} else {
					bluetoothAdapter.stopLeScan(leScanCallback);
					bleList.clear();
					bleList.notifyDataSetChanged();
				}
				scanning = !scanning;

			}
		});
		
		sceneFirebase = new SceneFirebase("260", firebase);
		uuid.setText(getDevicesUUID(MainActivity.this));
	}
	
	//정확도에 대한 메소드 호출 (사용안함)
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
  
    }
      
      
    //센서값 얻어오기
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
  
        if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {
              
        }
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
        	double xAxis = Util.decimalScale(String.valueOf(event.values[0]), 3);
        	double yAxis = Util.decimalScale(String.valueOf(event.values[1]), 3);
        	double zAxis = Util.decimalScale(String.valueOf(event.values[2]), 3);
            
            accelerometer.setText("xAxis : " + xAxis + " / yAxis : " + yAxis + " / zAxis : " + zAxis);
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
        mSensorManager.registerListener(this, mGyroscope,SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, accSensor,SensorManager.SENSOR_DELAY_UI);
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
			// Log.d("scan",device.getName() + " RSSI :" + rssi + " Record " +
			// scanRecord);
			if (recoScan(device.getName())) {
				bleList.addDevice(device, rssi, scanRecord);
				bleList.notifyDataSetChanged();
			}
		}
	};

	private boolean recoScan(String deviceName) {
		if(deviceName != null) {
			if(deviceName.equals("RECO")) {
				return true;
			}
		}

		return false;
	}

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
}
