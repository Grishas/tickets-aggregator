package com.ctns.query;

public class QueryBuilderException extends Exception {

	private static final long serialVersionUID = 8018876818846324273L;
	public QueryBuilderException(){
		super();
	}
	public QueryBuilderException(String message,Throwable throwable){
		super(message,throwable);
	}
	public QueryBuilderException(String message){
		super(message);
	}
	public QueryBuilderException(Throwable throwable){
		super(throwable);
	}
}
