package com.ctnf.client.activities.generic;
import org.gwtbootstrap3.client.ui.Image;
import org.gwtbootstrap3.client.ui.html.Div;

import com.ctnf.client.Resources;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class BrokerViewImpl extends Composite implements GenericView{

	private static BrokerViewImplUiBinder uiBinder = GWT.create(BrokerViewImplUiBinder.class);
	interface BrokerViewImplUiBinder extends UiBinder<Widget, BrokerViewImpl> {}

	@UiField(provided = true)
    Image imageLogoCompareTicketsNow;
	
	@UiField Div divMenu;

	private GenericPresenter presenter;
	
	public BrokerViewImpl(GenericPresenter presenter) {
		
		this.presenter  = presenter;
		
		this.imageLogoCompareTicketsNow = new Image(Resources.instance.compareticketsnow());
		
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
