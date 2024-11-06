package com.ctnf.shared.compare;

import java.util.Comparator;

import com.ctnc.shared.SearchResult;

public class SearchResultsPerformersComparator implements Comparator<SearchResult>{
	
	public int compare(SearchResult o1, SearchResult o2) {
		
		if(o1.getPerformersName()==null||o1.getPerformersName().size()==0)
		{
			return 1;
		}
		
		if(o2.getPerformersName()==null||o2.getPerformersName().size()==0)
		{
			return -1;
		}
		
		return o1.getPerformersName().get(0).compareTo( o2.getPerformersName().get(0) );
		
	}
}
