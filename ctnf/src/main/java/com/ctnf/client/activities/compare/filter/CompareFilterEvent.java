package com.ctnf.client.activities.compare.filter;

import com.google.gwt.event.shared.GwtEvent;

public class CompareFilterEvent extends GwtEvent<CompareFilterEventHandler> {

	public static Type<CompareFilterEventHandler> TYPE = new Type<CompareFilterEventHandler>();
	
	private CompareFilterEventContext context;

	public CompareFilterEvent(CompareFilterEventContext context) {
		super();
		this.context = context;
	}

	public CompareFilterEventContext getContext() {
		return context;
	}

	public void setContext(CompareFilterEventContext context) {
		this.context = context;
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<CompareFilterEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(CompareFilterEventHandler handler) {
		handler.process(this);
	}

}
