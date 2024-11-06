package com.ctnf.client.activities.checkout;
import com.ctnf.client.Factory;
import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Frame;

public class CheckoutActivity extends AbstractActivity implements CheckoutPresenter{

	private final Factory factory;
	private CheckoutView view = null;
	private CheckoutPlace place = null; 

	private final static String TICKETBLOCK_ID 			= "{TICKETBLOCK_ID}";
	private final static String QUANTITY				= "{QUANTITY}";
	private final static String AFFILIATE_CUSTOM_VALUE 	= "{AFFILIATE_CUSTOM_VALUE}";
	private final static String TREQ 					= "{TREQ}";
	private final static String EVENT_ID 				= "{EVENT_ID}";
	private final static String SESSION_ID 				= "{SESSION_ID}";
	private final static String PPC_SRC 				= "{PPC_SRC}";//used by tickets network .... check what mean?
	
	private final static String ticketcityCheckoutUrlTemplate 
	="https://secure.ticketcity.com/checkout.aspx?tbid={TICKETBLOCK_ID}&qty={QUANTITY}&aid=2887&acv={AFFILIATE_CUSTOM_VALUE}&treq={TREQ}";
	
	private final static String ticketnetworkCheckoutUrlTemplate 
	="https://tickettransaction2.com/mobile2/Checkout.aspx?"
	+ "brokerid=7919&sitenumber=0&tgid={TICKETBLOCK_ID}&evtID={EVENT_ID}&SessionID={SESSION_ID}&ppcsrc={PPC_SRC}&treq={QUANTITY}";

	/*
	https://tickettransaction2.com/mobile2/Checkout.aspx?
		brokerid=3320&
		sitenumber=0&
		tgid=1756457773&
		evtID=2739074&
		SessionID=UK100&
		ppcsrc=E4014675158214T4023754487679P51113S400363784755&
		treq=1
	 */
	
	public CheckoutActivity(CheckoutPlace place, final Factory factory){
		this.factory = factory;
		this.place = place;
	}
	
	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		
		this.view = this.factory.getCheckoutView(this);
		
		panel.setWidget(this.view.asWidget());
		
		GWT.log(this.place.toString());
		
		this.view.init();
		
		this.factory.setAddThis(this.place.getUrl(), "Thanks you for visiting our site - compareticketsnow.com");
		
	}

	private String getUrl()
	{
		switch (place.getSource()) 
		{
			case ticketcity:
				return this.generateTicketCityCheckoutUrl();
			case ticketnetwork:
				return this.generateTicketNetwokCheckoutUrl();
		}
		return null;
	}
	
	
	@Override
	public void checkout() {
		
		final String checkoutUrl = this.getUrl();

		GWT.log(checkoutUrl);

		this.factory.getCheckoutService().checkout(checkoutUrl, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {

				GWT.log("Can't report checkout", caught);

				Window.Location.assign(checkoutUrl);

				return;
			}

			@Override
			public void onSuccess(Void result) {

				GWT.log("Checkout reported");
				
				final JavaScriptObject window = newWindow("", "_blank", "");
				//final JavaScriptObject window = newWindow("", "", "");
				
				setWindowTarget(window, checkoutUrl);

				return;
			}
		});
	}

	private static native void setWindowTarget(JavaScriptObject window, String target)/*-{
    	window.location = target;
    	window.focus();
    	
	}-*/;
	
	private static native JavaScriptObject newWindow(String url, String name, String features)/*-{
    	var window = $wnd.open(url, name, features);
    	return window;
	}-*/;
	
	
	private String generateTicketCityCheckoutUrl()
	{
		String checkoutUrl = new String(ticketcityCheckoutUrlTemplate);
		checkoutUrl = checkoutUrl.replace(TICKETBLOCK_ID,String.valueOf(this.place.getTicketsBlockId()));
		checkoutUrl = checkoutUrl.replace(QUANTITY,String.valueOf(this.place.getQuantity()));
		checkoutUrl = checkoutUrl.replace(AFFILIATE_CUSTOM_VALUE,String.valueOf(System.currentTimeMillis()));
		checkoutUrl = checkoutUrl.replace(TREQ,String.valueOf(this.place.getQuantity()));
		return checkoutUrl;
	}

	private String generateTicketNetwokCheckoutUrl()
	{
/*		private final static String ticketnetworkCheckoutUrlTemplate 
		="https://tickettransaction2.com/mobile2/Checkout.aspx?"
		+ "brokerid=7919&sitenumber=0&tgid={TICKETBLOCK_ID}&evtID={EVENT_ID}&SessionID={SESSION_ID}&ppcsrc={PPC_SRC}&treq={QUANTITY}";
*/

		String checkoutUrl = new String(ticketnetworkCheckoutUrlTemplate);
		
		checkoutUrl = checkoutUrl.replace(TICKETBLOCK_ID,String.valueOf(this.place.getTicketsBlockId()));
		checkoutUrl = checkoutUrl.replace(EVENT_ID,String.valueOf(this.place.getEventId()));
		checkoutUrl = checkoutUrl.replace(SESSION_ID,this.generateSessionId());
		checkoutUrl = checkoutUrl.replace(PPC_SRC,"check");		
		checkoutUrl = checkoutUrl.replace(QUANTITY,String.valueOf(this.place.getQuantity()));
	
		return checkoutUrl;
	}

	@Override
	public void goTo(Place place) {
		this.factory.getPlaceController().goTo(place);
	}

	@Override
	public String getBrokerUrl() {
		return this.place.getSource().getName();
	}

	@Override
	public Factory getFactory() {
		return this.factory;
	}
	
	private native String generateSessionId() /*-{
	
	var chars = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz';  	var sid_length = 5; 	var sid = ''; 	
	
	for (var i=0; i<sid_length; i++) 
	{ 		
		var rnum = Math.floor(Math.random() * chars.length);
 		sid += chars.substring(rnum,rnum+1); 	
	} 	
	
	return sid;

	}-*/;
	
	
	
	
	
	
	
	
	
	
	

}