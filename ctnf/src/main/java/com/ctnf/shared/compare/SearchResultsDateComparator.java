package com.ctnf.shared.compare;

import java.util.Comparator;

import com.ctnc.shared.SearchResult;

public class SearchResultsDateComparator implements Comparator<SearchResult>{
	
	public int compare(SearchResult o1, SearchResult o2) {
		return o1.getDate().compareTo(o2.getDate());
	}
}
