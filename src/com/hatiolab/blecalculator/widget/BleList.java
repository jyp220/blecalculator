package com.hatiolab.blecalculator.widget;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hatiolab.blecalculator.MainActivity;
import com.hatiolab.blecalculator.Util;
import com.hatiolab.blecalculator.MainActivity.ViewHolder;
import com.hatiolab.blecalculator.model.Beacon;
import com.hatiolab.blecalculator.model.Position;
import com.hatiolab.blecalculator.model.Scene;

public class BleList extends BaseAdapter{
	private ArrayList<BluetoothDevice> devices;
//	private ArrayList<Integer> RSSIs;
	private LayoutInflater inflater;
	private ArrayList<Beacon> beaconSetting;
	private HashMap<String, Double> standardDeviation = new HashMap<String, Double>();
	private TextView txtLocation;
	private SceneFirebase sceneFirebase;
	
	private ArrayList<HashMap<String, ArrayList<Double>>> beaconRssi;

	public BleList(Context mContext, TextView location, SceneFirebase firebase) {
		super();
		devices = new ArrayList<BluetoothDevice>();
//		RSSIs = new ArrayList<Integer>();
		beaconSetting = new ArrayList<Beacon>();
		beaconRssi = new ArrayList<HashMap<String, ArrayList<Double>>>();
		inflater = ((Activity) mContext).getLayoutInflater();
		txtLocation = location;
		sceneFirebase = firebase;
	}
	
	public BleList(Context mContext, SceneFirebase firebase) {
		super();
		devices = new ArrayList<BluetoothDevice>();
//		RSSIs = new ArrayList<Integer>();
		beaconSetting = new ArrayList<Beacon>();
		beaconRssi = new ArrayList<HashMap<String, ArrayList<Double>>>();
		inflater = ((Activity) mContext).getLayoutInflater();
		sceneFirebase = firebase;
	}

	public void addDevice(BluetoothDevice device, int rssi, byte[] scanRecord, ArrayList<Scene> sceneBeacon, Long now, Long rssiUpdate) {
		Beacon beacon;
		
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
				if(beacon != null) {
					beacon.setRssiCount(beaconSetting.get(devices.indexOf(device)).getRssiCount());
					beaconSetting.set(devices.indexOf(device), beacon);
				}
			}
		}
		
	}

	public void clear() {
		devices.clear();
	}

	@Override
	public int getCount() {
		return devices.size();
	}

	@Override
	public Object getItem(int position) {
		return devices.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;

		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(
					android.R.layout.two_line_list_item, null);
			viewHolder.deviceName = (TextView) convertView
					.findViewById(android.R.id.text1);
			viewHolder.deviceRssi = (TextView) convertView
					.findViewById(android.R.id.text2);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		String deviceAddress = devices.get(position).getAddress();
		String deviceName = devices.get(position).getName();
		int rssi = beaconSetting.get(position).getRssi();
		int txPower = beaconSetting.get(position).getTxPower();
		
		
		ArrayList<Double> rssiValue = new ArrayList<Double>();
		HashMap<String, ArrayList<Double>> addressToRssi = new HashMap<>();
		rssiValue.add((double) rssi);
		
		boolean bool = true;
		if(beaconRssi.size() > 0) {
			for(int i = 0 ; i < beaconRssi.size() ; i++) {
				if(beaconRssi.get(i).containsKey(deviceAddress)) {
					beaconRssi.get(i).get(deviceAddress).add((double) rssi);
					bool = false;
					break;
				}
			}
			
			if(bool) {
				addressToRssi.put(deviceAddress, rssiValue);
				beaconRssi.add(addressToRssi);
			}
		} else {
			addressToRssi.put(deviceAddress, rssiValue);
			beaconRssi.add(addressToRssi);
		}
		
		
		
		
		
		
		
		
//		descendingSort();
//		Beacon[] test = new Beacon[4];
//		if(beaconSetting.size() >= 4) {
//			test[0] = beaconSetting.get(0);
//			test[1] = beaconSetting.get(1);
//			test[2] = beaconSetting.get(2);
//			test[3] = beaconSetting.get(3);
//			
//			int k = 3;                             // sequence length   
//			
//			combination(test, k);
//		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		if(beaconRssi.get(position).get(deviceAddress).size() > 500) {
			double result = Util.standardDeviation(beaconRssi.get(position).get(deviceAddress));
			standardDeviation.put(deviceAddress, result);
			double avg = Util.mean(beaconRssi.get(position).get(deviceAddress));
			double distance = Util.calculateDistance(txPower, avg);
			
			sceneFirebase.setStandardDeviation(deviceAddress, avg, distance, result);
			
			beaconRssi.get(position).get(deviceAddress).clear();
		}
		
		String deviceInfo = deviceAddress + " / Distance : " + 
				Util.decimalScale(String.valueOf(Util.calculateDistance(txPower, rssi)), 3) + " / beaconSetting Distance : " + Util.decimalScale(String.valueOf(Util.calculateDistance(beaconSetting.get(position).getTxPower(), beaconSetting.get(position).getRssi())), 3) 
				+ " / count : " + beaconSetting.get(position).getRssiCount() + " / txPower : " + txPower + " / SRSSI : " + beaconSetting.get(position).getRssi();
		

		viewHolder.deviceName.setText(deviceAddress != null && deviceAddress.length() > 0 ? deviceInfo : "알 수 없는 장치");
		viewHolder.deviceRssi.setText(String.valueOf(rssi));

		return convertView;
	}
	
}
