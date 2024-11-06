package com.ctnc.shared;

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

public abstract class Search implements IsSerializable{

	private IndexKey queryFieldName = null;
	private String query = null;
	
	private String from = null;
	private String to = null;
	
	private UserLocation currentUserLocation;
	
	private Location searchLocation;
	
	//this value indicate about search request related to location 
	private QueryLocationIndex howToSearchLocation;

	public IndexKey getQueryFieldName() {
		return queryFieldName;
	}

	public void setQueryFieldName(IndexKey queryFieldName) {
		this.queryFieldName = queryFieldName;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public UserLocation getCurrentUserLocation() {
		return currentUserLocation;
	}

	public void setCurrentUserLocation(UserLocation currentUserLocation) {
		this.currentUserLocation = currentUserLocation;
	}

	public Location getSearchLocation() {
		return searchLocation;
	}

	public void setSearchLocation(Location searchLocation) {
		this.searchLocation = searchLocation;
	}

	public QueryLocationIndex getHowToSearchLocation() {
		return howToSearchLocation;
	}

	public void setHowToSearchLocation(QueryLocationIndex howToSearchLocation) {
		this.howToSearchLocation = howToSearchLocation;
	}

	@Override
	public String toString() {
		return "Search [queryFieldName=" + queryFieldName + ", query=" + query + ", from=" + from + ", to=" + to
				+ ", currentUserLocation=" + currentUserLocation + ", searchLocation=" + searchLocation
				+ ", howToSearchLocation=" + howToSearchLocation + "]";
	}


	
}
