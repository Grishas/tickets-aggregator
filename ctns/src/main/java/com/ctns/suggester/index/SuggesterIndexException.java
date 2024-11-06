package com.ctns.suggester.index;

public class SuggesterIndexException extends Exception {

	private static final long serialVersionUID = -8527060637239738508L;
	public SuggesterIndexException(){
		super();
	}
	public SuggesterIndexException(String message,Throwable throwable){
		super(message,throwable);
	}
	public SuggesterIndexException(String message){
		super(message);
	}
	public SuggesterIndexException(Throwable throwable){
		super(throwable);
	}
}
