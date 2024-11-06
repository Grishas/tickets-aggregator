package com.ctnf.client.activities.search;
import java.util.List;
import org.gwtbootstrap3.client.ui.Pagination;
import com.ctnc.shared.SearchRequest;
import com.ctnc.shared.SearchResponse;
import com.ctnc.shared.SearchResult;
import com.ctnf.client.Factory;
import com.ctnf.client.activities.generic.GenericPlace;
import com.ctnf.client.activities.generic.Page;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;

public class SearchAsyncDataProvider extends AsyncDataProvider<SearchResult> {

	private SimplePager simplePager = null;
	private Pagination pagination = null;
	private SearchPresenter presenter = null;
	private SearchView view = null;
	private SearchPlace place;
	private final Factory factory;
	
	public SearchAsyncDataProvider(
			SearchView view,
			SimplePager simplePager, 
			Pagination pagination, 
			SearchPresenter presenter,
			SearchPlace place,
			Factory factory) {
		this.view = view;
		this.simplePager = simplePager;
		this.pagination = pagination;
		this.presenter = presenter;
		this.place = place;
		this.factory = factory;
	}

	/**
	 * {@link #onRangeChanged(HasData)} is called when the table requests a new
	 * range of data. You can push data back to the displays using
	 * {@link #updateRowData(int, List)}.
	 */
	@Override
	protected void onRangeChanged(HasData<SearchResult> display) {

		
		
		
		
		GWT.log("@@@onRangeChanged@@@ "+System.currentTimeMillis());
		
		// Get the new range.
		final Range range = display.getVisibleRange();
		
		SearchRequest request = 
				this.generateSearchRequest(range.getLength(),range.getStart());
		
		final float start = System.currentTimeMillis();
		
		this.factory.getSearchService().search(request,new AsyncCallback<SearchResponse>() {
			// Now we update the row data with that result starting 
			// at a particular row in the cell widget (usually the range start) 
			public void onSuccess(SearchResponse response) {
				
				//in case that no results for search
				//if(result.getResults().size()==0){}
				
				updateRowCount(response.getTotalResults(), true);
			
				updateRowData(range.getStart(), response.getResults());
				
				pagination.rebuild(simplePager);
				
				float end = System.currentTimeMillis();
				
				float time = ((end-start)/1000);
				
				setLocation(response);
				
				view.update(
						response,
						String.valueOf(time),
						range.getStart(),
						place.getUrl());

			}
			
			public void onFailure(Throwable caught) {
				view.waiting(false);
				presenter.goTo(new GenericPlace(Page.error,caught));
			}
		});		
	}
	
	private SearchRequest generateSearchRequest(int rangeLength,int rangeStart)
	{
		SearchRequest request = new SearchRequest();
		
		//set range
		request.setRangeLength(rangeLength);
		request.setRangeStart(rangeStart);
		
		//event , performer or venue
		request.setQueryFieldName(this.place.getQueryFieldName());
		request.setQuery(this.place.getQuery());
		
		//dates 
		request.setFrom(this.place.getFrom());
		request.setTo(this.place.getTo());

		//location
		this.setLocation(request);
				
		GWT.log(place.toString());

		return request;
	}
	
	//set location to SearchRequest
	private void setLocation(SearchRequest request)
	{
		request.setSearchLocation(this.place.getLocation());
		request.setHowToSearchLocation(this.place.getQueryLocationIndex());

		request.setCurrentUserLocation(this.factory.getUserLocation());
	}
	
	//set location to factory from SearchResponse
	private void setLocation(SearchResponse response)
	{
		
		//in case that user provide lat/lon then override ip resolution
//		if(this.factory.getUserLocation()!=null 
//		&& this.factory.getUserLocation().getLatitude()!=0
//		&& this.factory.getUserLocation().getLongitude()!=0
//		&& response.isMokeIp()==false)
//		{
//			response.getCurrentUserLocation().setLatitude(this.factory.getUserLocation().getLatitude());
//			response.getCurrentUserLocation().setLongitude(this.factory.getUserLocation().getLongitude());
//		}
		this.factory.setUserLocation(response.getCurrentUserLocation());
		
		
		
		
		//how user decide to search location
		//user in New York, bus search in LA
		this.factory.setSearchLocation(response.getSearchLocation());
				
	}
}
