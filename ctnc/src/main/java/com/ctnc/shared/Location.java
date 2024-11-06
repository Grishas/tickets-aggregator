package com.ctnc.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Location implements IsSerializable{

	private String segmentedKey;
	private String city;
	private double latitude = 0d;
	private double longitude = 0d;
	
	public Location() {
		super();
	}
	public String getSegmentedKey() {
		return segmentedKey;
	}
	public void setSegmentedKey(String segmentedKey) {
		this.segmentedKey = segmentedKey;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	@Override
	public String toString() {
		return "Location [segmentedKey=" + segmentedKey + ", city=" + city + ", latitude=" + latitude + ", longitude="
				+ longitude + "]";
	}
}
