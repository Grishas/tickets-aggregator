package com.ctnf.client.activities.search.suggestion;
import com.ctnc.shared.SuggestionRequest;
import com.ctnf.client.Factory;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.SuggestOracle;

/*An extended SuggestOracle that makes the RPC calls for getting suggestions*/
/*https://groups.google.com/forum/#!topic/google-web-toolkit/VpQq_gxATYY

Although being old, things in that post still applies. 
In particular:p
use a single connection to avoid explosion of requests, 
and left free the other ones for other tasks (i.e. avoid to use all the 2-to-8 max parallel browser http connections);
reuse data from a previous requests (i.e., 
if your request is a substring of the previous one, 
you may already have the suggestions, hence just filter them client-side).
Other things that come to my mind are:

use a Timer to simulate a little delay in case of fast writers, so you call the server only after 
a bit (probably an over optimization, but still an idea);

allow to fetch suggestions only on a minimum input length (say, min 3 characters). 
If you have a lot of possible suggestions, the data returned might be expensive even to parse, 
specially if - for the search - you decide to adopt a contains instead of startswith strategy;

in case you still have tons of suggestions, you could try to implement a lazy load 
SuggestionDisplay that simply show you the first, say, 50 suggestions and then, 
on scroll, all the others in an incremental way using the same input string.

*/
public class SuggestionOracleSearch extends SuggestOracle {
	
	private Factory factory;
	
	public SuggestionOracleSearch(Factory factory)
	{
		super();
		this.factory = factory;
	}
	
	public boolean isDisplayStringHTML() 
	{ 
		return true; 
	}

	@Override
	public void requestSuggestions(final Request request, final Callback callback) 
	{
		SuggestionRequest suggestionRequest =
				new SuggestionRequest(request,this.factory.runOnSuggesterIndex());
		
		GWT.log("Send suggester request to server with query: "+request.getQuery()+".Index: "+this.factory.runOnSuggesterIndex());	

		this.factory.getSuggestionService().getSuggestions( suggestionRequest , new SuggestionCallbackSearch( request , callback ) );		
	}
}







