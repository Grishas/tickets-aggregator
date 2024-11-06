package com.ctnl.extract.fs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource("classpath:extract.configuration")
public class ExtractConfiguration {
	
	@Autowired
	private Environment environment;	
	
	@Bean
	@Scope("singleton")
	public ExtractService getExtractFSService(){
		return new ExtractService();
	}
	
	@Bean
	@Scope("singleton")
	public ExtractProperties getExtractProperties(){

		String eventsExtractFSBaseFolderPath = this.environment.getProperty("dataRootPath");
		
		boolean prettyPrinting = this.environment.getProperty("ctnl.json.pretty.printing",Boolean.class);
		boolean extractOnlyMergedEvents = 
				this.environment.getProperty("ctnl.extract.only.merged.events",Boolean.class);
				
		ExtractProperties properties = new ExtractProperties();
		properties.setBaseFolderPath(eventsExtractFSBaseFolderPath);
		properties.setPrettyPrinting(prettyPrinting);
		properties.setExtractOnlyMergedEvents(extractOnlyMergedEvents);
		
		return properties;
	}
}

