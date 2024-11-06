package com.ctntn;
import org.apache.log4j.Logger;
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
@PropertySource("classpath:tn.configuration")
public class TNConfiguration {
	
	public final static String CTNTN_TARGET_END_POINT= "ctntn.target.end.point";
	public final static String CTNTN_WEB_SITE_CONFIG_ID = "ctntn.website.config.id";
	public final static String CTNTN_CHARSET = "ctntn.charset";
	public final static String CTNTN_GET_EVENTS_ACTION = "ctntn.get.events.action";
	public final static String CTNTN_GET_COUNTRIES_ACTION = "ctntn.get.countries.action";
	public final static String CTNTN_GET_STATES_ACTION = "ctntn.get.states.action";
	public final static String CTNTN_GET_EVENT_PERFORMERS_ACTION = "ctntn.get.event.performers.action";
	public final static String CTNTN_GET_VENUE_ACTION = "ctntn.get.venue.action";
	public final static String CTNTN_GET_CATEGORIES_MASTER_LIST_ACTION = "ctntn.get.categories.master.list.action";
	public final static String CTNTN_EVENT_DATE_DISPLAY_DATE_FORMAT = "ctntn.event.display.date.format";
	public final static String CTNTN_EVENT_DISPLAY_DATE_WITHOUT_TIME_STRING_DETECTION = "ctntn.event.display.date.without.time.string.detection";
	public final static String CTNTN_EVENT_DISPLAY_DATE_TIME_FORMAT = "ctntn.event.display.date.time.format";
	public final static String CTNTN_EVENT_DATE_FORMAT = "ctntn.event.date.format";
	public final static String CTNTN_GET_TICKETS_ACTION = "ctntn.get.tickets.action";
	
	@Autowired
	private Environment environment;
	
	@Autowired
	private CipherUtils cipherUtils;
	
	private Logger logger = Logger.getLogger(TNConfiguration.class);

	@Bean(name="tnService")
	@Scope("singleton")
	public TNService getTicketNetworkService() throws TNException{
		return new TNService();
	}
	
	
	@Bean
	@Scope("singleton")
	public TNProperties getTNProperties() throws TNException{
		
		String targetEndpoint = this.environment.getProperty(CTNTN_TARGET_END_POINT);
		
		String websiteConfigId = this.environment.getProperty(CTNTN_WEB_SITE_CONFIG_ID,String.class);
		try {
			websiteConfigId = cipherUtils.decrypt(websiteConfigId);
		} catch (UtilsException e) {
			logger.fatal(e);
			throw new TNException(e); 
		}
		
		String charset = this.environment.getProperty(CTNTN_CHARSET);
		String getEventsAction = this.environment.getProperty(CTNTN_GET_EVENTS_ACTION);
		String getCountriesAction = this.environment.getProperty(CTNTN_GET_COUNTRIES_ACTION);
		String getStatesAction = this.environment.getProperty(CTNTN_GET_STATES_ACTION);
		String getEventPerformersAction = this.environment.getProperty(CTNTN_GET_EVENT_PERFORMERS_ACTION);
		String getVenueAction = this.environment.getProperty(CTNTN_GET_VENUE_ACTION);
		String getCategoriesMasterListAction = this.environment.getProperty(CTNTN_GET_CATEGORIES_MASTER_LIST_ACTION);
		String eventDisplayDateFormat = this.environment.getProperty(CTNTN_EVENT_DATE_DISPLAY_DATE_FORMAT);
		String eventDisplayDateWithoutTimeStringDetection = this.environment.getProperty(CTNTN_EVENT_DISPLAY_DATE_WITHOUT_TIME_STRING_DETECTION);;
		String eventDisplayDateTimeFormat = this.environment.getProperty(CTNTN_EVENT_DISPLAY_DATE_TIME_FORMAT);
		String eventDateFormat = this.environment.getProperty(CTNTN_EVENT_DATE_FORMAT);
		String getTicketsAction = this.environment.getProperty(CTNTN_GET_TICKETS_ACTION);
		String getCategoriesMasterListRequest = this.environment.getProperty("ctntn.get.categories.master.list.request");
		String getCountriesRequest = this.environment.getProperty("ctntn.get.countries.request");
		String getEventPerformersRequest = this.environment.getProperty("ctntn.get.event.performers.request");
		String getEventsRequest = this.environment.getProperty("ctntn.get.events.request");
		String getStatesByCountryIdRequest = this.environment.getProperty("ctntn.get.states.by.country.id.request");
		String getTicketsRequest = this.environment.getProperty("ctntn.get.tickets.request");
		String getVenueRequest = this.environment.getProperty("ctntn.get.venue.request");
		
		TNProperties properties = new TNProperties();
		properties.setCharset(charset);
		properties.setEventDateFormat(eventDateFormat);
		properties.setEventDisplayDateFormat(eventDisplayDateFormat);
		properties.setEventDisplayDateTimeFormat(eventDisplayDateTimeFormat);
		properties.setEventDisplayDateWithoutTimeStringDetection(eventDisplayDateWithoutTimeStringDetection);
		properties.setGetCategoriesMasterListAction(getCategoriesMasterListAction);
		properties.setGetCategoriesMasterListRequest(getCategoriesMasterListRequest);
		properties.setGetCountriesAction(getCountriesAction);
		properties.setGetCountriesRequest(getCountriesRequest);
		properties.setGetEventPerformersAction(getEventPerformersAction);
		properties.setGetEventPerformersRequest(getEventPerformersRequest);
		properties.setGetEventsAction(getEventsAction);
		properties.setTargetEndpoint(targetEndpoint);
		properties.setGetStatesAction(getStatesAction);
		properties.setGetVenueAction(getVenueAction);
		properties.setGetTicketsAction(getTicketsAction);
		properties.setGetEventsRequest(getEventsRequest);
		properties.setGetStatesByCountryIdRequest(getStatesByCountryIdRequest);
		properties.setGetTicketsRequest(getTicketsRequest);
		properties.setGetVenueRequest(getVenueRequest);
		properties.setWebsiteConfigId(websiteConfigId);
		
		return properties;
	}
	
	
	
	@Bean
	@Scope("singleton")
	public TNGetEventsParser getTNGetEventsParser(){
		return new TNGetEventsParser();
	}
	
	
	
	
	
	
	
}