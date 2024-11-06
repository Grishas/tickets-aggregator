package com.ctnf.client.activities.checkout;
import com.ctnf.client.Factory;
import com.google.gwt.place.shared.Place;

public interface CheckoutPresenter {
	void goTo(Place place);
	void checkout();
	String getBrokerUrl();
	Factory getFactory();
}
