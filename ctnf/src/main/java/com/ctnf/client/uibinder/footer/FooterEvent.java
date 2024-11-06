package com.ctnf.client.uibinder.footer;

import com.google.gwt.event.shared.GwtEvent;

public class FooterEvent extends GwtEvent<FooterEventHandler> {

	public static Type<FooterEventHandler> TYPE = new Type<FooterEventHandler>();	
	private  FooterEventContext context;

	public FooterEvent(FooterEventContext context){
		this.context = context;
	}
	
	public FooterEventContext getContext() {
		return context;
	}

	public void setContext(FooterEventContext context) {
		this.context = context;
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<FooterEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(FooterEventHandler handler) {
		handler.process(this);
	}
}
