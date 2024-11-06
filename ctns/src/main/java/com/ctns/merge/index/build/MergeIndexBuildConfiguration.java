package com.ctns.merge.index.build;
import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource("classpath:merge.index.build.configuration")
public class MergeIndexBuildConfiguration {
	
	@Autowired
	private Environment environment;	

	@Bean
	@Scope("singleton")
	public MergeIndexBuild getMergeIndexBuild(){		
			return new MergeIndexBuild();
	}	
	
	@Bean
	@Scope("singleton")
	public MergeIndexBuildService getMergeIndexBuildService(){		
		return new MergeIndexBuildService();
	}
	
	@Bean
	@Scope("singleton")
	public MergeIndexBuildProperties getMergeBuildProperties(){		
		
		MergeIndexBuildProperties properties = new MergeIndexBuildProperties();
		
		properties.setBaseFolderPath(this.environment.getProperty("dataRootPath"));
		
		return properties;
	}	
}







