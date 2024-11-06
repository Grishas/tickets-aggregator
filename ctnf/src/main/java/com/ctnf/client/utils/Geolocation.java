package com.ctnf.client.utils;

public class Geolocation {

	private Double latitude = 0d;
	private Double longitude = 0d;
	private boolean supported = false;
	private int errorCode =-1;
	private String localMessage = "";
	
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	public String getLocalMessage() {
		return localMessage;
	}
	public void setLocalMessage(String localMessage) {
		this.localMessage = localMessage;
	}
	
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	public boolean isSupported() {
		return supported;
	}
	public void setSupported(boolean supported) {
		this.supported = supported;
	}
	
	@Override
	public String toString() {
		return "Geolocation [latitude=" + latitude + ", longitude=" + longitude + ", supported=" + supported
				+ ", errorCode=" + errorCode + ", localMessage=" + localMessage + "]";
	}

	
}
