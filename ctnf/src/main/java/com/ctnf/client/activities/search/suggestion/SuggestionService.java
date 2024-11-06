package com.ctnf.client.activities.search.suggestion;
import com.ctnc.shared.SuggestionRequest;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.ui.SuggestOracle;

@RemoteServiceRelativePath("suggestion")
public interface SuggestionService extends RemoteService {
	
	/***
	 * auto complete in search box  
	 * @param request
	 * @return
	 */
	SuggestOracle.Response getSuggestions(SuggestionRequest request);	
}
