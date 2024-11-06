package com.ctnc.shared;

public class UserLocation extends Location{

	private String ip;
	private String countryCode;
	private String region;
	private String resolveLocationStatus;
	
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public String getResolveLocationStatus() {
		return resolveLocationStatus;
	}
	public void setResolveLocationStatus(String resolveLocationStatus) {
		this.resolveLocationStatus = resolveLocationStatus;
	}
	
	@Override
	public String toString() {
		return "UserLocation [ip=" + ip + ", countryCode=" + countryCode + ", region=" + region
				+ ", resolveLocationStatus=" + resolveLocationStatus + "]"+super.toString();
	}
	
	

}
