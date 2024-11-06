package com.ctnf.client.uibinder.menu;
import com.ctnf.client.Factory;
import com.google.gwt.place.shared.Place;

public class Menu implements MenuPresenter{

	private final Factory factory;
	private final MenuView view;
	
	public Menu(Factory factory) 
	{	
		this.factory = factory;	
		this.view = this.factory.getMenuView();
		this.view.setMenuPresenter(this);
	}
	
	@Override
	public void goTo(Place place) {
		this.factory.getPlaceController().goTo(place);
	}

	@Override
	public Factory getFactory() {
		return this.factory;
	}
}
