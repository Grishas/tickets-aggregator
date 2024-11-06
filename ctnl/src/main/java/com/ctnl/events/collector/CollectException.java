package com.ctnl.events.collector;

public class CollectException extends Exception {

	private static final long serialVersionUID = -8527060637239738508L;
	public CollectException(){
		super();
	}
	public CollectException(String message,Throwable throwable){
		super(message,throwable);
	}
	public CollectException(String message){
		super(message);
	}
	public CollectException(Throwable throwable){
		super(throwable);
	}
}
