package com.ctnc.shared;
import java.util.List;
import java.util.Map;

import com.ctnc.GetTicketsStatus;
import com.google.gwt.user.client.rpc.GwtTransient;
import com.google.gwt.user.client.rpc.IsSerializable;

public class Tickets implements IsSerializable{
	
	private long eventId;
	private Source source;
	private String page;
	//tickets blocks by section
	private Map<String,List<TicketsBlock>> blocks = null;
	private GetTicketsStatus status;
	
	@GwtTransient
	private Long timestamp;//for tickets cash
	
	public Long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
	public GetTicketsStatus getStatus() {
		return status;
	}
	public void setStatus(GetTicketsStatus status) {
		this.status = status;
	}
	public double getEventId() {
		return eventId;
	}
	public void setEventId(long eventId) {
		this.eventId = eventId;
	}
	public Source getSource() {
		return source;
	}
	public void setSource(Source source) {
		this.source = source;
	}
	public String getPage() {
		return page;
	}
	public void setPage(String page) {
		this.page = page;
	}
	public Map<String, List<TicketsBlock>> getBlocks() {
		return blocks;
	}
	public void setBlocks(Map<String, List<TicketsBlock>> blocks) {
		this.blocks = blocks;
	}
	
	@Override
	public String toString() {
		return "Tickets [eventId=" + eventId + ", source=" + source + ", page="
				+ page + ", blocks=" + blocks + ", status=" + status + "]";
	}
	
}
