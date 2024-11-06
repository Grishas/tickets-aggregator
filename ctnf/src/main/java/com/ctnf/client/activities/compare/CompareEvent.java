package com.ctnf.client.activities.compare;

import com.google.gwt.event.shared.GwtEvent;

public class CompareEvent extends GwtEvent<CompareEventHandler> {

	public static Type<CompareEventHandler> TYPE = new Type<CompareEventHandler>();
	
	private CompareEventContext context;

	public CompareEvent(CompareEventContext context) {
		super();
		this.context = context;
	}

	public CompareEventContext getContext() {
		return context;
	}

	public void setContext(CompareEventContext context) {
		this.context = context;
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<CompareEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(CompareEventHandler handler) {
		handler.process(this);
	}

}
