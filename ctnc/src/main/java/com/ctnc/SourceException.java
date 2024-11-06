package com.ctnc;

public class SourceException extends Exception {

	private static final long serialVersionUID = -5547919012597516248L;
	
	public SourceException(){
		super();
	}
	public SourceException(String message,Throwable throwable){
		super(message,throwable);
	}
	public SourceException(Throwable throwable){
		super(throwable);
	}
	public SourceException(String message){
		super(message);
	}
}
