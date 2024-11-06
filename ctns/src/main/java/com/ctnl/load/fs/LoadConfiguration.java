package com.ctnl.load.fs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource("classpath:load.configuration")
public class LoadConfiguration {
	
	@Autowired
	private Environment environment;	
	
	@Bean
	@Scope("singleton")
	public LoadService getEventsLoadFSService()
	{
		return new LoadService();
	}
	
	@Bean
	@Scope("singleton")
	public LoadProperties getEventsLoadFSProperties(){

		String eventsLoadFSBaseFolderPath = this.environment.getProperty("dataRootPath");

		LoadProperties eventsLoadFSProperties = new LoadProperties();

		eventsLoadFSProperties.setBaseFolderPath(eventsLoadFSBaseFolderPath);
		
		return eventsLoadFSProperties;
	}
}

