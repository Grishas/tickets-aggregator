package com.ctntc;

public class TCException extends Exception {

	private static final long serialVersionUID = -5547919012597516248L;
	
	public TCException(){
		super();
	}
	public TCException(String message,Throwable throwable){
		super(message,throwable);
	}
	public TCException(Throwable throwable){
		super(throwable);
	}
	public TCException(String message){
		super(message);
	}
}
