package com.ctnl.run;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.ctnl.extract.fs.ExtractConfiguration;
import com.ctnl.extract.fs.ExtractService;
import com.ctnl.load.fs.LoadConfiguration;
import com.ctnl.load.fs.LoadException;
import com.ctnl.load.fs.LoadService;
import com.ctns.collect.index.build.LocationMappingRule;
import com.ctns.collect.index.build.LocationRuleAction;
import com.ctns.merge.index.MergeIndex;
import com.ctns.merge.index.MergeIndexConfiguration;

public class _Test_Cli {
	
	private void run()
	{
		AnnotationConfigApplicationContext context = 
				new AnnotationConfigApplicationContext(
				ExtractConfiguration.class,LoadConfiguration.class,MergeIndexConfiguration.class);


		MergeIndex mergeIndex = context.getBean(MergeIndex.class);
		
		
		
		
		ExtractService eventsExtractService = context.getBean(ExtractService.class);
		
		LocationMappingRule locationMappingRule = new LocationMappingRule();
		locationMappingRule.setAction(LocationRuleAction.replace);
		locationMappingRule.setValue("value1");
		
		LocationMappingRule locationMappingRule1 = new LocationMappingRule();
		locationMappingRule1.setAction(LocationRuleAction.add);
		locationMappingRule1.setValue("value2");
		
		List<LocationMappingRule> rules = new ArrayList<LocationMappingRule>();
		rules.add(locationMappingRule);
		rules.add(locationMappingRule1);
		
		Map<String,List<LocationMappingRule>> allRules = new HashMap<String,List<LocationMappingRule>>();
		allRules.put("key1",rules);
		
		eventsExtractService.extractLocationMappingRules(allRules);
		
		LoadService loadFSService = context.getBean(LoadService.class);
		try {
		System.out.println(loadFSService.loadLocationMappingRules());
		} catch (LoadException e) {
		
			e.printStackTrace();
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		

		context.close();	
	}
	
	public static void main(String[] args) {
		new _Test_Cli().run();
	}
}
