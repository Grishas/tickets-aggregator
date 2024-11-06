package com.ctns.run;
import java.util.List;

import com.ctnc.shared.IndexKey;
import com.ctnc.shared.SuggestionRequest;
import com.ctns.collect.index.CollectIndexConfiguration;
import com.ctns.collect.index.CollectIndexException;
import com.ctns.collect.index.CollectIndexService;
import com.ctns.suggester.index.SuggesterIndexConfiguration;
import com.ctns.suggester.index.SuggesterIndexException;
import com.ctns.suggester.index.SuggesterIndexService;
import com.google.gwt.user.client.ui.SuggestOracle.Request;

import org.apache.lucene.search.suggest.Lookup;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Cli_SuggesterIndexTest {

	private void run()
	{
		AnnotationConfigApplicationContext context = 
				new AnnotationConfigApplicationContext(SuggesterIndexConfiguration.class);

		SuggesterIndexService suggesterService = context.getBean(SuggesterIndexService.class);
		
		SuggestionRequest request = new SuggestionRequest();
		request.setQuery("cirque");
		request.setLimit(1000);
		request.setRunOnSuggesterIndex(IndexKey.event);
		
		List<Lookup.LookupResult> results = null;
		try {
			
			long start = System.currentTimeMillis();
			results = suggesterService.getSuggestions(request);
			long end = System.currentTimeMillis();
			System.out.println("suggest call take : "+(end-start)+" ms");
		
		} catch (SuggesterIndexException e) {
			e.printStackTrace();
		}
		
		System.out.println("results number : "+results.size());
		
		System.out.println("---------------------------------");
		
		for(Lookup.LookupResult result : results)
		{
			System.out.println(result.key);
		}
		
		System.out.println("---------------------------------");
		
		context.close();	
	}
	
	public static void main(String[] args) {
		new Cli_SuggesterIndexTest().run();
	}
}
