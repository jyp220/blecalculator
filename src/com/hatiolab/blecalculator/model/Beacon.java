package com.hatiolab.blecalculator.model;

public class Beacon {
	private String deviceAddress;
	private String uuid;
	private int rssi;
	private int major;
	private int minor;
	private int txPower;
	private double beaconX;
	private double beaconY;
	private int rssiCount = 1;
	
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public int getMajor() {
		return major;
	}
	public void setMajor(int major) {
		this.major = major;
	}
	public int getMinor() {
		return minor;
	}
	public void setMinor(int minor) {
		this.minor = minor;
	}
	public int getTxPower() {
		return txPower;
	}
	public void setTxPower(int txPower) {
		this.txPower = txPower;
	}
	public String getDeviceAddress() {
		return deviceAddress;
	}
	public void setDeviceAddress(String deviceAddress) {
		this.deviceAddress = deviceAddress;
	}
	public int getRssi() {
		return rssi;
	}
	public void setRssi(int rssi) {
		this.rssi = rssi;
	}
	public double getBeaconX() {
		return beaconX;
	}
	public void setBeaconX(double beaconX) {
		this.beaconX = beaconX;
	}
	public double getBeaconY() {
		return beaconY;
	}
	public void setBeaconY(double beaconY) {
		this.beaconY = beaconY;
	}
	public int getRssiCount() {
		return rssiCount;
	}
	public void setRssiCount(int rssiCount) {
		this.rssiCount = rssiCount;
	}
}
