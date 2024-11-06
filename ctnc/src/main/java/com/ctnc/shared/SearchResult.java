package com.ctnc.shared;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;

public class SearchResult implements IsSerializable{
	
	private int internalEventId;
	private String eventName;
	private List<String> relatedCityNames = new ArrayList<String>(1);
	private String segmentKey;
	//private boolean isFinalTime;
	private String date;
	private Map<Source,Long> sources;
	private List<String> performersName = new ArrayList<String>(2);
	private String venueName;
	private double latitude=0d;
	private double longitude=0d;
	
	//delete me
	private String test;
	
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public List<String> getRelatedCityNames() {
		return relatedCityNames;
	}
	public void setRelatedCityNames(List<String> relatedCityNames) {
		this.relatedCityNames = relatedCityNames;
	}
	public List<String> getPerformersName() {
		return performersName;
	}
	public void setPerformersName(List<String> performersName) {
		this.performersName = performersName;
	}
	public String getVenueName() {
		return venueName;
	}
	public void setVenueName(String venueName) {
		this.venueName = venueName;
	}
	
	public Map<Source, Long> getSources() {
		return sources;
	}
	public void setSources(Map<Source, Long> sources) {
		this.sources = sources;
	}
	
	public int getInternalEventId() {
		return internalEventId;
	}
	public void setInternalEventId(int internalEventId) {
		this.internalEventId = internalEventId;
	}
	public String getSegmentKey() {
		return segmentKey;
	}
	public void setSegmentKey(String segmentKey) {
		this.segmentKey = segmentKey;
	}
//	public boolean isFinalTime() {
//		return isFinalTime;
//	}
//	public void setFinalTime(boolean isFinalTime) {
//		this.isFinalTime = isFinalTime;
//	}
	public String getTest() {
		return test;
	}
	public void setTest(String test) {
		this.test = test;
	}
	public String getEventName() {
		return eventName;
	}
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	
}
