package com.ctnl.events.collector;

public class CollectProperties {

	private String[] countriesAbbreviation = null;
	private int retryTimes;
	
	public int getRetryTimes() {
		return retryTimes;
	}
	public void setRetryTimes(int retryTimes) {
		this.retryTimes = retryTimes;
	}
	public String[] getCountriesAbbreviation() {
		return countriesAbbreviation;
	}
	public void setCountriesAbbreviation(String[] countriesAbbreviation) {
		this.countriesAbbreviation = countriesAbbreviation;
	}	
}
