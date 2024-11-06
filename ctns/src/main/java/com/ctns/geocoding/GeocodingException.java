package com.ctns.geocoding;

public class GeocodingException extends Exception {

	private static final long serialVersionUID = 7634043130126895897L;
	
	public GeocodingException(){
		super();
	}
	public GeocodingException(String message,Throwable throwable){
		super(message,throwable);
	}
	public GeocodingException(String message){
		super(message);
	}
	public GeocodingException(Throwable throwable){
		super(throwable);
	}
}
