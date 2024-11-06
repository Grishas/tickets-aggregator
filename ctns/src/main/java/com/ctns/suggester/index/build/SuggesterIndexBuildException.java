package com.ctns.suggester.index.build;

public class SuggesterIndexBuildException extends Exception {

	private static final long serialVersionUID = -8527060637239738508L;
	public SuggesterIndexBuildException(){
		super();
	}
	public SuggesterIndexBuildException(String message,Throwable throwable){
		super(message,throwable);
	}
	public SuggesterIndexBuildException(String message){
		super(message);
	}
	public SuggesterIndexBuildException(Throwable throwable){
		super(throwable);
	}
}
