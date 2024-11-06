package com.ctnu;

public class UtilsException extends Exception {

	private static final long serialVersionUID = -5547919012597516248L;
	
	public UtilsException(){
		super();
	}
	public UtilsException(String message,Throwable throwable){
		super(message,throwable);
	}
	public UtilsException(String message){
		super(message);
	}
	public UtilsException(Throwable throwable){
		super(throwable);
	}
}
