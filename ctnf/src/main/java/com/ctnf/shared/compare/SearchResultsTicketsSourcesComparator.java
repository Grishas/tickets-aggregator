package com.ctnf.shared.compare;

import java.util.Comparator;

import com.ctnc.shared.SearchResult;

public class SearchResultsTicketsSourcesComparator implements Comparator<SearchResult>{
	
	public int compare(SearchResult o1, SearchResult o2) {
		
		return o1.getSources().size() - o2.getSources().size();
	}
}
