package com.ctnf.client.activities.search;
import com.ctnc.shared.IndexKey;
import com.ctnc.shared.SearchResponse;
import com.google.gwt.user.client.ui.IsWidget;

public interface SearchView extends IsWidget{
	
	void setSearchPresenter(SearchPresenter presenter);
	void init(SearchPlace place);	
	void update(SearchResponse result, String takeTime, int rangeStart, String url);
	IndexKey getIndexKey(String searchBy);
	void goToSearchResultsPlace();
	void waiting(boolean show);
	void addMenu();

}
