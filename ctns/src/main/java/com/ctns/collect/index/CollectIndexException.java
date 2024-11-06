package com.ctns.collect.index;

public class CollectIndexException extends Exception {

	private static final long serialVersionUID = -8527060637239738508L;
	public CollectIndexException(){
		super();
	}
	public CollectIndexException(String message,Throwable throwable){
		super(message,throwable);
	}
	public CollectIndexException(String message){
		super(message);
	}
	public CollectIndexException(Throwable throwable){
		super(throwable);
	}
}
