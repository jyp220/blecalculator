package com.hatiolab.blecalculator.model;

public class Beacon {
	private String deviceAddress;
	private String uuid;
	private int major;
	private int minor;
	private int txPower;
	
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
}
