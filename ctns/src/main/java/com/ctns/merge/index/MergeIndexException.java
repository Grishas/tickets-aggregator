package com.ctns.merge.index;

public class MergeIndexException extends Exception {

	private static final long serialVersionUID = -8527060637239738508L;
	public MergeIndexException(){
		super();
	}
	public MergeIndexException(String message,Throwable throwable){
		super(message,throwable);
	}
	public MergeIndexException(String message){
		super(message);
	}
	public MergeIndexException(Throwable throwable){
		super(throwable);
	}
}
