package com.ctnl.events.suggester;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.ctnc.Constants;
import com.ctnc.Event;
import com.ctnc.SuggesterDto;
import com.ctnc.shared.IndexKey;
import com.ctns.query.QueryBuilderException;
import com.ctns.query.Utils;
import com.ctnu.environment.Environment;

public class SuggesterByVenueName{
	
	private Logger logger = Logger.getLogger(SuggesterByVenueName.class);

	@Autowired 
	private SuggesterProperties properties;
	
	public List<SuggesterDto> build(Map<String, List<Event>> events, Map<String, List<String>> identicalVenues) throws SuggesterException
	{
		int totalEventsCounter = 0;
		
		Map<String,SuggesterDto> suggesters = new HashMap<String,SuggesterDto>(10000);
		
		for(Map.Entry<String, List<Event>>  segment : events.entrySet())
		{				
			totalEventsCounter+=segment.getValue().size();
				
			logger.info("---"+segment.getKey()+"--- " + segment.getValue().size());
			
			for(Event event : segment.getValue() )
			{
				this.process( true ,event , suggesters );
				
				if(event.getMoreSources()!=null && event.getMoreSources().size()>0 )
				{
					for(Event moreSourcesEvent : event.getMoreSources())
					{
						this.process( false , moreSourcesEvent , suggesters );
					}
				}
			}
		}
		
		logger.info("Total suggesters number : "+suggesters.size());
		
		logger.info("Total events number : "+totalEventsCounter);
				
		List<SuggesterDto> results = new ArrayList<SuggesterDto>( suggesters.values() );
		
		return results;
	}
	
	private void process( boolean isNewEvent ,Event event, Map<String, SuggesterDto> suggesters) throws SuggesterException
	{
		try {
			
			String key =  Utils.stamp( Constants.VENUE_NAME , event.getVenue().getName() );
					
			if( suggesters.containsKey( key ) && isNewEvent==true )
			{
				SuggesterDto suggester = suggesters.get( key );
						
				suggester.setWeight( (suggester.getWeight()+1 ));
				
				suggester.getEvents().add(event.getInternalId());
				
				suggester.getSegmentedKeys().add(event.getSegmentKey());
				
				suggester.getKeywords().add(event.getVenue().getName());
				
			}
			else if( ! suggesters.containsKey( key ) )
			{
				SuggesterDto suggester = new SuggesterDto();
				suggester.setKeyword(event.getVenue().getName());
				suggester.setRawKeyword(key);
				suggester.setWeight(1l);
				
				suggester.setEvents(new HashSet<Long>());
				suggester.getEvents().add(event.getInternalId());
				
				suggester.setTriggerEventInternalId(event.getInternalId());
				
				suggester.setIndexKey(IndexKey.venue);
				
				suggester.setEvents( new HashSet<Long>() );
				suggester.getEvents().add(event.getInternalId());
				
				suggester.setSegmentedKeys( new HashSet<String>() );
				suggester.getSegmentedKeys().add(event.getSegmentKey());
				
				suggester.setKeywords( new HashSet<String>() );
				suggester.getKeywords().add(event.getVenue().getName());
				
				suggesters.put( key , suggester );
			}
		} 
		catch (QueryBuilderException e) {
			throw new SuggesterException(e);
		}
	}

	public String buildSpell(List<SuggesterDto> suggestersByVenueName) {
		StringBuffer buffer = new  StringBuffer();
		for(SuggesterDto suggesterByVenueName : suggestersByVenueName)
		{
			buffer.append(suggesterByVenueName.getKeyword().toLowerCase());
			buffer.append(Environment.getLineSeparator());
		}
		
		return buffer.toString();
	}	
}
