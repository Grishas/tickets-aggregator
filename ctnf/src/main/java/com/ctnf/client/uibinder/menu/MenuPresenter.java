package com.ctnf.client.uibinder.menu;
import com.ctnf.client.Factory;
import com.google.gwt.place.shared.Place;

public interface MenuPresenter {
	void goTo(Place place);
	public Factory getFactory();
}
