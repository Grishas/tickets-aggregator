package com.ctnf.client.activities.generic;

import org.gwtbootstrap3.client.ui.html.Div;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class ErrorViewImpl extends Composite implements GenericView{

	private static ErrorViewImplUiBinder uiBinder = GWT.create(ErrorViewImplUiBinder.class);
	interface ErrorViewImplUiBinder extends UiBinder<Widget, ErrorViewImpl> {}

	@UiField Div divMenu;

	private GenericPresenter presenter;
	
	public ErrorViewImpl(GenericPresenter presenter) 
	{
		initWidget(uiBinder.createAndBindUi(this));
		
		this.presenter = presenter;
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
