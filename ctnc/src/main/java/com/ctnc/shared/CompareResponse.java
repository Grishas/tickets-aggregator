package com.ctnc.shared;
import java.util.List;
import com.google.gwt.user.client.rpc.IsSerializable;

public class CompareResponse implements IsSerializable{

	//UpdatedSince in ISO 8601 Format (Ex. 2013-06-21T05:32:07.65)
	//"2014-01-21T19:30"
	private String dateForMap;
	private String[] eventSourceName;
	private String[] eventSourceVenue;
	private CompareResponseStatus status;
	private List<Tickets> tickets = null;
	
	private String eventName;
	private String eventDate;
	private String eventVenue;
	private String eventSegmentKey;
	private String eventCity;

	private String currency;
	
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getEventName() {
		return eventName;
	}
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}
	public String getEventDate() {
		return eventDate;
	}
	public void setEventDate(String eventDate) {
		this.eventDate = eventDate;
	}
	public String getEventVenue() {
		return eventVenue;
	}
	public void setEventVenue(String eventVenue) {
		this.eventVenue = eventVenue;
	}
	public String getEventSegmentKey() {
		return eventSegmentKey;
	}
	public void setEventSegmentKey(String eventSegmentKey) {
		this.eventSegmentKey = eventSegmentKey;
	}
	public String getEventCity() {
		return eventCity;
	}
	public void setEventCity(String eventCity) {
		this.eventCity = eventCity;
	}
	public CompareResponseStatus getStatus() {
		return status;
	}
	public void setStatus(CompareResponseStatus status) {
		this.status = status;
	}
	public String getDateForMap() {
		return dateForMap;
	}
	public void setDateForMap(String dateForMap) {
		this.dateForMap = dateForMap;
	}
	public String[] getEventSourceName() {
		return eventSourceName;
	}
	public void setEventSourceName(String[] eventSourceName) {
		this.eventSourceName = eventSourceName;
	}
	public String[] getEventSourceVenue() {
		return eventSourceVenue;
	}
	public void setEventSourceVenue(String[] eventSourceVenue) {
		this.eventSourceVenue = eventSourceVenue;
	}
	public List<Tickets> getTickets() {
		return tickets;
	}
	public void setTickets(List<Tickets> tickets) {
		this.tickets = tickets;
	}
	@Override
	public String toString() {
		return "CompareResponse [tickets=" + tickets + "]";
	}
}
