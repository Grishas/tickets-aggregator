package com.ctnf.client;
import com.ctnf.client.activities.checkout.CheckoutActivity;
import com.ctnf.client.activities.checkout.CheckoutPlace;
import com.ctnf.client.activities.compare.CompareActivity;
import com.ctnf.client.activities.compare.ComparePlace;
import com.ctnf.client.activities.generic.GenericActivity;
import com.ctnf.client.activities.generic.GenericPlace;
import com.ctnf.client.activities.search.SearchActivity;
import com.ctnf.client.activities.search.SearchPlace;
import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;

public class ApplicationActivityMapper implements ActivityMapper {

	private final Factory factory;
	
	public ApplicationActivityMapper(Factory factory){
		super();
		this.factory = factory;
	}
	
	@Override
	public Activity getActivity(Place place) {
			
		if (place instanceof SearchPlace){	
			SearchPlace searchPlace = (SearchPlace)place;
			SearchActivity searchActivity = new SearchActivity(searchPlace,factory);
			return searchActivity; 
		}	
		else if (place instanceof ComparePlace){	
			//this.factory.getContainerPanlel().setWidgetSize(this.factory.getMenuPanel(), 0);
			ComparePlace comparePlace = (ComparePlace)place;
			CompareActivity compareActivity = new CompareActivity(comparePlace,factory);	
			return compareActivity;
		}
		else if (place instanceof CheckoutPlace){
			CheckoutPlace checkoutPlace = (CheckoutPlace)place;
			CheckoutActivity checkoutActivity = new CheckoutActivity(checkoutPlace,this.factory);
			return checkoutActivity;
		}
		else if(place instanceof GenericPlace ){
			GenericPlace genericPlace = (GenericPlace)place;
			GenericActivity genericActivity = new GenericActivity(genericPlace,this.factory); 
			return genericActivity;
		}
		else
		{
			return null;
		}
	}	
}