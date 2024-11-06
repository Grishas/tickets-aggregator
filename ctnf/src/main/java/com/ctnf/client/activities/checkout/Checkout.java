package com.ctnf.client.activities.checkout;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.Window;

public class Checkout {

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

	private CheckoutPlace place = null;
	public Checkout(CheckoutPlace place)
	{
		this.place = place;
	}
	
	
	public String getUrl()
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
		
	public void checkout() {
		
		final String checkoutUrl = this.getUrl();

		GWT.log(checkoutUrl);

		Window.Location.assign(checkoutUrl);
	}

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
	
	private native String generateSessionId() /*-{
	
	var chars = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz'; 
	var sid_length = 5;
	var sid = '';	
	
	for (var i=0; i<sid_length; i++) 
	{
		var rnum = Math.floor(Math.random() * chars.length);
		sid += chars.substring(rnum,rnum+1);	
	}
	
	return sid;

	}-*/;

	
}
