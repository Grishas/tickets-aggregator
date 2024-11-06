package com.ctnc.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public enum IndexKey implements IsSerializable{
	
	event(0),performer(1),venue(2),location(3),dates(4);
	
	private int value = -1;
	
	private IndexKey(int value){
		this.value = value;
	}
	
	public int value(){
		return this.value;
	}
}	
