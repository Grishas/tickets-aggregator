package com.ctnf.client.activities.search.suggestion;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.SuggestOracle;

public class SuggestionCallbackSearch implements AsyncCallback<SuggestOracle.Response> {

	 private SuggestOracle.Request request;
     private SuggestOracle.Callback callback;

     public SuggestionCallbackSearch(SuggestOracle.Request request,SuggestOracle.Callback callback)
     {
         this.request = request;
         this.callback = callback;
     }

     public void onSuccess(SuggestOracle.Response response) 
     {
    	 GWT.log("SuggestOracle.Response.getSuggestions size is : "+response.getSuggestions().size());	 
         callback.onSuggestionsReady(request,response);
     }
     
     public void onFailure(Throwable error) 
     {
         callback.onSuggestionsReady(request, new SuggestOracle.Response());
     }

    
}
