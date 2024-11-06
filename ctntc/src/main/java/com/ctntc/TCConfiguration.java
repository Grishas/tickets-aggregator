package com.ctntc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;

import com.ctnu.UtilsException;
import com.ctnu.config.UtilsConfiguration;
import com.ctnu.crypto.CipherUtils;

@Import(UtilsConfiguration.class)
@Configuration
@PropertySource("classpath:tc.configuration")
public class TCConfiguration {
	
	public final static String CTNTC_CHARSET= "ctntc.charset";
	public final static String CTNTC_TARGET_END_POINT= "ctntc.target.end.point";
	public final static String CTNTC_API_KEY = "ctntc.api.key";
	public final static String CTNTC_GET_COUNTRIES_ACTION = "ctntc.get.countries.action";
	public final static String CTNTC_GET_EVENTS_BY_COUNTRY_ID_ACTION = "ctntc.get.events.by.country.id.action";
	public final static String CTNTC_EVENT_DATE_FORMAT = "ctntc.event.date.format";
	public final static String CTNTC_EVENT_DATE_WITHOUT_TIME_STRING_DETECTION = "ctntc.event.date.without.time.string.detection";
	public final static String CTNTC_EVENT_DATE_TIME_FORMAT = "ctntc.event.date.time.format";
	public final static String CTNTC_GET_TICKET_BLOCKS_ACTION = "ctntc.get.ticket.blocks.action";

	@Autowired
	private Environment environment;
	
	@Autowired
	private  CipherUtils cipherUtils;

	@Bean
	@Scope("singleton")
	public TCService getTicketNetworkService(){
		return new TCService();	
	}
	
	@Bean
	@Scope("singleton")
	public TCProperties getTCProperties() throws TCException{
		
		String targetEndpoint = this.environment.getProperty(CTNTC_TARGET_END_POINT);
		
		String apiKey = this.environment.getProperty(CTNTC_API_KEY);
		try {
			apiKey = cipherUtils.decrypt(apiKey);
		} catch (UtilsException e) {
			throw new TCException(e);
		}
	
		String getCountriesAction = this.environment.getProperty(CTNTC_GET_COUNTRIES_ACTION);
		String getEventsByCountryIdAction = this.environment.getProperty(CTNTC_GET_EVENTS_BY_COUNTRY_ID_ACTION);
		String charset = this.environment.getProperty(CTNTC_CHARSET);
		String eventDateFormat = this.environment.getProperty(CTNTC_EVENT_DATE_FORMAT);
		String eventDateWithoutTimeStringDetection = this.environment.getProperty(CTNTC_EVENT_DATE_WITHOUT_TIME_STRING_DETECTION);
		String eventDateTimeFormat = this.environment.getProperty(CTNTC_EVENT_DATE_TIME_FORMAT);
		String getTicketBlocksAction = this.environment.getProperty(CTNTC_GET_TICKET_BLOCKS_ACTION);
		String getCountriesRequest=this.environment.getProperty("ctntc.get.countries.request");
		String getEventsByCountryIdRequest=this.environment.getProperty("ctntc.get.events.by.country.id.request");
		String getTicketBlocksRequest=this.environment.getProperty("ctntc.get.ticket.blocks.request");

		TCProperties properties = new TCProperties();
		properties.setApiKey(apiKey);
		properties.setCharset(charset);
		properties.setEventDateFormat(eventDateFormat);
		properties.setEventDateTimeFormat(eventDateTimeFormat);
		properties.setEventDateWithoutTimeStringDetection(eventDateWithoutTimeStringDetection);
		properties.setGetCountriesAction(getCountriesAction);
		properties.setGetCountriesRequest(getCountriesRequest);
		properties.setGetEventsByCountryIdAction(getEventsByCountryIdAction);
		properties.setGetEventsByCountryIdRequest(getEventsByCountryIdRequest);
		properties.setGetTicketBlocksAction(getTicketBlocksAction);
		properties.setGetTicketBlocksRequest(getTicketBlocksRequest);
		properties.setTargetEndpoint(targetEndpoint);
		return properties;
	}
	
	
	
	
	
	

	
	
	
	
	
	
	
	
	
}