package com.ctnf.client.activities.compare.filter;
import com.google.gwt.place.shared.Place;

public interface CompareFilterPresenter {
	void goTo(Place place);
	void valueUpdated(CompareFilterValues compareFilterValues);
	void reset(CompareFilterValues compareFilterValues);
}
