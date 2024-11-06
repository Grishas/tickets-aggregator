package com.ctns.suggester.index;
import java.util.List;
import org.apache.log4j.Logger;
import org.apache.lucene.search.suggest.Lookup;
import org.springframework.beans.factory.annotation.Autowired;
import com.ctnc.shared.IndexKey;
import com.ctnc.shared.SuggestionRequest;

public class SuggesterIndexService {

	private Logger logger = Logger.getLogger(SuggesterIndexService.class);

	@Autowired
	private SuggesterIndexLookup suggesterIndexLookup;
	
	@Autowired 
	private SuggesterIndexProperties suggesterProperties;
	
    public SuggesterIndexService(){}

    public List<Lookup.LookupResult> getSuggestions(SuggestionRequest request) throws SuggesterIndexException{
    	return this.suggesterIndexLookup.lookup(request);
    }

	public int getSuggesterMaxNumberOfItemsToReturn() {
		return this.suggesterProperties.getSuggesterMaxNumberOfItemsToReturn();
	}
	
	public String suggestSimilar(IndexKey index ,String query) throws SuggesterIndexException {
		return this.suggesterIndexLookup.suggestSimilar( index , query );
	}
}
