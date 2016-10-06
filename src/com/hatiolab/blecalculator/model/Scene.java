package com.hatiolab.blecalculator.model;

public class Scene {
	private String beaconId;
	private double left;
	private double top;
	private double zPos;
	
	public String getBeaconId() {
		return beaconId;
	}
	public void setBeaconId(String beaconId) {
		this.beaconId = beaconId;
	}
	public double getLeft() {
		return left;
	}
	public void setLeft(double left) {
		this.left = left;
	}
	public double getTop() {
		return top;
	}
	public void setTop(double top) {
		this.top = top;
	}
	public double getzPos() {
		return zPos;
	}
	public void setzPos(double zPos) {
		this.zPos = zPos;
	}
}
