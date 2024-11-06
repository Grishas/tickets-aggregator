package com.ctnl.extract.fs;

public class ExtractException extends Exception {

	private static final long serialVersionUID = 4762421447309101180L;

	public ExtractException(){
		super();
	}
	public ExtractException(String message,Throwable throwable){
		super(message,throwable);
	}
	public ExtractException(String message){
		super(message);
	}
	public ExtractException(Throwable throwable){
		super(throwable);
	}
}
