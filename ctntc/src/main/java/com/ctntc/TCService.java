package com.ctntc;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.apache.log4j.Logger;

import com.ctnc.Country;
import com.ctnc.Event;
import com.ctnc.GetTicketsStatus;
import com.ctnc.SourceException;
import com.ctnc.SourceService;
import com.ctnc.shared.Tickets;
import com.ctnu.UtilsException;
import com.ctnu.http.CtnHttpRequest;
import com.ctnu.http.CtnHttpResponse;
import com.ctnu.http.HttpService;
import com.ctnu.http.HttpServiceNew;

public class TCService implements SourceService{

	@Autowired
	private HttpService httpService;
	
	@Autowired
	private TCProperties properties;
	
	private Logger logger = Logger.getLogger(TCService.class);
	
	private List<Country> countries = new ArrayList<Country>(); 

	public TCService(){}
	
    public Tickets getTickets(long eventId) throws SourceException  
	{
		CtnHttpRequest httpRequest = new CtnHttpRequest();
		httpRequest.setCharset(this.properties.getCharset());
		httpRequest.setSoapAction(this.properties.getGetTicketBlocksAction());

		String soapBody = this.properties.getGetTicketBlocksRequest();
		
		soapBody = soapBody.replace("#EventID#",String.valueOf(eventId));
		soapBody = soapBody.replace("#APIKey#",this.properties.getApiKey());
		
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
			TCGetTicketBlocksParser ticketBlocksParser = new TCGetTicketBlocksParser();
			Tickets tickets = ticketBlocksParser.parse(inputStream,httpRequest.getCharset());
			
			this.logger.info(tickets.getSource().name()+" reply with status: "+ tickets.getStatus());
			
			if( tickets.getStatus().equals( GetTicketsStatus.found_tickest) )
			{
				this.logger.info("Found tickets in: "+tickets.getBlocks().size()+" sections");
			}
			
			return tickets;
		} catch (TCException e) {
			throw new SourceException(e);
		}
	}
    
    public Map<String,List<Event>> getEvents(long countryId) throws TCException  
	{
		CtnHttpRequest httpRequest = new CtnHttpRequest();
		httpRequest.setCharset(this.properties.getCharset());
		httpRequest.setSoapAction(this.properties.getGetEventsByCountryIdAction());

		String soapBody = this.properties.getGetEventsByCountryIdRequest();
		soapBody = soapBody.replace("#CountryID#",String.valueOf(countryId));
		soapBody = soapBody.replace("#APIKey#",this.properties.getApiKey());
		
		httpRequest.setSoapBody(soapBody);
		httpRequest.setUri(this.properties.getTargetEndpoint());
		
		CtnHttpResponse httpResponse = null;
		try {
			httpResponse = this.httpService.execute( httpRequest );
		} catch (UtilsException e) {
			throw new TCException(e);
		}

		InputStream inputStream = new ByteArrayInputStream(httpResponse.getData());
		
		return new TCGetEventsParser().parse(
				inputStream,
				httpRequest.getCharset(),
				this.properties.getEventDateWithoutTimeStringDetection(),
				this.properties.getEventDateFormat(),
				this.properties.getEventDateTimeFormat());
	}
    
    public List<Country> getCountries() throws TCException
	{
		CtnHttpRequest httpRequest = new CtnHttpRequest();
		httpRequest.setCharset(this.properties.getCharset());
		httpRequest.setSoapAction(this.properties.getGetCountriesAction());

		String soapBody = this.properties.getGetCountriesRequest();
		soapBody = soapBody.replace("#APIKey#",String.valueOf(this.properties.getApiKey()));
		
		httpRequest.setSoapBody(soapBody);
		httpRequest.setUri(this.properties.getTargetEndpoint());
		
		CtnHttpResponse httpResponse = null;
		try {
			httpResponse = this.httpService.execute( httpRequest );
		} catch (UtilsException e) {
			throw new TCException(e);
		}

		InputStream inputStream = new ByteArrayInputStream(httpResponse.getData());
		
		return new TCGetCountriesParser().parse(inputStream,httpRequest.getCharset());
	}

    private Country getCountry(String countryAbbreviation){
    	for(Country country : this.countries){
    		if(country.getAbbreviation().equals(countryAbbreviation)){
    			return country;
    		}
    	}
		return null;
	}

	public Map<String, List<Event>> getEventsByCountriesAbbreviation(String[] countriesAbbreviation) throws SourceException {
		
		//preload countries 
		try 
		{
			this.countries = this.getCountries();
		}
		catch (TCException e) 
		{
			throw new SourceException(e);
		}
		
		Map<String,List<Event>> allEvents = new HashMap<String,List<Event>>();
		
		for(String countryAbbreviation : countriesAbbreviation)
		{
			Country country = this.getCountry(countryAbbreviation);
			if(country==null)
			{
				//throw new TCException("Can't find country : "+countryAbbreviation);
				logger.warn("Can't find country : "+countryAbbreviation+".Continue...");
				continue;
			}
			
			Map<String, List<Event>> segmentedEvents = null;
			
			try 
			{
				segmentedEvents = this.getEvents(country.getId());
			} 
			catch (TCException e) 
			{
				throw new SourceException(e);
			}
			
			allEvents.putAll(segmentedEvents);
		}
		
		return allEvents;
	}
}