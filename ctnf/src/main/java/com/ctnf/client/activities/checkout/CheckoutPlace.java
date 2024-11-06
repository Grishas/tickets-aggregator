package com.ctnf.client.activities.checkout;
import java.util.HashMap;
import java.util.Map;

import com.ctnc.shared.Source;
import com.ctnf.client.activities.generic.GenericPlace.Tokenizer;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class CheckoutPlace extends Place {
		
			private static final String PREFIX     	= "!checkout";
			private static final String SOURCE 		= "source";
			private static final String EVENT_ID 	= "event-id";
			private static final String QUANTITY 	= "quantity";
			private static final String TICKETS_BLOCK_ID 	= "tickets-block-id";
			private static final String EQ         	= "=";
			private static final String AMPS       	= "&";
			
			private Source source = null;
			private double eventId = 0;
			private double ticketsBlockId = 0;
			private int quantity = 0;
			
			@Override
			public String toString() {
				return "CheckoutPlace [source=" + source + ", eventId="
						+ eventId + ", ticketsBlockId=" + ticketsBlockId
						+ ", quantity=" + quantity + "]";
			}

			public CheckoutPlace(Source source,double eventId,double ticketsBlockId,int quantity) {
				this.source = source;
				this.eventId = eventId;
				this.quantity = quantity;
				this.ticketsBlockId = ticketsBlockId;
			}
			
			public double getTicketsBlockId() {
				return ticketsBlockId;
			}
			public void setTicketsBlockId(double ticketsBlockId) {
				this.ticketsBlockId = ticketsBlockId;
			}
			public Source getSource() {
				return source;
			}
			public void setSource(Source source) {
				this.source = source;
			}

			public double getEventId() {
				return eventId;
			}
			public void setEventId(double eventId) {
				this.eventId = eventId;
			}
			public int getQuantity() {
				return quantity;
			}
			public void setQuantity(int quantity) {
				this.quantity = quantity;
			}
			
			public String getUrl()
			{
				Tokenizer tokenizer = new Tokenizer();
				StringBuilder url = new StringBuilder(); 
				//url.append("compareticketsnow.com");
				url.append(com.google.gwt.core.client.GWT.getHostPageBaseURL());
				url.append("#"+PREFIX+":");
				url.append(tokenizer.getToken(this));		
				return url.toString();
			}

			@Prefix(PREFIX)
			public static class Tokenizer implements PlaceTokenizer<CheckoutPlace>
			{
				@Override
				public CheckoutPlace getPlace(String token) {
					return this.toCheckoutPlace( token );
				}

				@Override
				public String getToken(CheckoutPlace place) {
					return SOURCE   		+ EQ + place.getSource().name()  	+ AMPS + 
						   EVENT_ID 		+ EQ + place.getEventId() 			+ AMPS + 
						   TICKETS_BLOCK_ID + EQ + place.getTicketsBlockId() 	+ AMPS + 
						   QUANTITY 		+ EQ + place.getQuantity();
				}
				
				private CheckoutPlace toCheckoutPlace(String token)
				{
					Map<String,String> parameters = this.parse( token );
					
					Source source = Source.valueOf(parameters.get(SOURCE));
					
					double eventId = Long.valueOf(parameters.get(EVENT_ID));
					
					double ticketsBlockId = Long.valueOf(parameters.get(TICKETS_BLOCK_ID));
					
					int quantity = Integer.valueOf(parameters.get(QUANTITY));
					
					return  new CheckoutPlace(source,eventId,ticketsBlockId,quantity);
				}
				
				private final Map<String, String> parse(String token) {

				    Map<String, String> map = new HashMap<String, String>();
				    
				    if (token != null) 
				    {
				        String[] parameters = token.split(AMPS);
				        
				        for (String parameter : parameters) 
				        {
				            String[] keyValue = parameter.split(EQ);
				            
				            if (keyValue.length > 1) 
				            {
				                map.put(keyValue[0], keyValue[1]);
				            }
				        }
				    }
				    
				    return map;
				}
			}
	}
