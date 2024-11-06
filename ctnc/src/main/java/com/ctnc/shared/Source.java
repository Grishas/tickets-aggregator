package com.ctnc.shared;

public enum Source {

	ticketnetwork("www.tndirect.com"),ticketcity("www.ticketcity.com");
	
	private String name;
	
	Source(String name){
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
