package com.ctnf.client.activities.generic;

import org.gwtbootstrap3.client.ui.html.Div;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class PolicyViewImpl extends Composite implements GenericView{

	private static PolicyViewImplUiBinder uiBinder = GWT.create(PolicyViewImplUiBinder.class);
	interface PolicyViewImplUiBinder extends UiBinder<Widget, PolicyViewImpl> {}

	@UiField Div divMenu;

	private GenericPresenter presenter;
	
	public PolicyViewImpl(GenericPresenter presenter) 
	{
		this.presenter = presenter;
	
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void setMenu()
	{
		Scheduler.get().scheduleDeferred(new Command() {
			@Override
			public void execute() {
				Widget menu = presenter.getFactory().getMenuView().asWidget();
				divMenu.add(menu);
			}
		});
	}
}
