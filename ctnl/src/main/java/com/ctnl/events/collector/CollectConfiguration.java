package com.ctnl.events.collector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
//import com.ctntc.TCConfiguration;
//import com.ctntn.TNConfiguration;

//@Import({
//	TNConfiguration.class,
//	TCConfiguration.class
//})
@Configuration
@PropertySource("classpath:collect.configuration")
public class CollectConfiguration {
	
	@Autowired
	private Environment environment;	
	
	@Bean
	@Scope("singleton")
	public CollectService getCollectService(){
		return new CollectService();
	}
	
	@Bean
	@Scope("singleton")
	public CollectProperties getCollectProperties(){

		CollectProperties properties = new CollectProperties();
		properties.setCountriesAbbreviation(environment.getProperty("ctnl.collect.countries.abbreviation",String[].class));
		properties.setRetryTimes(environment.getProperty("ctnl.collect.retry.times",Integer.class));
		return properties;
	}
}

