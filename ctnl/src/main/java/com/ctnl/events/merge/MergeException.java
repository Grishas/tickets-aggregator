package com.ctnl.events.merge;

public class MergeException extends Exception {

	private static final long serialVersionUID = 8018876818846324273L;
	public MergeException(){
		super();
	}
	public MergeException(String message,Throwable throwable){
		super(message,throwable);
	}
	public MergeException(String message){
		super(message);
	}
	public MergeException(Throwable throwable){
		super(throwable);
	}
}
