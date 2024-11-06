package com.ctnl.events.merge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;

import com.ctnl.events.merge.MergeProperties;
import com.ctnl.events.merge.MergeService;
import com.ctns.collect.index.CollectIndexConfiguration;

@Import(CollectIndexConfiguration.class)
@Configuration
@PropertySource("classpath:merge.configuration")
public class MergeConfiguration {
	
	@Autowired
	private Environment environment;	
	
	@Bean
	@Scope("singleton")
	public Merge getMerge(){
		return new Merge();
	}
	
	@Bean
	@Scope("singleton")
	public MergeService getMergeService(){
		MergeService mergeService = new MergeService();
		return mergeService;
	}
	
	@Bean
	@Scope("singleton")
	public MergeProperties getMergeProperties(){
		
		MergeProperties properties = new MergeProperties();

		properties.setDistanceMaxThreshold(environment.getProperty("merge.distance.max.threshold",Double.class));

		return properties;
	}
	
	@Bean
	@Scope("singleton")
	public MergeQueryBuilder getMergeQueryBuilder(){
		MergeQueryBuilder mergeQueryBuilder = new MergeQueryBuilder();
		return mergeQueryBuilder;
	}	
}

