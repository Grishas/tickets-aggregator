package com.ctnc.shared;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class SearchResponse extends Search implements IsSerializable{

	private int totalResults = -1;
	
	private List<SearchResult> results = new ArrayList<SearchResult>();
	
	private QueryLocationIndex howToSearchLocationResolved;

	//Did you mean: justin bieber
	
	private String suggested = null;
	
	private boolean mokeIp;
	
	public boolean isMokeIp() {
		return mokeIp;
	}

	public void setMokeIp(boolean mokeIp) {
		this.mokeIp = mokeIp;
	}

	public String getSuggested() {
		return suggested;
	}

	public void setSuggested(String suggested) {
		this.suggested = suggested;
	}

	public int getTotalResults() {
		return totalResults;
	}

	public void setTotalResults(int totalResults) {
		this.totalResults = totalResults;
	}

	public List<SearchResult> getResults() {
		return results;
	}

	public void setResults(List<SearchResult> results) {
		this.results = results;
	}

	public QueryLocationIndex getHowToSearchLocationResolved() {
		return howToSearchLocationResolved;
	}

	public void setHowToSearchLocationResolved(QueryLocationIndex howToSearchLocationResolved) {
		this.howToSearchLocationResolved = howToSearchLocationResolved;
	}

	@Override
	public String toString() {
		return "SearchResponse [totalResults=" + totalResults + ", results=" + results
				+ ", howToSearchLocationResolved=" + howToSearchLocationResolved + "]";
	}
	
	

}
