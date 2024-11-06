package com.ctnl.events.suggester;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import com.ctnc.Event;
import com.ctnc.SuggesterDto;
import com.ctnc.shared.IndexKey;
import com.ctns.query.QueryBuilderException;
import com.ctns.query.Utils;
import com.ctnu.environment.Environment;

public class SuggesterByLocationName{

	private Logger logger = Logger.getLogger(SuggesterByLocationName.class);

	@Autowired 
	private SuggesterProperties suggesterProperties;
	
	public List<SuggesterDto> build(Map<String, List<Event>> events) throws SuggesterException
	{
		int totalEventsCounter = 0;
		
		Map<String,SuggesterDto> suggesters = new HashMap<String,SuggesterDto>(1000);
		
		for(Map.Entry<String,List<Event>> entry : events.entrySet())
		{			
			for(Event event : entry.getValue())
			{	
				this.process( event , suggesters);
				
				if(event.getMoreSources()!=null&&event.getMoreSources().size()>0)
				{
					for(Event moreSourcesEvent : event.getMoreSources())
					{
						this.process(moreSourcesEvent,suggesters);
					}
				}
			}
		}
		
		logger.info("Total suggesters number : "+suggesters.size());
		
		logger.info("Total events number : "+totalEventsCounter);
				
		List<SuggesterDto> results = new ArrayList<SuggesterDto>( suggesters.values() );
		
		return results;
	}
	
	private void process( Event event, Map<String, SuggesterDto> suggesters) throws SuggesterException
	{
		for(String cityName : event.getLocation().getCity().getCityNames())
    	{
    		try 
    		{
    			String key =  Utils.getLocationKey(event.getSegmentKey(), cityName );
    			
    			if( suggesters.containsKey( key ) )
    			{
    				SuggesterDto suggester = suggesters.get( key );
    				
    				suggester.setWeight( (suggester.getWeight() + 1 ) );
    				
    				suggester.getEvents().add( event.getInternalId() );
    				
    			}
    			else if( ! suggesters.containsKey( key ) )
    			{
    				SuggesterDto suggester = new SuggesterDto();
    				
    				String state  = event.getLocation().getState().getAbbreviation();
    				if(state!=null){
    					state = state+",";
    				}
    				else{
    					state = "";
    				}
    			
    				String keyword  = cityName+"," + state.toUpperCase() + event.getLocation().getCountry().getAbbreviation().toUpperCase();
    				
    				suggester.setKeyword( keyword );
    				
    				suggester.setRawKeyword( key );
    				
    				suggester.setWeight(1l);
    				
    				suggester.setEvents(new HashSet<Long>());
    				suggester.getEvents().add(event.getInternalId());
    				
    				suggester.setIndexKey(IndexKey.location);
    				
    				suggester.setSegmentedKeys( new HashSet<String>() );
    				suggester.getSegmentedKeys().add(event.getSegmentKey());
    				
    				suggesters.put( key , suggester );
    			}
    		} 
    		catch (QueryBuilderException e) {
    			throw new SuggesterException(e);
    		}
    	}
	}

	public String build(List<SuggesterDto> suggestersByLocationName) {
		StringBuffer buffer = new  StringBuffer();
		for(SuggesterDto suggesterByLocationName : suggestersByLocationName)
		{
			buffer.append(suggesterByLocationName.getKeyword().toLowerCase());
			buffer.append(Environment.getLineSeparator());
		}
		
		return buffer.toString();
	}	
}
