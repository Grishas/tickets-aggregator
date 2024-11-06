package com.ctnb;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import com.ctnc.Constants;
import com.ctnc.GetTicketsStatus;
import com.ctnc.SourceService;
import com.ctnc.shared.CompareRequest;
import com.ctnc.shared.CompareResponse;
import com.ctnc.shared.CompareResponseStatus;
import com.ctnc.shared.Source;
import com.ctnc.shared.Tickets;
import com.ctnc.shared.TicketsBlock;
import com.ctns.merge.index.MergeIndexException;
import com.ctns.merge.index.MergeIndexService;
import com.ctns.query.QueryBuilder;
import com.ctntc.TCService;
import com.ctntn.TNService;

public class TicketsService {
	
	private Logger logger = Logger.getLogger(TicketsService.class);

//	public TicketsService()
//	{
//		System.out.println("TicketsService");
//		new Throwable().printStackTrace(System.out);
//	}
	
	@Autowired
	private MergeIndexService index;
	
	@Autowired
	private ApplicationContext context;

	@Autowired
	private BackendProperties properties; 
	
	@Autowired
	private QueryBuilder query;
	
	private Map<String, List<Tickets>> ticketsCache  = new ConcurrentHashMap<String, List<Tickets>>();
	
	//@Scheduled(fixedRate=60000)//each minute 60000
	private void ticketsCashMaintanance()
	{
//		logger.info("Tickets cash maintanance: "+
//	Thread.currentThread().getName()+"."+this+"."+Calendar.getInstance().getTime());
		
		for(Map.Entry<String, List<Tickets>> entry : this.ticketsCache.entrySet())
		{
			for(Tickets tickets : entry.getValue())
			{
				Long timestamp = tickets.getTimestamp();
				
				Long gap = System.currentTimeMillis() - timestamp;
				
				if(gap >= properties.getKeepTicketsInCacheMs())
				{
					this.ticketsCache.remove(entry.getKey());
					//this.logger.info("Remove tickets entry from cash: "+entry.getKey()+" *running each 5 min");
				}
				else
				{
					break;//go to next entry
				}
			}
		}
	}
	
	public class Execute implements Callable<Tickets> {
		
		private SourceService sourceService = null;
		private Long eventId = 0l;
		
		public Execute(SourceService sourceService,Long eventId)
		{
			this.sourceService = sourceService;
			this.eventId = eventId;
		}
		
		@Override
		public Tickets call() throws Exception 
		{
			Tickets tickets  = null;
			
			int retryCounter = 0;
			
			do
			{
				try
				{
					tickets = this.sourceService.getTickets(eventId);	
					
					if( tickets == null )
					{
						retryCounter++;
						
						logger.error("Failure null in get tickets.Going to "+retryCounter+" retry...");
						
						Thread.sleep(500);
						
						continue;
					}
					else
					{
						//for cashing timeout
						tickets.setTimestamp(System.currentTimeMillis());
						
						return tickets;
					}
				}
				catch( Throwable t)
				{
					retryCounter++;

					logger.error("Failure in get tickets.Going to "+retryCounter+" retry...",t);
								
					Thread.sleep(500);

					continue;
				}
			}
			while( retryCounter != properties.getGetTicketsRetryThreshold() );
			
			//return unexpected error
			tickets = new Tickets();
			tickets.setStatus(GetTicketsStatus.unexpected_error);
			//tickets.setSource(Source.ticketcity);	
			tickets.setBlocks(new HashMap<String,List<TicketsBlock>>(0));	
			
			return tickets;
		}	
	}
	
	
	private SourceService getSourceService(Source source){
		
		switch (source) {	
		
		case ticketcity:
			return this.context.getBean(TCService.class);
			
		case ticketnetwork:
			return this.context.getBean(TNService.class);
		}
		return null;
		
	}
	
	private String getRequestKey(Map<Source,Long> requests)
	{
		StringBuilder key = new StringBuilder();
		
		for(Map.Entry<Source,Long> request : requests.entrySet())
		{
			key.append(request.getKey().name()+"="+request.getValue()+",");
		}
		
		return key.toString();
	}
	
	public List<Tickets> getTickets(Map<Source,Long> requests)
	{
		//start search in cash
//		String cacheKey = this.getRequestKey(requests);
//		List<Tickets> ticketsFromCache = this.tryToGetFromTicketsCache(cacheKey);
//		if(ticketsFromCache!=null){
//			logger.info("Return tickets results from cash");
//			return ticketsFromCache;
//		}
		//end search in cash
	
		
		
		final ExecutorService service = Executors.newFixedThreadPool( requests.size() );     
				
	    final List<Future<Tickets>> tasks = new ArrayList<Future<Tickets>>(requests.size());
	            
	    for(Map.Entry<Source,Long>  request : requests.entrySet())
	    {
	    	SourceService sourceService =  this.getSourceService(request.getKey());
		
	    	tasks.add( service.submit( new Execute(sourceService,request.getValue()) ) );
		}
		
		final List<Tickets> result = new ArrayList<Tickets>(requests.size());
		
		for(Future<Tickets> task : tasks)
		{
			try {
				
				Tickets tickets = task.get(properties.getGetTicketsTimeout(),TimeUnit.MILLISECONDS );
				
				switch (tickets.getStatus()) {
				case found_tickest:
					result.add( tickets ) ;
					break;
				case not_found_tickest:
					this.logger.info("No tickests available.");
					break;
				case access_error:
					this.logger.info("API key issue");
					break;
				case unexpected_error:
					this.logger.info("Unexpected error");
					break;				
				}			
			} catch (InterruptedException e) {
				this.logger.error(e.getMessage());
			} catch (ExecutionException e) {
				this.logger.error(e.getMessage());
			} catch (TimeoutException e) {
				this.logger.error(e.getMessage());
			}
		}
		
	    service.shutdownNow();
	    
	    //start update tickets cash
//	    if(result!=null&&result.size()>0){
//	    	this.ticketsCache.put(cacheKey,result);
//	    }
	    //end update tickets cash
		
	    return result;
	}
	
	
	private List<Tickets> tryToGetFromTicketsCache(String requestKey) 
	{
		return this.ticketsCache.get(requestKey);
	}

	public boolean getSetConfirmRequest(CompareRequest request,CompareResponse response) throws BackendException
	{
		Query query = this.query.generateQueryBySourceEventId(request.getRequests());

		List<Document> results = null;
		try {
			results = this.index.search(query,1);
		} catch (MergeIndexException e) {
			throw new BackendException(e);
		}
		
		boolean confirmed = false;
		
		if(results.size()==0)
		{
			this.logger.error("Request was not confirmed.Query: "+ query+".Result: 0");
			
			response.setStatus(CompareResponseStatus.source_not_found);
			
			return false;//request not in index therefore not confirmed
		}
		else
		{
			Document document = results.get(0);
			
			confirmed = this.deepValidate(document,request);
			
			if(confirmed==true)
			{
				this.completeCompareResponse(document,response);
				
				this.logger.info("Request was confirmed Query: "+ query+".Result: "+results.size());
				
				response.setStatus(CompareResponseStatus.confirmed);

				return true;
			}
			else
			{
				this.logger.info("Request was confirmed Query: "+ query+".Result: "+results.size());
				
				response.setStatus(CompareResponseStatus.req_res_data_mismash);
				
				return false;
			}
		}	
	}
	
	private void completeCompareResponse(Document document,CompareResponse response)
	{
		String dateForMap = document.get(Constants.DATE_FOR_TICKET_UTILS_MAP);
		String[] eventSourceName = document.getValues(Constants.EVENT_NAME_SOURCE_FINAL);
		String[] venueSourceName = document.getValues(Constants.VENUE_NAME_SOURCE_FINAL);
	
		response.setDateForMap(dateForMap);
		response.setEventSourceName(eventSourceName);
		response.setEventSourceVenue(venueSourceName);
		
		//---
		
		response.setEventName(document.get(Constants.EVENT_NAME));
		response.setEventDate(document.get(Constants.DISPLAY_DATE));
		response.setEventSegmentKey(document.get(Constants.SEGMENT_KEY));
		
		for(String relatedCity : document.getValues(Constants.CITY_NAME))
		{
			response.setEventCity(relatedCity);
			break;
		}
		
		for(String venueName : document.getValues(Constants.VENUE_NAME)){
			response.setEventVenue(venueName);
			break;
		}
	}
	
	private boolean deepValidate(Document document,CompareRequest request){
		
//		String eventName = document.get(Constants.EVENT_NAME).trim().toLowerCase();
//		if( ! request.getEventName().trim().toLowerCase().equals(eventName) )
//		{
//			this.logger.error("[request]"+request.getEventName().trim().toLowerCase() +" != "+eventName+"[document]");
//			return false;
//		}

		if( ! request.getDate().trim().toLowerCase().equals(document.get(Constants.DISPLAY_DATE).trim().toLowerCase()))
		{
			return false;
		}

		if( ! request.getSegmentKey().trim().toLowerCase().equals(document.get(Constants.SEGMENT_KEY).trim().toLowerCase()))
		{
			return false;
		}
		
		return true;
		
//		for(String relatedCity : document.getValues(Constants.CITY_NAME))
//		{
//			response.getRelatedCityNames().add(relatedCity);
//		}
//		
//		String[] venueNames = document.getValues(Constants.VENUE_NAME);
//		for(String venueName : venueNames){
//			response.getVenuesName().add(venueName);
//		}
				
		
//		double latitude = Double.valueOf(document.get(Constants.LATITUDE));
//		double longitude = Double.valueOf(document.get(Constants.LONGITUDE));
//		response.setLatitude(latitude);
//		response.setLongitude(longitude);
			
//		String[] performers = document.getValues(Constants.PERFORMER_NAME);
		
//		if(performers!=null&&performers.length>0){
//			for(String performer : performers){
//				if(performer.length()>0){
//					result.getPerformersName().add(performer);
//				}
//			}
//		}
	}
}
