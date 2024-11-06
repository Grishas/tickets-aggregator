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
import com.ctnc.shared.Performer;
import com.ctns.query.QueryBuilderException;
import com.ctns.query.Utils;
import com.ctnu.environment.Environment;

public class SuggesterByPerformerName{

	private Logger logger = Logger.getLogger(SuggesterByPerformerName.class);

	@Autowired 
	private SuggesterProperties properties;
	
	public List<SuggesterDto> build(Map<String, List<Event>> events) throws SuggesterException
	{
		int totalEventsCounter = 0;
		
		Map<String,SuggesterDto> suggesters = new HashMap<String,SuggesterDto>(10000);
		
		for(Map.Entry<String, List<Event>>  segment : events.entrySet())
		{				
			totalEventsCounter+=segment.getValue().size();
				
			logger.info("---"+segment.getKey()+"--- " + segment.getValue().size());
			
			for(Event event : segment.getValue())
			{
				if(event.getPerformers()!=null&&event.getPerformers().size()>0)
				{
					for(Performer performer : event.getPerformers())
					{
						if(performer.getName()==null||performer.getName().equals("")){
							logger.warn("Performer name is empty or null : "+event);
							continue;
						}
						
						try {
							String key =  Utils.stamp(Constants.PERFORMER_NAME,performer.getName());
							
							if( suggesters.containsKey( key ) )
							{
								SuggesterDto suggester = suggesters.get(key);
								
								suggester.setWeight((suggester.getWeight()+1));
								
								suggester.getEvents().add(event.getInternalId());
								
								suggester.getSegmentedKeys().add(event.getSegmentKey());
								
								suggester.getKeywords().add(performer.getName());
							}
							else
							{
								SuggesterDto suggester = new SuggesterDto();
								suggester.setKeyword(performer.getName());
								suggester.setRawKeyword(key);
								suggester.setWeight(1l);
								suggester.setEvents(new HashSet<Long>());
								suggester.getEvents().add(event.getInternalId());
								suggester.setTriggerEventInternalId(event.getInternalId());
								suggester.setIndexKey(IndexKey.performer);
								
								suggester.setEvents( new HashSet<Long>(10000) );
								suggester.getEvents().add(event.getInternalId());
								
								suggester.setSegmentedKeys( new HashSet<String>(25) );
								suggester.getSegmentedKeys().add(event.getSegmentKey());
								
								suggester.setKeywords( new HashSet<String>(200) );
								suggester.getKeywords().add(performer.getName());
								
								suggesters.put( key , suggester );
							}
							
						} catch (QueryBuilderException e) {
							e.printStackTrace();
							logger.error(e);
						}
					}
				}
			}
		}
		
		logger.info("Total suggesters number : "+suggesters.size());
		logger.info("Total events number : "+totalEventsCounter);
				
		List<SuggesterDto> results = new ArrayList<SuggesterDto>( suggesters.values() );
		
		return results;
	}

	public String buildSpell(List<SuggesterDto> suggestersByPerformerName) {
		StringBuffer buffer = new  StringBuffer();
		for(SuggesterDto suggesterByPerformerName : suggestersByPerformerName)
		{
			buffer.append(suggesterByPerformerName.getKeyword().toLowerCase());
			buffer.append(Environment.getLineSeparator());
		}
		
		return buffer.toString();
	}
}