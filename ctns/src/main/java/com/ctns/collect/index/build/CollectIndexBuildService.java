package com.ctns.collect.index.build;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import com.ctnc.Event;
import com.ctnc.shared.Performer;
import com.ctns.query.QueryBuilderException;
import com.ctns.query.Utils;

public class CollectIndexBuildService {

	private Logger logger = Logger.getLogger(CollectIndexBuildService.class);

	@Autowired
	private CollectIndexBuild collectIndexBuild;
	
	@Autowired 
	private CollectIndexBuildProperties properties;
	
    public CollectIndexBuildService(){}

	public void build(List<Map<String, List<Event>>> events,Map<String, List<LocationMappingRule>> locationMappingRules)
			throws CollectIndexBuildException 
	{
		this.setInternalId(events);	
		
		this.normalizeData(events,locationMappingRules);		
    }
	
	public void build(List<Map<String, List<Event>>> events)throws CollectIndexBuildException {
		
		this.collectIndexBuild.build(events);
    }

	private List<Map<String, List<Event>>> setInternalId(List<Map<String, List<Event>>> allEvents) {
		
		long internalId = 1;
		
		for(Map<String, List<Event>> source : allEvents)
		{
			for(List<Event> events : source.values())
			{
				for(Event event : events)
				{
					event.setInternalId(internalId);
					
					internalId++;
				}
			}
		}
		
		logger.info("total number of events in system is: "+internalId);
		
		return allEvents;
	}

	private Map<String,String> spellingVariants = new HashMap<String, String>();
	private Map<String,String> venuesMapping = new HashMap<String, String>();

	private List<Map<String, List<Event>>> 
	normalizeData(List<Map<String, List<Event>>> allEvents, Map<String, List<LocationMappingRule>> locationMappingRules) 
			throws CollectIndexBuildException {

		venuesMapping.put("luxor hotel casino#us_nv#la vega","Luxor Theater - Luxor Hotel");
		venuesMapping.put("shea pac#us_ny#orchard park","Sheas Performing Arts Center");
		venuesMapping.put("shea pac#us_ny#buffalo","Sheas Performing Arts Center");
		venuesMapping.put("axi planet hollywood#us_nv#la vega","Planet Hollywood Theater Of The Performing Arts");
		venuesMapping.put("bbt center#us_fl#sunris","BB&T Center");
		
		venuesMapping.put("david copperfield theater mgm grand#us_nv#la vega","MGM Grand");
		venuesMapping.put("mgm grand hollywood theater#us_nv#la vega","MGM Grand");
		
		venuesMapping.put("lyric theater new york#us_ny#new york","Lyric Theater");
		venuesMapping.put("lyric theater broadwai#us_ny#new york","Lyric Theater");
		
		venuesMapping.put("bb king club#us_ny#new york","B.B. King Blues Club & Grill");
		venuesMapping.put("b.b king blue club grill new york#us_ny#new york","B.B. King Blues Club & Grill");
		
		venuesMapping.put("arkansa state univers convoc center#us_ar#jonesboro","ASU Convocation Center");

		
		venuesMapping.put("tabernacl atlanta#us_ga#atlanta","The Tabernacle");
		venuesMapping.put("tabernacl ga#us_ga#atlanta","The Tabernacle");

		

		

		
		

		


		
		
		spellingVariants.put("theatre","Theater");
		
			for(Map<String, List<Event>> source : allEvents)
			{
				for(List<Event> events : source.values())
				{
					for(Event event : events)
					{
						this.normalizeSpaces(event);
						
						//this.capitalize(event);

						this.spellingVariants(event);
						
						this.venueNameVariants(event);
						
						this.cityNameVariants(event,locationMappingRules);
					}
				}
			}
			
			return allEvents;
		}

	private void normalizeSpaces(Event event) 
	{
		String eventName = event.getName().replaceAll("\\s+", " ");
		event.setName(eventName);
		
		if(event.getPerformers()!=null&&event.getPerformers().size()>0)
		{
			for(Performer performer : event.getPerformers())
			{
				String performerName = performer.getName().replaceAll("\\s+", " ");
				performer.setName(performerName);
			}
		}
		
		String venueName = event.getVenue().getName().replaceAll("\\s+", " ");
		event.getVenue().setName(venueName);
	}

	private void capitalize(Event event) 
	{
		String eventName = WordUtils.capitalize(event.getName());
		event.setName(eventName);
		
		if(event.getPerformers()!=null&&event.getPerformers().size()>0)
		{
			for(Performer performer : event.getPerformers())
			{
				String performerName = WordUtils.capitalize(performer.getName());
				performer.setName(performerName);
			}
		}
		
		String venueName = WordUtils.capitalize(event.getVenue().getName());
		event.getVenue().setName(venueName);
	}
	
	private void cityNameVariants(Event event, Map<String, List<LocationMappingRule>> locationMappingRules) throws CollectIndexBuildException 
	{
		Set<String> modified = new HashSet<String>();
	 	
		for(String cityName : event.getLocation().getCity().getCityNames())
		{
			//cityNames = cityNames.replace("-","");
			
			String key = null;
			try {
				key = Utils.getLocationKey( event.getSegmentKey() , cityName );
			} catch (QueryBuilderException e) {
				throw new CollectIndexBuildException(e); 
			}
			
	    	if( locationMappingRules.containsKey( key ) )
	    	{
	    		for(LocationMappingRule locationMappingRule : locationMappingRules.get(key))
	    		{
	    			switch (locationMappingRule.getAction()) 
	    			{
						case replace:
							
							modified.add( locationMappingRule.getValue() );
							
							break;
					default:
						break;
							
						//problematic logic 
//						case add:
//							
//							if( ! modified.contains( cityName ))
//							{
//								modified.add(cityName);
//							}
//							
//							modified.add(locationMappingRule.getValue());
//							
//							break;		
					}
	    		}
	    	}
		}
		
		if( modified.size() > 0 ) 
		{
			logger.info("City name variants modify event with internal id: "+event.getInternalId());
			logger.info("city names before: "+event.getLocation().getCity().getCityNames());
			event.getLocation().getCity().setCityNames(modified);
			logger.info("city names after: "+event.getLocation().getCity().getCityNames());
		}
	}

	private void venueNameVariants(Event event) throws CollectIndexBuildException
	{
		for(String cityName : event.getLocation().getCity().getCityNames())
		{
			String venueKey = null;
			
		    try {
				venueKey = Utils.getVenueKey(event.getSegmentKey(),cityName,event.getVenue().getName());
			} catch (QueryBuilderException e) {
				throw new CollectIndexBuildException(e);
			}
				
			if( venuesMapping.containsKey( venueKey ) )
			{
				String venueName = venuesMapping.get(venueKey);
					venueName = WordUtils.capitalize(venueName);
					event.getVenue().setName(venueName);
					logger.info("normalize venue with key: "+venueKey+" to: "+venueName+".internal event id:"+event.getInternalId());
					
					break;//replace one time !!!
			}
		}
	}

	private void spellingVariants(Event event) 
	{
		for(Map.Entry<String,String> entry : spellingVariants.entrySet())
		{
			if(event.getName().toLowerCase().contains(entry.getKey())==true)
			{
				String eventName = event.getName().toLowerCase().replace(entry.getKey(),entry.getValue());
				eventName = WordUtils.capitalize(eventName);
				event.setName(eventName);
				logger.info("Normalize event name: \""+event.getName()+"\" from: "+entry.getKey()+" to: "+entry.getValue()+".Internal event id: "+event.getInternalId());
			}

			if(event.getVenue().getName().toLowerCase().contains(entry.getKey())==true)
			{
				String venueName = event.getVenue().getName().toLowerCase().replace(entry.getKey(),entry.getValue());
				venueName = WordUtils.capitalize(venueName);
				event.getVenue().setName(venueName);
				logger.info("Normalize venue name: \""+event.getVenue().getName()+"\" from: "+entry.getKey()+" to: "+entry.getValue()+".Internal event id: "+event.getInternalId());
			}
			
			for(Performer performer : event.getPerformers())
			{
				if(performer.getName().toLowerCase().contains(entry.getKey())==true)
				{
					String performerName = performer.getName().toLowerCase().replace(entry.getKey(),entry.getValue());
					performerName = WordUtils.capitalize(performerName);
					performer.setName(performerName);
					logger.info("Normalize performer name: \""+performer.getName()+"\" from: "+entry.getKey()+" to: "+entry.getValue()+".Internal event id: "+event.getInternalId());
				}
			}
		}
	}
}
