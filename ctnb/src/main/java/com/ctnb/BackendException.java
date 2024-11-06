package com.ctnb;

public class BackendException extends Exception {

	private static final long serialVersionUID = -8527060637239738508L;
	public BackendException(){
		super();
	}
	public BackendException(String message,Throwable throwable){
		super(message,throwable);
	}
	public BackendException(String message){
		super(message);
	}
	public BackendException(Throwable throwable){
		super(throwable);
	}
}
