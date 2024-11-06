package com.ctns.collect.index.build;

public class LocationMappingRule {

	private LocationRuleAction action = null;
	private String value = null;
	
	public LocationRuleAction getAction() {
		return action;
	}
	public void setAction(LocationRuleAction action) {
		this.action = action;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return "LocationMappingRule [action=" + action + ", value=" + value
				+ "]";
	}
}
