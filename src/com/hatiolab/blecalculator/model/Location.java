package com.hatiolab.blecalculator.model;

import java.sql.Date;

public class Location {
	private String uuid;
	private String type;
	private String interval;
	private double[] location;
	private Date timestamp;
	
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getInterval() {
		return interval;
	}
	public void setInterval(String interval) {
		this.interval = interval;
	}
	public double[] getLocation() {
		return location;
	}
	public void setLocation(double[] location) {
		this.location = location;
	}
	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
}
