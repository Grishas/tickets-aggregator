package com.ctnf.client;
//import com.ctnf.client.activities.checkout.CheckoutPlace;
import com.ctnf.client.activities.compare.ComparePlace;
import com.ctnf.client.activities.generic.GenericPlace;
import com.ctnf.client.activities.search.SearchPlace;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;

@WithTokenizers({
	SearchPlace.Tokenizer.class,
	ComparePlace.Tokenizer.class,
	//CheckoutPlace.Tokenizer.class,
	GenericPlace.Tokenizer.class
	})
public interface AppicationPlacesHistoryMapper extends PlaceHistoryMapper{}
