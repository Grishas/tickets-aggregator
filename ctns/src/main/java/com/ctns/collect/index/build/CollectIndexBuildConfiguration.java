package com.ctns.collect.index.build;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource("classpath:collect.index.build.configuration")
public class CollectIndexBuildConfiguration {
	
	@Autowired
	private Environment environment;	

	@Bean
	@Scope("singleton")
	public CollectIndexBuild getCollectIndexBuild(){		
			return new CollectIndexBuild();
	}	
	
	@Bean
	@Scope("singleton")
	public CollectIndexBuildService getCollectIndexBuildService(){		
		return new CollectIndexBuildService();
	}
	
	@Bean
	@Scope("singleton")
	public CollectIndexBuildProperties getCollectIndexBuildProperties(){		
		CollectIndexBuildProperties properties = new CollectIndexBuildProperties();
		properties.setBaseFolderPath(this.environment.getProperty("dataRootPath"));
		return properties;
	}	
}

