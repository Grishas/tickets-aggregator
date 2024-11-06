package com.ctnc;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ctnc.shared.Base;
import com.ctnc.shared.Performer;
import com.ctnc.shared.Source;

public class Event extends Base{

	private long internalId = -1;
	
	private String segmentKey; 
	
	private Source source;
	
	private List<Performer> performers = new ArrayList<>(1);
	
	private Venue venue;
	
	private Location location;

	//event date time
	private Date date; 
	private String displayDate; 
	private boolean finalTime;
	private String ticketUtilsMapDate; 
	
	private Category category;
	
	private List<Event> moreSources = null;
	
	public Event(){}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (internalId ^ (internalId >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Event other = (Event) obj;
		if (internalId != other.internalId)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Event [internalId=" + internalId + ", segmentKey=" + segmentKey
				+ ", source=" + source + ", performers=" + performers
				+ ", venue=" + venue + ", location=" + location + ", date="
				+ date + ", displayDate=" + displayDate + ", finalTime="
				+ finalTime + ", category=" + category + ", moreSources="
				+ moreSources + ", toString()=" + super.toString() + "]";
	}

	

	public String getTicketUtilsMapDate() {
		return ticketUtilsMapDate;
	}

	public void setTicketUtilsMapDate(String ticketUtilsMapDate) {
		this.ticketUtilsMapDate = ticketUtilsMapDate;
	}

	public boolean isFinalTime() {
		return finalTime;
	}

	public void setFinalTime(boolean finalTime) {
		this.finalTime = finalTime;
	}

	public String getSegmentKey() {
		return segmentKey;
	}

	public void setSegmentKey(String segmentKey) {
		this.segmentKey = segmentKey;
	}

	public long getInternalId() {
		return internalId;
	}

	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

	public List<Event> getMoreSources() {
		return moreSources;
	}

	public void setMoreSources(List<Event> moreSources) {
		this.moreSources = moreSources;
	}

	public Source getSource() {
		return source;
	}

	public void setSource(Source source) {
		this.source = source;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public List<Performer> getPerformers() {
		return performers;
	}

	public void setPerformers(List<Performer> performers) {
		this.performers = performers;
	}

	public Venue getVenue() {
		return venue;
	}

	public void setVenue(Venue venue) {
		this.venue = venue;
	}

	
	public Date getDate() {
		return date;
	}


	public void setDate(Date date) {
		this.date = date;
	}


	public String getDisplayDate() {
		return displayDate;
	}


	public void setDisplayDate(String displayDate) {
		this.displayDate = displayDate;
	}


	public Category getCategory() {
		return category;
	}


	public void setCategory(Category category) {
		this.category = category;
	}
}
