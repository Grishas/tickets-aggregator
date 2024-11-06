package com.ctnf.shared.compare;

import java.util.Comparator;

import com.ctnc.shared.SearchResult;

public class SearchResultsEventComparator implements Comparator<SearchResult>{
	
	public int compare(SearchResult o1, SearchResult o2) {
		
		if (o1 == o2) 
		{
			return 0;
		}

		if (o1 != null) 
		{
			return (o2 != null) ? o1.getEventName().compareTo( o2.getEventName() ) : 1;
		}
		
		return -1;
	}
}
