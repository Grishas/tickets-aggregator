package com.ctnf.client.activities.generic;
import com.ctnf.client.Factory;
import com.google.gwt.place.shared.Place;

public interface GenericPresenter {
	void goTo(Place place);
	Factory getFactory();
}
