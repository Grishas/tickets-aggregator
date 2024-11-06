package com.ctnf.server;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.log4j.Logger;
import org.apache.lucene.search.suggest.Lookup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.ctnb.BackendException;
import com.ctnb.BackendServices;
import com.ctnc.shared.SuggestionRequest;
import com.ctnf.client.activities.search.suggestion.SuggestionSearch;
import com.ctnf.client.activities.search.suggestion.SuggestionService;
import com.ctnu.Spring;
import com.google.gwt.user.client.ui.SuggestOracle.Response;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class SuggestionServiceImpl extends RemoteServiceServlet implements SuggestionService {

	private Logger logger = Logger.getLogger(SuggestionServiceImpl.class);

	private static final long serialVersionUID = 2078647704422920310L;
	
//	public void init(ServletConfig config) throws ServletException {
//        super.init(config);
//        
//        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this,config.getServletContext());
//         
//        //SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
//
//      }
	
	
	private BackendServices backendServices = Spring.context().getBean(BackendServices.class);
	
	@Override
	public Response getSuggestions(SuggestionRequest request) {
				
		this.logger.trace("Suggester index number="+request.getRunOnSuggesterIndex().name()+" query : "+request.getQuery()+",max limit="
		+ this.backendServices.getSuggesterMaxNumberOfItemsToReturn());
		
		List<Suggestion> suggestions = new ArrayList<Suggestion>( this.backendServices.getSuggesterMaxNumberOfItemsToReturn() );
		
		List<Lookup.LookupResult> results = null;
		
		try {
			results = this.backendServices.getSuggesters(request);
		} catch (BackendException e) {
			this.logger.error(e);
			return null;
		}
		
		SuggestionSearch suggestionForSearch = null;
		for (Lookup.LookupResult result : results) {
			suggestionForSearch = 
					new com.ctnf.client.activities.search.suggestion.SuggestionSearch(
							result.highlightKey.toString(),result.key.toString());
			
			suggestions.add(suggestionForSearch);
	    }
		
		Response response = new Response();
		response.setSuggestions(suggestions);
		
		logger.trace("Got suggestions response : "+response.getSuggestions().size());
		
		return response;
	}
}
