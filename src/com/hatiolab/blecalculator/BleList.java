package com.hatiolab.blecalculator;

import java.util.ArrayList;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hatiolab.blecalculator.MainActivity.ViewHolder;
import com.hatiolab.blecalculator.model.Beacon;

public class BleList extends BaseAdapter{
	private int[] nodeA = {0,0};
	private int[] nodeB = {6,0};
	private int[] nodeC = {2,4};
	
	private boolean testNodeA = false;
	private boolean testNodeB= false;
	private boolean testNodeC = false;
	
	private ArrayList<BluetoothDevice> devices;
	private ArrayList<Integer> RSSIs;
	private LayoutInflater inflater;
	private ArrayList<Beacon> beaconSetting;
	TextView txtLocation;

	public BleList(Context mContext, TextView location) {
		super();
		devices = new ArrayList<BluetoothDevice>();
		RSSIs = new ArrayList<Integer>();
		beaconSetting = new ArrayList<Beacon>();
		inflater = ((Activity) mContext).getLayoutInflater();
		txtLocation = location;
	}

	public void addDevice(BluetoothDevice device, int rssi, byte[] scanRecord) {
		if (!devices.contains(device)) {
			devices.add(device);
			RSSIs.add(rssi);
			beaconSetting.add(Util.recordParser(device, scanRecord));
		} else {
			RSSIs.set(devices.indexOf(device), rssi);
		}
	}

	public void clear() {
		devices.clear();
		RSSIs.clear();
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
		int rssi = RSSIs.get(position);
		int txPower = beaconSetting.get(position).getTxPower();
		
		String deviceInfo = deviceName + " / " + deviceAddress + " / " + Util.decimalScale(String.valueOf(Util.calculateDistance(txPower, rssi)), 3) + " / " + txPower;
		

		viewHolder.deviceName.setText(deviceAddress != null && deviceAddress.length() > 0 ? deviceInfo : "알 수 없는 장치");
		viewHolder.deviceRssi.setText(String.valueOf(rssi));
		
		
		
		
		
		if(devices.size() > 2) {
			int rssiA = 0;
			int txPowerA = 0;
			int rssiB = 0;
			int txPowerB = 0;
			int rssiC = 0;
			int txPowerC = 0;
			
			for(int i = 0 ; i < devices.size() ; i++) {
				if(devices.get(i).getAddress().equals("E5:86:0C:10:54:77")) {
					rssiA = RSSIs.get(i);
					txPowerA = beaconSetting.get(i).getTxPower();
					testNodeA = true;
				} else if(devices.get(i).getAddress().equals("DF:8B:8B:ED:41:85")) {
					rssiB = RSSIs.get(i);
					txPowerB = beaconSetting.get(i).getTxPower();
					testNodeB = true;
				} else {
					rssiC = RSSIs.get(i);
					txPowerC = beaconSetting.get(i).getTxPower();
					testNodeC = true;
				}
			}
			
			if(testNodeA && testNodeB && testNodeC) {
				double distanceA = Util.decimalScale(String.valueOf(Util.calculateDistance(txPowerA, rssiA)), 3);
				double distanceB = Util.decimalScale(String.valueOf(Util.calculateDistance(txPowerB, rssiB)), 3);
				double distanceC = Util.decimalScale(String.valueOf(Util.calculateDistance(txPowerC, rssiC)), 3);
				
				double result[] = Util.moveNode(nodeA, nodeB, nodeC, distanceA, distanceB, distanceC);
				if(result != null)
					txtLocation.setText("X : " + result[0] + ", Y : " + result[1]);
				
				testNodeA = false;
				testNodeB = false;
				testNodeC = false;
			}
			
		}

		return convertView;
	}
}
