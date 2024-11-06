package com.ctnf.client;

import java.util.HashMap;
import java.util.Map;

public class EventContext {
	
	public static final String EVENT_TYPE="EVENT_TYPE";

	private Map<String,Object> context = new HashMap<String, Object>(1);

	public EventContext(Map<String, Object> contextData) {
		this.context = contextData;
	}
	
	public Map<String, Object> getContext() {
		return context;
	}
	public void setContext(Map<String, Object> context) {
		this.context = context;
	}	
	
	
	
	
}
