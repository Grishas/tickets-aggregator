package com.ctnc;

public enum GetTicketsStatus {

	found_tickest(0),not_found_tickest(1),access_error(2),unexpected_error(3);

	private int status;
	
	GetTicketsStatus(int status){
		this.status = status;
	}
	
	public int getStatus() {
		return status;
	}
	
}
