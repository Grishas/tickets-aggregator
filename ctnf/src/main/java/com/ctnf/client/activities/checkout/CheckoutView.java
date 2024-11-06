package com.ctnf.client.activities.checkout;
import com.google.gwt.user.client.ui.IsWidget;

public interface CheckoutView extends IsWidget{

	void init();
	void setPresenter(CheckoutPresenter presenter);

}
