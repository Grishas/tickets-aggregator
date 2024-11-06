package com.ctnl.events.suggester;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.springframework.beans.factory.annotation.Autowired;
import com.ctnc.Constants;
import com.ctnc.Event;
import com.ctnc.SuggesterDto;
import com.ctnc.shared.IndexKey;
import com.ctnl.events.merge.MergeException;
import com.ctnl.events.merge.MergeQueryBuilder;
import com.ctns.merge.index.MergeIndex;
import com.ctns.merge.index.MergeIndexException;
import com.ctnu.environment.Environment;

public class SuggesterByEventName{

	private Logger logger = Logger.getLogger(SuggesterByEventName.class);

	@Autowired 
	private SuggesterProperties suggesterProperties;
	
	@Autowired
	private MergeIndex mergeIndex;

	@Autowired
	private MergeQueryBuilder mergeQueryBuilder;
	
	private Map<Long,Long> invertSuggesterIndex = new HashMap<Long,Long>();	
	private Map<Long,List<Long>> suggesterIndex = new HashMap<Long, List<Long>>();
	
	public List<SuggesterDto> build(Map<String, List<Event>> events) throws SuggesterException
	{		
		int totalEventsCounter = 0;
		
		List<SuggesterDto> suggesters = new ArrayList<SuggesterDto>(10000);
		
		for(Map.Entry<String, List<Event>>  segment : events.entrySet())
		{				
			totalEventsCounter+=segment.getValue().size();
				
			logger.info("---"+segment.getKey()+"--- " + segment.getValue().size());
			
			for(Event event : segment.getValue())
			{
				
				if( this.invertSuggesterIndex.containsKey( event.getInternalId() ) )
				{
					logger.info("Find event id :"+ event.getInternalId() +" in invert index with parent : "+this.invertSuggesterIndex.get( event.getInternalId()));
					continue;
				}

				Query query;
				try 
				{
					query = this.mergeQueryBuilder.eventName(event);
				} 
				catch (MergeException e1) 
				{
					throw new SuggesterException(e1);
				}
				
				List<Document> documents = null;
				try 
				{
					documents = this.mergeIndex.search( query , suggesterProperties.getMaxResults() );		 
				} 
				catch (MergeIndexException e) 
				{
					throw new SuggesterException(e);
				}	
				
				logger.info("Results number : "+documents.size());
				
				SuggesterDto suggester = new SuggesterDto();
				suggester.setKeyword(event.getName());
				suggester.setQuery(query.toString());
				suggester.setRawKeyword("?");
				suggester.setTriggerEventInternalId(event.getInternalId());
				suggester.setIndexKey(IndexKey.event);
				
				this.suggesterIndex.put( event.getInternalId() , new ArrayList<Long>(documents.size()) );
				
				suggester.setEvents( new HashSet<Long>(documents.size()) );
				
				suggester.setSegmentedKeys( new HashSet<String>(documents.size()) );
				suggester.getSegmentedKeys().add(event.getSegmentKey());
				
				suggester.setKeywords( new HashSet<String>(documents.size()) );
				suggester.getKeywords().add(event.getName());
				
				BooleanQuery booleanQuery  = (BooleanQuery)query;
				int clausesLength = booleanQuery.getClauses().length;
				logger.info("Query clause length : "+booleanQuery.getClauses().length);
				
				for(Document document : documents)
				{
					int termsNumber;
					
					try 
					{
						termsNumber = 
								this.mergeQueryBuilder.queryFieldBuilder( Constants.EVENT_NAME , document.getField(Constants.EVENT_NAME).stringValue())
								.size();
					} 
					catch (MergeException e) 
					{
						throw new SuggesterException(e);
					}
					
					Long internalId = Long.valueOf(document.getField(Constants.EVENT_INTERNAL_ID).stringValue());
					String name = document.getField(Constants.EVENT_NAME).stringValue();
					String segmentedKey = document.getField(Constants.SEGMENT_KEY).stringValue();
					
					if(termsNumber==clausesLength)
					{
						this.invertSuggesterIndex.put( internalId , event.getInternalId());	
						
						suggester.getEvents().add(internalId);
					
						suggester.getSegmentedKeys().add(segmentedKey);
						
						suggester.getKeywords().add(name.toLowerCase());
					}
					else
					{
						logger.info("Escaped events id : "+internalId+",[Name]"+name+",Expected terms number : "+clausesLength+" Actual terms number : "+termsNumber);
					}					
				}
				
				suggester.setWeight(suggester.getEvents().size());
				suggesters.add(suggester);
				logger.info(suggester+Environment.getLineSeparator());
			}
		}
		
		logger.info("Total suggesters number : "+suggesters.size());
		logger.info("Total events number : "+totalEventsCounter);
		
		return suggesters;
	}

	public String buildSpell(List<SuggesterDto> suggestersByEventName) {

		StringBuffer buffer = new  StringBuffer();
		for(SuggesterDto suggesterByEventName : suggestersByEventName)
		{
			buffer.append(suggesterByEventName.getKeyword().toLowerCase());
			buffer.append(Environment.getLineSeparator());
		}
		
		return buffer.toString();
	}
}
