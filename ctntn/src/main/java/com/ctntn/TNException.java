package com.ctntn;

public class TNException extends Exception {

	private static final long serialVersionUID = -5547919012597516248L;
	
	public TNException(){
		super();
	}
	public TNException(String message,Throwable throwable){
		super(message,throwable);
	}
	public TNException(Throwable throwable){
		super(throwable);
	}
	public TNException(String message){
		super(message);
	}
}
