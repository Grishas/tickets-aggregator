package com.ctnc.shared;
import java.util.List;
import java.util.Map;
import com.google.gwt.user.client.rpc.IsSerializable;

public class CompareRequest implements IsSerializable{
	
	private Map<Source,Long> requests = null;
	private String eventName = null;
	private String date = null;
	private List<String> citiesClose = null;
	private String segmentKey = null;
	private String venueName = null;
	
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

	public List<String> getCitiesClose() {
		return citiesClose;
	}

	public void setCitiesClose(List<String> citiesClose) {
		this.citiesClose = citiesClose;
	}

	public String getSegmentKey() {
		return segmentKey;
	}

	public void setSegmentKey(String segmentKey) {
		this.segmentKey = segmentKey;
	}

	public String getVenueName() {
		return venueName;
	}

	public void setVenueName(String venueName) {
		this.venueName = venueName;
	}

	public Map<Source, Long> getRequests() {
		return requests;
	}

	public void setRequests(Map<Source, Long> requests) {
		this.requests = requests;
	}

	@Override
	public String toString() {
		return "CompareRequest [requests=" + requests + "]";
	}	
}
