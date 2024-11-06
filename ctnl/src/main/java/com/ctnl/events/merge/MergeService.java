package com.ctnl.events.merge;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import com.ctnc.Constants;
import com.ctnc.Event;
import com.ctns.query.QueryBuilderException;
import com.ctns.query.Utils;
import com.ctnu.environment.Environment;

public class MergeService 
{
	private Logger logger = Logger.getLogger(MergeService.class);

	@Autowired
	private Merge merge; 
	
    public MergeService(){}
        
    public enum MergeLocationResult{LEVEL1,LEVEL2};
    public Map<String,Set<String>> mergeLocation(Map<String, List<Event>> eventsFromMerge,MergeLocationResult mergeLocationResult) throws MergeException
    {
    	Map<String,Set<String>> resultsFirstLevel = new HashMap<String, Set<String>>();
    	Map<String,Set<String>> resultsSecondLevel = new HashMap<String, Set<String>>();
    	
		for(Map.Entry<String,List<Event>> entry : eventsFromMerge.entrySet())
		{			
			for(Event event : entry.getValue())
			{	
				if(event.getMoreSources()!=null&&event.getMoreSources().size()>0)
				{
					List<Event> flat = new ArrayList<Event>();
					flat.add(event);	
					flat.addAll(event.getMoreSources());
					
					this.mergeLocationProcessFirstLevel(flat,resultsFirstLevel);
				}
			}
		}
		
		logger.info("@@@Start first level results~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

		for(Map.Entry<String,Set<String>> entry : resultsFirstLevel.entrySet())
		{
			logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
			
			logger.info(entry.getKey());
			
			for(String value : entry.getValue())
			{
				logger.info(value);
			}
		}

		logger.info("@@@End first level results~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		
		if(mergeLocationResult.equals(MergeLocationResult.LEVEL1))
		{
			return resultsFirstLevel;
		}
		
		this.mergeLocationProcessSecondLevel(resultsFirstLevel,resultsSecondLevel);
    	
		logger.info("@@@Start second level results~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		
		for(Map.Entry<String,Set<String>> entry : resultsSecondLevel.entrySet())
		{
			logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
			
			logger.info(entry.getKey());
			
			for(String value : entry.getValue())
			{
				logger.info(value);
			}
		}
		
		logger.info("@@@End second level results~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

		logger.info("@@@Start second level results(only duble plus)~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		
		for(Map.Entry<String,Set<String>> entry : resultsSecondLevel.entrySet())
		{
			if(entry.getValue().size()==1)
			{
				continue;
			}
			
			logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
			
			logger.info(entry.getKey());
			
			for(String value : entry.getValue())
			{
				logger.info(value);
			}
		}
		
		logger.info("@@@End second level results(only duble plus)~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		
		if(mergeLocationResult.equals(MergeLocationResult.LEVEL2))
		{
			return resultsSecondLevel;
		}
		
		return null;
    }
    
    private void mergeLocationProcessFirstLevel(
    		List<Event> sameEvent, 
    		Map<String, Set<String>> resultsFirstLevel) throws MergeException
    {
    	//collect all close cities of event 
    	Set<String> likeCity = new HashSet<String>();
    	
    	for(Event event : sameEvent)
    	{
    		likeCity.addAll(event.getLocation().getCity().getCityNames());
    	}
    	
    	//representer
    	Event event = sameEvent.get(0);
    	
    	for(String cityName : likeCity)
    	{
    		String key = null;
    		
    		try 
    		{
				key = Utils.getLocationKey(event.getSegmentKey(),cityName);
				
    		} catch (QueryBuilderException e) {
    			throw new MergeException(e);
			}
    			
    		if( resultsFirstLevel.containsKey( key ) )
    		{
    			resultsFirstLevel.get( key ).addAll( likeCity );
    		}
    		else
    		{
    			Set<String> set = new HashSet<String>();
    			set.addAll( likeCity );
    			resultsFirstLevel.put( key , set );
    		}  			
    	}
    }
    
    //second level conclusion
    //if a related to b and b related to c then a related to c(b is connector)
    //may have bad impact when user type wrong data(in this case wrong city name)
    //we cover this errors in our policy
    private void mergeLocationProcessSecondLevel(Map<String, Set<String>> resultsFirstLevel,Map<String, Set<String>> resultsSecondLevel) throws MergeException
    {
    	for(Map.Entry<String,Set<String>> entry : resultsFirstLevel.entrySet())
    	{
    		String mainKey = entry.getKey();
    		
    		String[] segmentKey = mainKey.split("#");
    		
    		logger.info("current main key: "+mainKey);
    		
    		for(String cityName : entry.getValue())
    		{
    			String subKey = null;
    			try {
    				subKey = Utils.getLocationKey(segmentKey[0],cityName);
				} catch (QueryBuilderException e) {
					throw new MergeException(e);
				}
    			
        		logger.info("current sub key: "+subKey);
    			
    			if( resultsSecondLevel.containsKey( mainKey ) )
    			{
    				resultsSecondLevel.get(mainKey).addAll( resultsFirstLevel.get( subKey ) );
    			}
    			else
    			{
    				resultsSecondLevel.put(mainKey, resultsFirstLevel.get( subKey ) );
    			}	
    		}	

			if( resultsSecondLevel.containsKey(mainKey) ) //add org cities
			{
				resultsSecondLevel.get(mainKey).addAll( resultsFirstLevel.get( mainKey ) );
			}
			else
			{
				resultsSecondLevel.put( mainKey , resultsFirstLevel.get( mainKey ) );    				
			}
    	}
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
	public Map<String,List<String>> mergeVenues(Map<String, List<Event>> eventsFromMerge) throws MergeException 
	{
		Map<String,List<String>> venuesMapping = new HashMap<String,List<String>>();

		for(Map.Entry<String,List<Event>> events_ : eventsFromMerge.entrySet())
		{			
			for(Event event : events_.getValue())
			{				
				if(event.getMoreSources()!=null&&event.getMoreSources().size()>0)
				{
					//flat events
					List<Event> flat = new ArrayList<Event>();
					flat.add(event);
					flat.addAll(event.getMoreSources());		
					
					this.mergeVenuesProcess( flat , venuesMapping );
				}
			}
		}
		
		return venuesMapping;
	}	
	
	/***
	 * create identical venue names data. 
	 * @param sameEvent
	 * @param venuesMapping
	 * @throws MergeException
	 */
	private void mergeVenuesProcess(List<Event> sameEvent, Map<String, List<String>> venuesMapping) throws MergeException
	{
		Map<String,Event> tmp = new HashMap<String,Event>();
		
		for(Event event : sameEvent)
		{
			String venueName = null;
			try 
			{
				venueName = Utils.stamp(Constants.VENUE_NAME ,event.getVenue().getName() );
			} 
			catch (QueryBuilderException e) {
				throw new MergeException(e);
			}
			
			if( ! tmp.containsKey( venueName ) )			
			{
				tmp.put( venueName , event );
			}
		}
		
		if( tmp.size() > 1 )//we have different venue names 
		{
			for(Map.Entry<String,Event> eventA : tmp.entrySet())
			{
				String venueNameA = eventA.getValue().getVenue().getName();
				
				for(String cityName : eventA.getValue().getLocation().getCity().getCityNames())
				{
					String keyA = null;
					try {
						keyA = Utils.getVenueKey( eventA.getValue().getSegmentKey(),cityName,venueNameA);
					} catch (QueryBuilderException e) {
						throw new MergeException(e);
					}
					
					for(Map.Entry<String,Event> eventB : tmp.entrySet())
					{
						if(eventA.getValue().equals(eventB.getValue()))
						{
							continue;
						}
						
						String venueNameB = eventB.getValue().getVenue().getName();
						
						if( venuesMapping.containsKey( keyA ) )
						{
							if( ! venuesMapping.get( keyA ).contains( venueNameB ) )
							{
								logger.info(Environment.getLineSeparator()+
								"------------------------------------------------------------");
								venuesMapping.get( keyA ).add( venueNameB );
								logger.info("update key: \""+keyA+"\" with value" );
								logger.info(venueNameA);
								logger.info(eventA.getValue().getInternalId());
							}
						}
						else
						{
							List<String> values = new ArrayList<String>(2);
							values.add(venueNameA);
							values.add(venueNameB);						
							venuesMapping.put(keyA,values);
							
							logger.info(Environment.getLineSeparator()+
							"------------------------------------------------------------");
							logger.info("create new key: \""+keyA+"\" with values");
							logger.info(eventA.getValue().getInternalId());
							logger.info(eventB.getValue().getInternalId());
						}
					}
				}
			}
		}
	}
	
    public Map<String, List<Event>> mergeEvent(List<Map<String, List<Event>>> events) throws MergeException
    {   
    	return this.merge.process(events);	
    }

    /***
     * method provided for cities names normalization
     */
    public void reportMergedEventsWithDifferentCities(Map<String, List<Event>> eventsFromMerge) throws MergeException 
	{
    	logger.info("@@@Starting report of merged events with different cities@@@");

    	long sameCityName = 0;    	
    	long differentCityName = 0;
    	StringBuilder shortReport = new StringBuilder();
    	Set<String> avoidDuplicateShortReport  = new HashSet<String>();
    	
		for(Map.Entry<String,List<Event>> events_ : eventsFromMerge.entrySet())
		{			
			for(Event event : events_.getValue())
			{	
				if(event.getMoreSources()!=null&&event.getMoreSources().size()>0)
				{
					Set<String> tmp = new HashSet<String>();
					List<Event> relevantEvents = new ArrayList<Event>(event.getMoreSources().size());
					
					//assume that all cities names are correct here
					//after add/replace relevant in collect process
					tmp.addAll(event.getLocation().getCity().getCityNames());
					
					int counterA = tmp.size();
							
					for(Event moreSourcesEvent : event.getMoreSources())
					{
						tmp.addAll(moreSourcesEvent.getLocation().getCity().getCityNames());
						relevantEvents.add(moreSourcesEvent);
					}
					
					int counterB = tmp.size();
					
					if(counterA!=counterB)
					{
						relevantEvents.add(event);

						differentCityName++;

						logger.info(Environment.getLineSeparator()+"______________________________________________________________________");
						
						StringBuilder tmp_ = new StringBuilder();
						for(Event relevantEvent : relevantEvents)
						{
							logger.info("Event internal id: "+ relevantEvent.getInternalId());
							logger.info("Cities: "+relevantEvent.getLocation().getCity().getCityNames() );
							
							tmp_.append(relevantEvent.getSegmentKey()+" "+relevantEvent.getLocation().getCity().getCityNames());
						}
						
						if( ! avoidDuplicateShortReport.contains(tmp_.toString()))
						{
							shortReport.append(Environment.getLineSeparator()+"______________________________________________________________________");
							shortReport.append(tmp_.toString());
							avoidDuplicateShortReport.add(tmp_.toString());
						}
						
					}
					else
					{
						sameCityName++;
					}
				}
			}
		}
		
		logger.info("Events number  with different city name short report : "+shortReport.toString());
		logger.info("Events number with same city name: "+sameCityName);
		logger.info("Events number with different city name: "+differentCityName);	
		logger.info("@@@Finish report of merged events with different cities@@@");
	}	
}


