package com.ctns.suggester.index;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.search.suggest.Lookup;
import org.apache.lucene.search.suggest.analyzing.AnalyzingInfixSuggester;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import com.ctnc.shared.IndexKey;
import com.ctnc.shared.SuggestionRequest;

//http://stackoverflow.com/questions/24968697/how-to-implements-auto-suggest-using-lucenes-new-analyzinginfixsuggester-api
//http://blog.mikemccandless.com/2014/11/apache-lucene-500-is-coming.html
//https://today.java.net/pub/a/today/2005/08/09/didyoumean.html
//http://puneetkhanal.blogspot.co.il/2013/04/simple-auto-suggester-using-lucene-41.html
//http://stackoverflow.com/questions/24968697/how-to-implements-auto-suggest-using-lucenes-new-analyzinginfixsuggester-api
//http://blog.mikemccandless.com/2012/09/lucenes-new-analyzing-suggester.html	

public class SuggesterIndexLookup{
	
	private Logger logger = Logger.getLogger(SuggesterIndexLookup.class);

	@Autowired
	@Qualifier(value="AnalyzingInfixSuggesterPerformerName")
	private AnalyzingInfixSuggester analyzingInfixSuggesterPerformerName;
	
	@Autowired
	@Qualifier(value="AnalyzingInfixSuggesterEventName")
	private AnalyzingInfixSuggester analyzingInfixSuggesterEventName;
	
	@Autowired
	@Qualifier(value="AnalyzingInfixSuggesterVenueName")
	private AnalyzingInfixSuggester analyzingInfixSuggesterVenueName;
	
	@Autowired
	@Qualifier(value="AnalyzingInfixSuggesterLocationName")
	private AnalyzingInfixSuggester analyzingInfixSuggesterLocationName;
	
	@Autowired
	private SuggesterIndexProperties properties;  
	
	@Autowired
	@Qualifier(value="SpellCheckerEventName")
	private SpellChecker spellCheckerEventName;
	
	@Autowired
	@Qualifier(value="SpellCheckerPerformerName")
	private SpellChecker spellCheckerPerformerName;
	
	@Autowired
	@Qualifier(value="SpellCheckerVenueName")
	private SpellChecker spellCheckerVenueName;
	
	@Autowired
	@Qualifier(value="SpellCheckerLocationName")
	private SpellChecker spellCheckerLocationName;

	public List<Lookup.LookupResult> lookup( SuggestionRequest request  ) throws SuggesterIndexException 
	{
		int maxNumberOfItemsToReturn = this.properties.getSuggesterMaxNumberOfItemsToReturn();
		List<Lookup.LookupResult> result = null;
		
		long s,e=0;
		
		try 
		{
			//event(0),performer(1),venue(2),location(3)
			switch (request.getRunOnSuggesterIndex()) 
			{
				case event:
					s = System.currentTimeMillis();
					
					result =  this.analyzingInfixSuggesterEventName.lookup( 
							request.getQuery() , maxNumberOfItemsToReturn , true , true );
					
					if(result.size()==0)
					{
						String[] similars = this.spellCheckerEventName.suggestSimilar(request.getQuery(),1);

						System.out.println("___________________________"+System.currentTimeMillis());
	
						for(String similar : similars)
						{
							System.out.println("similar :"+similar);
						}
					}
					
					e = System.currentTimeMillis();
					
					System.out.println("event name lookup: "+(e-s));
					
					return result;
					
				case performer:
					s = System.currentTimeMillis();

					result = this.analyzingInfixSuggesterPerformerName.lookup( request.getQuery() , maxNumberOfItemsToReturn , true , true );
					
					if(result.size()==0)
					{
						String[] similars = this.spellCheckerPerformerName.suggestSimilar(request.getQuery(),1);

						System.out.println("___________________________"+System.currentTimeMillis());
	
						for(String similar : similars)
						{
							System.out.println("similar :"+similar);
						}
					}
					
					e = System.currentTimeMillis();
					
					System.out.println("performer name lookup: "+(e-s));
					
					return result;
				case venue:
					
					s = System.currentTimeMillis();

					result = this.analyzingInfixSuggesterVenueName.lookup( request.getQuery() , maxNumberOfItemsToReturn , true , true );
				
					if(result.size()==0)
					{
						String[] similars = this.spellCheckerVenueName.suggestSimilar(request.getQuery(),1);

						System.out.println("___________________________"+System.currentTimeMillis());
	
						for(String similar : similars)
						{
							System.out.println("similar :"+similar);
						}
					}
					
					e = System.currentTimeMillis();
					
					System.out.println("venue name lookup: "+(e-s));
					
					return result;
				
				case location:
					s = System.currentTimeMillis();

					result = this.analyzingInfixSuggesterLocationName.lookup( request.getQuery() , maxNumberOfItemsToReturn , true , true );
					
					if(result.size()==0)
					{
						String[] similars = this.spellCheckerLocationName.suggestSimilar(request.getQuery(),1);

						System.out.println("___________________________"+System.currentTimeMillis());
	
						for(String similar : similars)
						{
							System.out.println("similar :"+similar);
						}
					}
					
					e = System.currentTimeMillis();
					
					System.out.println("location name: "+(e-s));
					
					return result;
			} 
		}
		catch (IOException e1) {
			throw new SuggesterIndexException(e1);
		}
		
		return null;
	}
	
	
	
	public String suggestSimilar( IndexKey index ,String query ) throws SuggesterIndexException 
	{
		
		long s,e=0;
		
		String[] suggestSimilar = null;
		
		try 
		{
			//event(0),performer(1),venue(2),location(3)
			switch (index) 
			{
				case event:
					s = System.currentTimeMillis();
					
					suggestSimilar = this.spellCheckerEventName.suggestSimilar( query , 1 );
	
					e = System.currentTimeMillis();
					
					this.logger.debug("event name suggest similar: "+(e-s));
					
					break;
					
				case performer:
					s = System.currentTimeMillis();

					suggestSimilar = this.spellCheckerPerformerName.suggestSimilar( query , 1 );
					
					e = System.currentTimeMillis();
					
					System.out.println("performer name suggest similar: "+(e-s));
					
					break;
				
				case venue:
					
					s = System.currentTimeMillis();

					suggestSimilar = this.spellCheckerVenueName.suggestSimilar( query , 1 );
				
					e = System.currentTimeMillis();
					
					System.out.println("venue name suggest similar: "+(e-s));
					
					break;
									
				case location:
					s = System.currentTimeMillis();

					suggestSimilar = this.spellCheckerLocationName.suggestSimilar( query , 1 );
					
					e = System.currentTimeMillis();
					
					System.out.println("location suggest similar: "+(e-s));
					
					break;
			} 
		}
		catch (IOException e1) {
			throw new SuggesterIndexException(e1);
		}
		
		if( suggestSimilar!=null && suggestSimilar.length > 0 )
		{
			return suggestSimilar[0];
		}
		else
		{
			return null;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
