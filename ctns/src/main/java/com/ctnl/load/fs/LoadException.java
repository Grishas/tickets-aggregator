package com.ctnl.load.fs;

public class LoadException extends Exception {

	private static final long serialVersionUID = 4762421447309101180L;

	public LoadException(){
		super();
	}
	public LoadException(String message,Throwable throwable){
		super(message,throwable);
	}
	public LoadException(String message){
		super(message);
	}
	public LoadException(Throwable throwable){
		super(throwable);
	}
}
