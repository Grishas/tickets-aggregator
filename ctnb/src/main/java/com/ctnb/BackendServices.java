package com.ctnb;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.suggest.Lookup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import com.ctnc.Constants;
import com.ctnc.shared.CompareRequest;
import com.ctnc.shared.CompareResponse;
import com.ctnc.shared.IndexKey;
import com.ctnc.shared.QueryLocationIndex;
import com.ctnc.shared.SearchRequest;
import com.ctnc.shared.SearchResponse;
import com.ctnc.shared.SearchResult;
import com.ctnc.shared.Source;
import com.ctnc.shared.SuggestionRequest;
import com.ctnc.shared.Tickets;
import com.ctnc.shared.TicketsBlock;
import com.ctnc.shared.UserLocation;
import com.ctns.geocoding.GeocodingException;
import com.ctns.geocoding.GeocodingService;
import com.ctns.merge.index.MergeIndexException;
import com.ctns.merge.index.MergeIndexResponse;
import com.ctns.merge.index.MergeIndexService;
import com.ctns.query.QueryBuilder;
import com.ctns.query.QueryBuilderException;
import com.ctns.suggester.index.SuggesterIndexException;
import com.ctns.suggester.index.SuggesterIndexService;
import com.ctnu.UtilsException;
import com.ctnu.date.DateUtils;
import com.ctnu.environment.Environment;
import com.ctnu.http.HttpService;
import com.ctnu.mail.Gmail;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class BackendServices {
	
//	public BackendServices()
//	{
//		System.out.println("BackendServices");
//		new Throwable().printStackTrace(System.out);
//	}
	
	@Autowired private SuggesterIndexService suggester;

	@Autowired private MergeIndexService search;
	
	@Autowired private TicketsService tickets;
	
	@Autowired private HttpService httpService;
	
	@Autowired private BackendProperties properties;
	
	@Autowired private GeocodingService geocoding;
	
	@Autowired private QueryBuilder query;
	
	@Autowired private PageCacheService pageCacheService;
	
	private final static long HOURS_24_MINUS_1_MS = (86400000-1);

	private Logger logger = Logger.getLogger(BackendServices.class);
	
	public String getPageCache(String key)
	{
		return this.pageCacheService.tryToGetPageFromCache(key);
	}
	
	public void addPageToCache(String key,String data)
	{
		this.pageCacheService.addPageToCache(key, data);
	}
	
	
	
	public SearchResponse search(SearchRequest request)throws BackendException {
		
		SearchResponse response = new SearchResponse();
		response.setMokeIp(this.isEnableMokeIp());

		this.normalizeRequestDates(request,response);
		
		MergeIndexResponse mergeIndexResponse = null;
		List<SearchResult> results  = null;
				
		switch (request.getHowToSearchLocation()) {
		
			case city:
						
				//try to search by user city 
				try {
					mergeIndexResponse = this.search.pagingSearch( 
							request.getRangeLength(), request.getRangeStart(), 
							request.getQuery(),
							request.getFrom_(),request.getTo_(),
							request.getSearchLocation().getCity(),request.getSearchLocation().getSegmentedKey(),
							request.getQueryFieldName());
				} catch (MergeIndexException e) {
					throw new BackendException(e);
				}
				
				this.prepareResults(QueryLocationIndex.city,mergeIndexResponse,results,request,response);
				
				return response;
					
			case geo:
				
				try //can't get lat/lon
				{
					this.completeGeocoding(request);
				} 
				catch (GeocodingException e1) 
				{
					logger.error(e1);
				
					return this.ignoreLocation(QueryLocationIndex.ignore, mergeIndexResponse, results, request, response);
				}

				try 
				{
					mergeIndexResponse = this.search.pagingSearch( 
							request.getRangeLength(), 
							request.getRangeStart(), 
							request.getQuery(),
							request.getFrom_(),request.getTo_(),
							request.getQueryFieldName(),
							request.getSearchLocation().getLatitude(),request.getSearchLocation().getLongitude());
					
					this.prepareResults(QueryLocationIndex.geo,mergeIndexResponse,results,request,response);
									
					return response;

				} 
				catch (MergeIndexException e1) 
				{
					logger.error(e1);
					
					return this.ignoreLocation(QueryLocationIndex.ignore, mergeIndexResponse, results, request, response);
				}
							
			case ignore:
				
				return this.ignoreLocation(QueryLocationIndex.ignore,mergeIndexResponse,results,request,response);
		}
		
		return null;		
	}

	private void completeGeocoding(SearchRequest request) throws GeocodingException
	{
		if(request.getSearchLocation().getLatitude()==0||request.getSearchLocation().getLongitude()==0)
		{
			com.ctnc.shared.Location location = this.geocoding.getLocation(
					request.getSearchLocation().getSegmentedKey(), 
					request.getSearchLocation().getCity());
			
			request.getSearchLocation().setLatitude(location.getLatitude());
			request.getSearchLocation().setLongitude(location.getLongitude());
		}
		
//		if(request.getSearchLocation().getCity()==null||request.getSearchLocation().getSegmentedKey()==null)
//		{
//			GeocodingResult[] geocodingResult = 
//					this.geocoding.reverseGeocode(
//							request.getSearchLocation().getLatitude(),
//							request.getSearchLocation().getLongitude());
//			
//			
//			System.out.println(geocodingResult);
//			//request.getSearchLocation().setCity(city);
//			//request.getSearchLocation().setSegmentedKey(segmentedKey);
//		}
	}
	
	private SearchResponse ignoreLocation(QueryLocationIndex howToSearchLocationResolved, MergeIndexResponse mergeIndexResponse, 
			List<SearchResult> results, SearchRequest request,SearchResponse response) throws BackendException
	{
		//try to search by user city 
		try {
			mergeIndexResponse = this.search.pagingSearch( 
					request.getRangeLength(), request.getRangeStart(), 
					request.getQuery(),
					request.getFrom_(),request.getTo_(),
					null,null,
					request.getQueryFieldName());
		} catch (MergeIndexException e) {
			throw new BackendException(e);
		}
		
		this.prepareResults(QueryLocationIndex.ignore,mergeIndexResponse,results,request,response);

		return response;
	}
	
	
	private void prepareResults(QueryLocationIndex howToSearchLocationResolved, MergeIndexResponse mergeIndexResponse, 
			List<SearchResult> results, SearchRequest request,SearchResponse response)
	{
		results = this.toResults( mergeIndexResponse, request.getQuery() , request );
		
		response.setResults(results);
		response.setTotalResults(mergeIndexResponse.getTotalHits());
		
		response.setQuery(request.getQuery());
		response.setQueryFieldName(request.getQueryFieldName());
		
		response.setHowToSearchLocation(request.getHowToSearchLocation());
		response.setCurrentUserLocation(request.getCurrentUserLocation());
		
		this.tryToCompleteCityAndSegKey(request,response,results);
		
		response.setHowToSearchLocationResolved(howToSearchLocationResolved);
		
		this.checkAndCorrectSpelling(mergeIndexResponse,request,response);
		
	}
	
	

	private void checkAndCorrectSpelling(MergeIndexResponse mergeIndexResponse, SearchRequest request, SearchResponse response) {
		// check spelling
		if (mergeIndexResponse.getTotalHits() == 0 && request.getQuery() != null && request.getQuery().length() > 0
				&& request.getQueryFieldName() != null) {
			Query query = null;
			String field = null;
			try {

				switch (request.getQueryFieldName()) {
				case event:
					field = Constants.EVENT_NAME;
					break;

				case performer:
					field = Constants.PERFORMER_NAME;
					break;

				case venue:
					field = Constants.VENUE_NAME;
					break;
				}

				query = this.query.buildQuery(field, request.getQuery());
			} catch (QueryBuilderException e1) {
				logger.error(e1);
				response.setSuggested(null);
				return;
			}

			int haveKeyword = -1;
			try {
				haveKeyword = this.search.search(query, 1).size();
			} catch (MergeIndexException e1) {
				logger.error(e1);
				response.setSuggested(null);
				return;
			}

			if (haveKeyword == 0)// no "justin beber" in the house
			{
				try {
					String suggested = suggester.suggestSimilar(request.getQueryFieldName(), request.getQuery());
					response.setSuggested(suggested);
				} catch (SuggesterIndexException e) {
					logger.error(e);
					response.setSuggested(null);
					return;
				}
			}
		}
	}	
	
	private void tryToCompleteCityAndSegKey(SearchRequest request,SearchResponse response,List<SearchResult> results)
	{
		response.setSearchLocation(request.getSearchLocation());

		if( ( results.size() > 0 ) && ( request.getSearchLocation().getCity()==null||request.getSearchLocation().getSegmentedKey()==null ) )
		{			
			response.getSearchLocation().setCity(results.get(0).getRelatedCityNames().get(0));
			
			response.getSearchLocation().setSegmentedKey(results.get(0).getSegmentKey());	
		}
		else if(   ( results.size() == 0 ) 
				&& ( request.getSearchLocation().getCity()==null  || request.getSearchLocation().getSegmentedKey()==null )
				&& ( request.getSearchLocation().getLatitude()!=0 && request.getSearchLocation().getLongitude()!=0 )
				)
		{
			try {
				MergeIndexResponse mergeIndexResponse = 
						this.search.pagingSearch(1, 1, request.getSearchLocation().getLatitude(), request.getSearchLocation().getLongitude());
				
				if(mergeIndexResponse.getTotalHits()>0)
				{
					Document document = mergeIndexResponse.getDocuments().get(0);
					response.getSearchLocation().setCity(document.get(Constants.CITY_NAME));
					response.getSearchLocation().setSegmentedKey(document.get(Constants.SEGMENT_KEY));				
				}
				else
				{
					response.getSearchLocation().setCity("");
					response.getSearchLocation().setSegmentedKey("");	
					logger.error("Failure in city and seg key resolve by lat/lot:"+request.getSearchLocation().getLatitude()+"/"+request.getSearchLocation().getLongitude());
				}

			} catch (MergeIndexException e) {
				response.getSearchLocation().setCity("");
				response.getSearchLocation().setSegmentedKey("");	
				logger.error("Failure in city and seg key resolve by lat/lot:"+request.getSearchLocation().getLatitude()+"/"+request.getSearchLocation().getLongitude(),e);
			}
		}
	}
	
	
	
	
	
	
	
	
	private List<SearchResult> toResults(MergeIndexResponse mergeIndexResponse,String query,SearchRequest request)
	{
		List<SearchResult> results = new ArrayList<SearchResult>();
		
		//test
		int index = 0;
		//test
		for(Document document : mergeIndexResponse.getDocuments())
		{
			SearchResult result = new SearchResult();
			
			result.setInternalEventId(Integer.valueOf(document.get(Constants.EVENT_INTERNAL_ID)));
			result.setEventName(document.get(Constants.EVENT_NAME));
			result.setSegmentKey(document.get(Constants.SEGMENT_KEY));
			
			for(String relatedCity : document.getValues(Constants.CITY_NAME))
			{
				result.getRelatedCityNames().add(relatedCity);
			}
			
//			String[] venueNames = document.getValues(Constants.VENUE_NAME);
//			for(String venueName : venueNames){
//				result.getVenuesName().add(venueName);
//			}
			
			
			
			//venue
			String venueName = null;
			if(request.getQueryFieldName()!=null&&request.getQueryFieldName().equals(IndexKey.venue)&&query!=null)
			{
				query = query.replace("-"," ");
				for(String name : document.getValues(Constants.VENUE_NAME))
				{
					if(name.trim().toLowerCase().equals(query.trim().toLowerCase()))
					{
						venueName = name;
						break;
					}
					
					if(name.trim().toLowerCase().contains(query.trim().toLowerCase()))
					{
						venueName = name;
					}
				}
			}
			
			if(venueName==null)
			{
				venueName = document.get(Constants.VENUE_NAME);
			}
			
			result.setVenueName(venueName);

			
			
			
			
			
			result.setDate(document.get(Constants.DISPLAY_DATE));
			
			
			
			
			
			
			
			
			
			
			
//			boolean isFinalTime = Boolean.valueOf(document.get(Constants.IS_FINAL_TIME));
//			result.setFinalTime(isFinalTime);

			
			
			
			
			
			
			double latitude = Double.valueOf(document.get(Constants.LATITUDE));
			double longitude = Double.valueOf(document.get(Constants.LONGITUDE));
			result.setLatitude(latitude);
			result.setLongitude(longitude);
			
			String[] sourceEventId = document.getValues(Constants.SOURCE_EVENT_ID);
			Map<Source,Long> source_EventId = new HashMap<Source, Long>();	
			for(String source : sourceEventId){
					String[] tmp = source.split("_");
					Source source_ = Source.valueOf(tmp[0]);
					long eventId_ = Long.valueOf(tmp[1]);
					source_EventId.put(source_,eventId_);
				}
			result.setSources(source_EventId);
			
			String[] performers = document.getValues(Constants.PERFORMER_NAME);
			if(performers!=null&&performers.length>0){
				for(String performer : performers){
					if(performer.length()>0){
						result.getPerformersName().add(performer);
					}
				}
			}
			
			//test
			this.test(document,result,mergeIndexResponse,index);
			index++;
			//test
			
			results.add(result);
		}
		
		return results;	
	}
	
	private void test(Document document,SearchResult searchDocument,MergeIndexResponse mergeIndexResponse,int index)
	{
		//test
		String[] related = document.getValues(Constants.SOURCE_EVENT_ID);
		StringBuilder ids = new StringBuilder();
		if(related!=null){
			ids.append("][related ids:");
			for(String id : related){
				ids.append(id+",");
			}
			ids.deleteCharAt(ids.length()-1);
			ids.append("]");
		}
		
		searchDocument.setTest(
				"[internal id: "+document.get(Constants.EVENT_INTERNAL_ID)+
				"][index:"+mergeIndexResponse.getTest().get(index)+
				"][seg key: "+document.get(Constants.SEGMENT_KEY)+
				ids.toString()
				);
		
		//test
	}
	
	
	
	
	
	
	
	
	
	
	
	
	private void normalizeRequestDates(SearchRequest request,SearchResponse response)
	{
		if( request.getFrom()!=null && request.getTo()!=null )
		{
			//from date
			Date from = null;
			try {
				from = DateUtils.parseDate( request.getFrom() , DateUtils.DATE_FORMAT );
			} catch (UtilsException e) {
				logger.error(e);
				this.setDateDefaultCase(request,response);
				return;
			}
			//from = DateUtils.clearTime( from );
			
			//--------------------------------------------------------------------------------
			
			//to date
			Date to = null;
			try {
				to = DateUtils.parseDate( request.getTo() , DateUtils.DATE_FORMAT );
			} catch (UtilsException e) {
				logger.error(e);
				this.setDateDefaultCase(request,response);
				return;
			}
			//to = DateUtils.clearTime( to );
			to = DateUtils.addMillisecondsToDate(to,HOURS_24_MINUS_1_MS);
			
			//check that from not in past.if in past set from to today and to plus one year
			
			//today
			Date today 	= this.getToday().getTime();
			
			long fromMinusTodayGap = from.getTime() - today.getTime();
			
			//from date in the past
			if( fromMinusTodayGap < 0 )
			{
				request.setFrom_(today);
				
				Calendar todayPlus1Year = this.getToday();
				todayPlus1Year.add(Calendar.YEAR,1); 
				request.setTo_(todayPlus1Year.getTime());
			
				//date as strings for client
				request.setFrom(DateUtils.dateToStringByFormat(today, DateUtils.DATE_FORMAT));
				request.setTo(DateUtils.dateToStringByFormat(todayPlus1Year.getTime(), DateUtils.DATE_FORMAT));
				return;
				
			}
			else
			{
				//for search query
				request.setFrom_(from);
				request.setTo_(to);
				
				//date as strings for client
				response.setFrom(request.getFrom());
				response.setTo(request.getTo());
				return;
			}
		}
		else//no dates provide. set range from today + 10 years 
		{
			this.setDateDefaultCase(request,response);
			return;
		}		
	}
	
	//from today + 10
	private void setDateDefaultCase(SearchRequest request, SearchResponse response)
	{
		response.setFrom(null);
		response.setTo(null);
		
		Date today 	= this.getToday().getTime();
		request.setFrom_(today);
		
		Calendar todayPlus10Years = this.getToday();
		
		todayPlus10Years.add(Calendar.YEAR,10); 
		
		request.setTo_(todayPlus10Years.getTime());
	}
	
	
	
	
	
	private Calendar getToday()
	{
		Calendar today = Calendar.getInstance();
		today.setTimeInMillis(System.currentTimeMillis());
		today.clear(Calendar.HOUR_OF_DAY);
		today.clear(Calendar.HOUR);
		today.clear(Calendar.AM_PM);
		today.clear(Calendar.MINUTE);
		today.clear(Calendar.SECOND);
		today.clear(Calendar.MILLISECOND);
		return today;
	}

	
	public void prepareUserLocation(UserLocation userLocation,boolean doForceResolve)
	{		
		if( userLocation.getCity()==null || userLocation.getSegmentedKey()==null || 
			userLocation.getLatitude()==0d || userLocation.getLongitude()==0d || doForceResolve==true){
			
			try 
			{
				this.getSetUserLocationByIp(userLocation);
				
				this.setUserLocation(userLocation);

			} 
			catch (BackendException e) 
			{
				userLocation.setResolveLocationStatus("failure");
				this.setUserLocation(userLocation);
				
				this.logger.error("Set default user location: "+userLocation+".Error is: "+e);

			}
		}
	}
	
	private void setUserLocation(UserLocation userLocation)
	{
		//set default in case of failure
		if(userLocation.getResolveLocationStatus().equals(Constants.FAILURE))
		{
			userLocation.setCity("New York");
			userLocation.setSegmentedKey("us_ny");
			userLocation.setResolveLocationStatus(Constants.FAILURE);
		}
		else
		{
			userLocation.setResolveLocationStatus(Constants.SUCCESS);
		}
	}
	
	
	
	public boolean isEnableMokeIp()
	{
		return this.properties.isEnableMokeIp();
	}
	
	
	public boolean mokeIp(UserLocation userLocation)
	{		
		if(this.properties.isEnableMokeIp()==true)
		{
			if(this.properties.getMokeIpTarget()!=null && this.properties.getMokeIpTarget().length()>0
					&&
			   this.properties.getMokeIpSourceList()!=null && this.properties.getMokeIpSourceList().length>0 ){
			
				for(String sourceIp : this.properties.getMokeIpSourceList())
				{
					if(userLocation.getIp().equals(sourceIp))	
					{
						String originalIp = userLocation.getIp();
												
						userLocation.setIp(this.properties.getMokeIpTarget());
						
						this.logger.info("Moke IP mode is on: "+originalIp+" -> "+this.properties.getMokeIpTarget());
						
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	private void getSetUserLocationByIp(UserLocation userLocation) throws BackendException
	{
		String ip = userLocation.getIp();
		
		try 
		{
			//free String data = httpService.get("http://ip-api.com/json/"+ip);

			//1-may-2016 --- 1-may-2017 pay 160 eur
			String data = httpService.get("https://pro.ip-api.com/json/"+ip+"?key=A5albHEHZER6TDh");

			JsonParser parser = new JsonParser();
			JsonObject object = parser.parse(data).getAsJsonObject();
		
			if(object.get("status").getAsString().equals("success"))
			{
				userLocation.setSegmentedKey( (object.get("countryCode").getAsString() +"_"+ object.get("region").getAsString()).toLowerCase() );
				userLocation.setCity( object.get("city").getAsString() );
				userLocation.setLatitude( object.get("lat").getAsDouble() );
				userLocation.setLongitude( object.get("lon").getAsDouble() );
				userLocation.setCountryCode( object.get("countryCode").getAsString() );
				userLocation.setRegion( object.get("region").getAsString() );
				userLocation.setIp(ip);
				userLocation.setResolveLocationStatus("success");
			}
			else
			{
				userLocation.setResolveLocationStatus("failure");
				userLocation.setIp(ip);
			}
								
		} catch (UtilsException e) {
			
			userLocation.setResolveLocationStatus("failure");
			userLocation.setIp(ip);
			
			throw new BackendException(e);
		}
	}
	
	
	
	
	
	public List<Document> search(String query)throws BackendException {
		try {
			 return this.search.search(query);
		} catch (MergeIndexException e) {
			throw new BackendException(e);
		}
	}
	
	public List<Lookup.LookupResult> getSuggesters(SuggestionRequest request) throws BackendException {
		try {
			return this.suggester.getSuggestions(request);
		} catch (SuggesterIndexException e) {
			throw new BackendException(e);
		}
	}
	
	public int getSuggesterMaxNumberOfItemsToReturn(){
		return this.suggester.getSuggesterMaxNumberOfItemsToReturn();
	}

	public void checkout(String checkout) {
		this.logger.info("Checkout: "+checkout);
		this.immidiateReport("Checkout", checkout);
	}
	
	public CompareResponse getTickets(CompareRequest request) throws BackendException{	

		CompareResponse response = new CompareResponse();
		//fix this !!!!!
		response.setCurrency("$");

		boolean requestConfirmed = 
				this.tickets.getSetConfirmRequest(request,response);

		if(requestConfirmed==false){
			response.setTickets(new ArrayList<Tickets>(0));
			return response;
		}
		
		List<Tickets> tickets = this.tickets.getTickets(request.getRequests());
		response.setTickets(tickets);
		
		if(this.logger.isTraceEnabled())
		{
			for(Tickets tickets_ : tickets)
			{
				this.logger.trace("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
				
				this.logger.trace("Status: "+tickets_.getStatus());
				
				this.logger.trace(tickets_);
				
				for(Map.Entry<String,List<TicketsBlock>> entry : tickets_.getBlocks().entrySet())
				{					
					for(TicketsBlock tb : entry.getValue())
					{
						this.logger.trace(tb);
					}
				}
				
				this.logger.trace("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
			}
		}
		
		return response;
	}

	private StringBuffer messages = new StringBuffer();
	
	public void report(String title, String message) {

		
		messages.append("Title: "+	Calendar.getInstance().getTime().toString()+"---"+title+Environment.getLineSeparator()+
				message+Environment.getLineSeparator()+Environment.getLineSeparator());
	}
	
	public void immidiateReport(String title, String message) {

		try {
			if(properties.isSendEmailsEnable()){
			
				Gmail.send("compareticketsnow", properties.getGmailPassword(), properties.getMaintenanceToEmails(),
					(title == null) ? "" : title, (message == null) ? "" : message);
				}
			
		} catch (AddressException e) {
			logger.error(e);
		} catch (MessagingException e) {
			logger.error(e);
		}	
	}
	
	@Scheduled(fixedRate=18000000)//each 5 hours - 60000*60*5
	private void report()
	{
		try {
			
			if(properties.isSendEmailsEnable()){
			
				Gmail.send("compareticketsnow", properties.getGmailPassword(), properties.getMaintenanceToEmails(),
					"System report", messages.toString());
				}
			
			this.messages.setLength(0);
			
		} catch (AddressException e) {
			logger.error(e);
		} catch (MessagingException e) {
			logger.error(e);
		}		
	}
}
