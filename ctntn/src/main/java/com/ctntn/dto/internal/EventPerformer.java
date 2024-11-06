package com.ctntn.dto.internal;

public class EventPerformer {

    private long eventID = 0;
    private long performerID = 0;
    private String performerName = null;
    
	public long getEventID() {
		return eventID;
	}
	public void setEventID(long eventID) {
		this.eventID = eventID;
	}
	public long getPerformerID() {
		return performerID;
	}
	public void setPerformerID(long performerID) {
		this.performerID = performerID;
	}
	public String getPerformerName() {
		return performerName;
	}
	public void setPerformerName(String performerName) {
		this.performerName = performerName;
	}
    
    
	
}
