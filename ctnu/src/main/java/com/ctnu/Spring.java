package com.ctnu;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Spring {

	private static AnnotationConfigApplicationContext context = null;
	
	public static void set(AnnotationConfigApplicationContext context){
		Spring.context = context;
	}
	
	public static AnnotationConfigApplicationContext context(){
		return Spring.context;
	}
}
