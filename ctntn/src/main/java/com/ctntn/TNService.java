package com.ctntn;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import com.ctnc.Country;
import com.ctnc.Event;
import com.ctnc.SourceException;
import com.ctnc.SourceService;
import com.ctnc.State;
import com.ctnc.shared.Tickets;
import com.ctntn.dto.internal.Category;
import com.ctntn.dto.internal.EventPerformer;
import com.ctntn.dto.internal.Venue;
import com.ctnu.UtilsException;
import com.ctnu.environment.Environment;
import com.ctnu.http.CtnHttpRequest;
import com.ctnu.http.CtnHttpResponse;
import com.ctnu.http.HttpService;

public class TNService implements SourceService{

	
	@Autowired
	private HttpService httpService;

	@Autowired 
	private TNGetEventsParser tnGetEventsParser;
	
	@Autowired 
	private TNProperties properties;
	
	private Logger logger = Logger.getLogger(TNService.class);

	//cached on init for common events process , try to realease at end of index build
    private Map<Long,List<EventPerformer>> eventPerformers = null;
    private List<Venue> venues = null;
	private List<Category> categories = null;
    private List<Country> countries = null;
	
	public TNService(){}
    
    public List<EventPerformer> getPerformersByEventId(Long eventId){
    	return this.eventPerformers.get(eventId);
    }
    
    public Tickets getTickets(long eventId) throws SourceException
	{
		CtnHttpRequest httpRequest = new CtnHttpRequest();
		httpRequest.setCharset(this.properties.getCharset());
		httpRequest.setSoapAction(this.properties.getGetTicketsAction());

		String soapBody = this.properties.getGetTicketsRequest();

		soapBody = soapBody.replace("#eventID#",String.valueOf(eventId));
		soapBody = soapBody.replace("#websiteConfigID#",this.properties.getWebsiteConfigId());
		
		httpRequest.setSoapBody(soapBody);
		httpRequest.setUri(this.properties.getTargetEndpoint());
		
		CtnHttpResponse httpResponse = null;
		try {
			httpResponse = this.httpService.execute( httpRequest );
		} catch (UtilsException e) {
			throw new SourceException(e);
		}

		InputStream inputStream = new ByteArrayInputStream(httpResponse.getData());
		
		try {
			TNGetTicketsParser ticketsParser = new TNGetTicketsParser();
			Tickets tickets = ticketsParser.parse(inputStream,httpRequest.getCharset());
			this.logger.info(tickets.getSource().name()+" reply with status: "+ tickets.getStatus()+" in "+tickets.getBlocks().size()+" sections");
			return tickets;
		} catch (TNException e) {
			throw new SourceException(e);
		}
	}
    
	public List<Event> getEvents(State state) throws TNException 
	{
		CtnHttpRequest httpRequest = new CtnHttpRequest();
		httpRequest.setCharset(this.properties.getCharset());
		httpRequest.setSoapAction(this.properties.getGetEventsAction());

		String soapBody = this.properties.getGetEventsRequest();
//		try {
//			soapBody = UtilsIO.readAllBytes("../ctntn/src/resources/GetEvents",this.properties.getCharset());
//		} catch (UtilsException e) {
//			throw new TNException(e);
//		}
		
		soapBody = soapBody.replace("#stateID#",String.valueOf(state.getId()));
		soapBody = soapBody.replace("#websiteConfigID#",this.properties.getWebsiteConfigId());
		
		httpRequest.setSoapBody(soapBody);
		httpRequest.setUri(this.properties.getTargetEndpoint());
		
		CtnHttpResponse httpResponse = null;
		try {
			httpResponse = this.httpService.execute( httpRequest );
		} catch (UtilsException e) {
			throw new TNException(e);
		}

		InputStream inputStream = new ByteArrayInputStream(httpResponse.getData());
		
		List<Event> events = this.tnGetEventsParser.parse(
				inputStream,
				httpRequest.getCharset(),
				state,
		        this.properties.getEventDateFormat(),
		        this.properties.getEventDisplayDateWithoutTimeStringDetection());
	
		return events;
	}

	private List<Country> getCountries() throws TNException
	{
		CtnHttpRequest httpRequest = new CtnHttpRequest();
		httpRequest.setCharset(this.properties.getCharset());
		httpRequest.setSoapAction(this.properties.getGetCountriesAction());

		String soapBody = this.properties.getGetCountriesRequest();
//		try {
//			soapBody = UtilsIO.readAllBytes("../ctntn/src/resources/GetCountries",this.charset);
//		} catch (UtilsException e) {
//			throw new TNException(e);
//		}
//		
		soapBody = soapBody.replace("#websiteConfigID#",this.properties.getWebsiteConfigId());
		
		httpRequest.setSoapBody(soapBody);
		httpRequest.setUri(this.properties.getTargetEndpoint());
		
		CtnHttpResponse httpResponse = null;
		try {
			httpResponse = this.httpService.execute( httpRequest );
		} catch (UtilsException e) {
			throw new TNException(e);
		}

		InputStream inputStream = new ByteArrayInputStream(httpResponse.getData());
		
		return new TNGetCountriesParser().parse(inputStream,httpRequest.getCharset());
	}

	private List<Category> getCategoriesMasterList( ) throws TNException {

		CtnHttpRequest httpRequest = new CtnHttpRequest();
		httpRequest.setCharset(this.properties.getCharset());
		httpRequest.setSoapAction(this.properties.getGetCategoriesMasterListAction());

		String soapBody = this.properties.getGetCategoriesMasterListRequest();
//		try {
//			soapBody = UtilsIO.readAllBytes("../ctntn/src/resources/GetCategoriesMasterList",this.charset);
//		} catch (UtilsException e) {
//			throw new TNException(e);
//		}
		
		soapBody = soapBody.replace("#websiteConfigID#",this.properties.getWebsiteConfigId());
		
		httpRequest.setSoapBody(soapBody);
		httpRequest.setUri(this.properties.getTargetEndpoint());
		
		CtnHttpResponse httpResponse = null;
		try {
			httpResponse = this.httpService.execute( httpRequest );
		} catch (UtilsException e) {
			throw new TNException(e);
		}

		InputStream inputStream = new ByteArrayInputStream(httpResponse.getData());
		
		return new TNGetCategoriesMasterListParser().parse(inputStream,httpRequest.getCharset());
	}

	public List<State> getStates(long countryId ) throws TNException {

		CtnHttpRequest httpRequest = new CtnHttpRequest();
		httpRequest.setCharset(this.properties.getCharset());
		httpRequest.setSoapAction(this.properties.getGetStatesAction());

		String soapBody = this.properties.getGetStatesByCountryIdRequest();
//		try {
//			soapBody = UtilsIO.readAllBytes("../ctntn/src/resources/GetStatesByCountryId",this.charset);
//		} catch (UtilsException e) {
//			throw new TNException(e);
//		}

		soapBody = soapBody.replace("#countryID#", String.valueOf(countryId));
		soapBody = soapBody.replace("#websiteConfigID#",this.properties.getWebsiteConfigId());
		
		httpRequest.setSoapBody(soapBody);
		httpRequest.setUri(this.properties.getTargetEndpoint());
		
		CtnHttpResponse httpResponse = null;
		try {
			httpResponse = this.httpService.execute( httpRequest );
		} catch (UtilsException e) {
			throw new TNException(e);
		}

		InputStream inputStream = new ByteArrayInputStream(httpResponse.getData());
		
		List<State> states = new TNGetStatesParser().parse(inputStream,httpRequest.getCharset());
	
		return states;
	}
	
	private List<Country> getCountriesByAbbreviation(String[] countriesAbbreviation , List<Country> countries) throws TNException
	{
		List<Country> countriesByAbbreviation = new ArrayList<Country>(countriesAbbreviation.length);
		
		for(Country country : countries)
		{
			for(String countrieAbbreviation : countriesAbbreviation)
			{
				if(country.getAbbreviation()!=null && country.getAbbreviation().equals(countrieAbbreviation))
				{
					countriesByAbbreviation.add( country );
					
					if(countriesByAbbreviation.size()==countriesAbbreviation.length)
					{
						return countriesByAbbreviation;
					}
				}
			}
		}
		
		if(countriesByAbbreviation.size()!=countriesAbbreviation.length)
		{
			StringBuffer error = new StringBuffer();
			error.append("can't find all provided countries");
			error.append(Environment.getLineSeparator());
			for(String countrieAbbreviation : countriesAbbreviation){
				error.append(countrieAbbreviation);
				error.append(Environment.getLineSeparator());
			}
			
			throw new TNException(error.toString());
		}
		
		return countriesByAbbreviation;
	}

	public Map<Long,List<EventPerformer>> getEventPerformers() throws TNException
	{
		CtnHttpRequest httpRequest = new CtnHttpRequest();
		httpRequest.setCharset(this.properties.getCharset());
		httpRequest.setSoapAction(this.properties.getGetEventPerformersAction());

		String soapBody = this.properties.getGetEventPerformersRequest();
//		try {
//			soapBody = UtilsIO.readAllBytes("../ctntn/src/resources/GetEventPerformers",this.charset);
//		} catch (UtilsException e) {
//			throw new TNException(e);
//		}
		
		soapBody = soapBody.replace("#websiteConfigID#",this.properties.getWebsiteConfigId());
		
		httpRequest.setSoapBody(soapBody);
		httpRequest.setUri(this.properties.getTargetEndpoint());
		
		CtnHttpResponse httpResponse = null;
		try {
			httpResponse = this.httpService.execute( httpRequest );
		} catch (UtilsException e) {
			throw new TNException(e);
		}

		InputStream inputStream = new ByteArrayInputStream(httpResponse.getData());
		
		return new TNGetEventPerformersParser().parse(inputStream,httpRequest.getCharset());
	
	}
	
	public List<com.ctntn.dto.internal.Venue> getVenue() throws TNException
	{
		CtnHttpRequest httpRequest = new CtnHttpRequest();
		httpRequest.setCharset(this.properties.getCharset());
		httpRequest.setSoapAction(this.properties.getGetVenueAction());

		String soapBody = this.properties.getGetVenueRequest();
//		try {
//			soapBody = UtilsIO.readAllBytes("../ctntn/src/resources/GetVenue",this.charset);
//		} catch (UtilsException e) {
//			throw new TNException(e);
//		}
//		
		soapBody = soapBody.replace("#websiteConfigID#",this.properties.getWebsiteConfigId());
		
		httpRequest.setSoapBody(soapBody);
		httpRequest.setUri(this.properties.getTargetEndpoint());
		
		CtnHttpResponse httpResponse = null;
		try {
			httpResponse = this.httpService.execute( httpRequest );
		} catch (UtilsException e) {
			throw new TNException(e);
		}

		InputStream inputStream = new ByteArrayInputStream(httpResponse.getData());
		
		return new TNGetVenueParser().parse(inputStream,httpRequest.getCharset());
	
	}
	
	private com.ctntn.dto.internal.Venue getVenue(String venueId)
	{
		for(com.ctntn.dto.internal.Venue venue : this.venues)
		{
			if(venue.getId().equals(venueId))
			{
				return venue;
			}
		}
		
		return null;
	}

	public Map<String, List<Event>> getEventsByCountriesAbbreviation(String[] countriesAbbreviation) throws SourceException {
		
		try {
			this.eventPerformers = this.getEventPerformers();
		} catch (TNException e) {
			throw new SourceException(e);
		}
    	//this.venues = this.getVenue();
    	try {
			this.categories = this.getCategoriesMasterList();
		} catch (TNException e) {
			throw new SourceException(e);
		}
    	
    	try {
			this.countries = this.getCountries();
		} catch (TNException e) {
			throw new SourceException(e);
		}
		
		Map<String,List<Event>> events = new HashMap<String,List<Event>>();
		
		List<Country> countries = null;
		try {
			countries = this.getCountriesByAbbreviation( countriesAbbreviation , this.getCountries() );
		} catch (TNException e) {
			throw new SourceException(e);
		}
		
		for( Country country :  countries)
		{
			List<State> states=null;
			try {
				states = this.getStates( country.getId() );
			} catch (TNException e) {
				throw new SourceException(e);
			}
			
			for( State state :  states)
			{
				String segmentKey = this.tnGetEventsParser.getSegmentKey(country, state);

				List<Event> eventsByState;
				try {
					eventsByState = this.getEvents( state );
				} catch (TNException e) {
					throw new SourceException(e);
				}
				
				if(eventsByState.size()>0)
				{
					if(events.containsKey(segmentKey))//for uk
					{
						//for example tc not support state for uk
						events.get(segmentKey).addAll(eventsByState);
					}
					else
					{
						events.put( segmentKey , eventsByState );
					}
				}
			}
		}
		
		return events;
	}
	
	protected Category getCategory(long childCategoryId, long parentCategoryId, long grandChildCategoryId)
	{
		for(com.ctntn.dto.internal.Category category : this.categories){
			if(	category.getChildCategoryID()==childCategoryId
			&&	category.getParentCategoryID()==parentCategoryId
			&&	category.getGrandchildCategoryID()==grandChildCategoryId ){	
				return category;
			}
		}
		
		return null;
	}
	
	protected Country getCountry(long countryId) 
	{
		for(Country country : this.countries)
		{
			if(country.getId()==countryId)
			{
				return country;
			}
		}
		
		return null;
	}
}