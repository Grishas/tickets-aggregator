package com.ctnl.events.suggester;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource("classpath:suggester.configuration")
public class SuggesterConfiguration {
	
	@Autowired
	private Environment environment;	
	
	@Bean
	@Scope("singleton")
	public SuggesterByEventName getEventsSuggesterByEventName(){
		return new SuggesterByEventName();
	}
	@Bean
	@Scope("singleton")
	public SuggesterByVenueName getEventsSuggesterByVenueName(){
		return new SuggesterByVenueName();
	}
	@Bean
	@Scope("singleton")
	public SuggesterByPerformerName getEventsSuggesterByPerformerName(){
		return new SuggesterByPerformerName();
	}
	
	@Bean
	@Scope("singleton")
	public SuggesterService getEventsSuggesterService(){
		SuggesterService eventsSuggesterService = new SuggesterService();
		return eventsSuggesterService;
	}
	
	@Bean
	@Scope("singleton")
	public SuggesterProperties getEventsSuggesterProperties(){
		SuggesterProperties properties = new SuggesterProperties();
		return properties;
	}
	
	@Bean
	@Scope("singleton")
	public SuggesterByLocationName getEventsSuggesterByLocationName(){
		return new SuggesterByLocationName();
	}
}

