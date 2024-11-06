package com.ctnf.client.uibinder.menu;

import com.google.gwt.event.shared.GwtEvent;

public class MenuEvent extends GwtEvent<MenuEventHandler> {

	public static Type<MenuEventHandler> TYPE = new Type<MenuEventHandler>();
	
	private  MenuEventContext context;
	
	public MenuEventContext getContext() {
		return context;
	}
	public void setContext(MenuEventContext context) {
		this.context = context;
	}

	public MenuEvent(MenuEventContext context){
		this.context = context;
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<MenuEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(MenuEventHandler handler) {
		handler.action(this);
	}

}
