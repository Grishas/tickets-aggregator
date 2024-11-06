package com.ctns.merge.index.build;

public class MergeIndexBuildException extends Exception {

	private static final long serialVersionUID = -8527060637239738508L;
	public MergeIndexBuildException(){
		super();
	}
	public MergeIndexBuildException(String message,Throwable throwable){
		super(message,throwable);
	}
	public MergeIndexBuildException(String message){
		super(message);
	}
	public MergeIndexBuildException(Throwable throwable){
		super(throwable);
	}
}
