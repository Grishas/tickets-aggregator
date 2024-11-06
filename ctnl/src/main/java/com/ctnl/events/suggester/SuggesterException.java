package com.ctnl.events.suggester;

public class SuggesterException extends Exception {

	private static final long serialVersionUID = 8018876818846324273L;
	public SuggesterException(){
		super();
	}
	public SuggesterException(String message,Throwable throwable){
		super(message,throwable);
	}
	public SuggesterException(String message){
		super(message);
	}
	public SuggesterException(Throwable throwable){
		super(throwable);
	}
}
