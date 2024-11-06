package com.ctns.collect.index.build;

public class CollectIndexBuildException extends Exception {

	private static final long serialVersionUID = -8527060637239738508L;
	public CollectIndexBuildException(){
		super();
	}
	public CollectIndexBuildException(String message,Throwable throwable){
		super(message,throwable);
	}
	public CollectIndexBuildException(String message){
		super(message);
	}
	public CollectIndexBuildException(Throwable throwable){
		super(throwable);
	}
}
