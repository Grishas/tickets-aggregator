package com.ctnl.run;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.ctnc.Event;
import com.ctnl.extract.fs.ExtractConfiguration;
import com.ctnl.extract.fs.ExtractException;
import com.ctnl.extract.fs.ExtractService;
import com.ctnl.load.fs.LoadConfiguration;
import com.ctnl.load.fs.LoadException;
import com.ctnl.load.fs.LoadService;
import com.ctns.collect.index.build.CollectIndexBuildConfiguration;
import com.ctns.collect.index.build.CollectIndexBuildException;
import com.ctns.collect.index.build.CollectIndexBuildService;
import com.ctns.collect.index.build.LocationMappingRule;

public class _4_Cli_Collect_Index {

	private Logger logger = Logger.getLogger(_4_Cli_Collect_Index.class);

	private void run()
	{
		AnnotationConfigApplicationContext context = 
				new AnnotationConfigApplicationContext(
						CollectIndexBuildConfiguration.class,
						LoadConfiguration.class,
						ExtractConfiguration.class);

		LoadService loadFSService = context.getBean(LoadService.class);
		Map<String,List<LocationMappingRule>> locationMappingRules = null; 
		try {
			locationMappingRules = loadFSService.loadLocationMappingRules();
		} catch (LoadException e) {
			logger.fatal("Load location mapping rules fail",e);
			context.close();	
			return;
		}
		
		long startLoad = System.currentTimeMillis();

		List<Map<String, List<Event>>> eventsFromCollect = null;

		try {
			eventsFromCollect = loadFSService.eventsFromCollect();
		} catch (LoadException e) {
			logger.fatal(e);
			context.close();	
			return;
		}
		
		long endLoad = System.currentTimeMillis();

		//-----------------------------------------------------------------------------------------------------

		long startSearchIndexBuild = System.currentTimeMillis();
		
		CollectIndexBuildService searchBuildService = context.getBean(CollectIndexBuildService.class);
		
		try {
			searchBuildService.build(eventsFromCollect);
		} catch (CollectIndexBuildException e) {
			logger.fatal(e);
			context.close();	
			return;
		}
				
		long endSearchIndexBuild = System.currentTimeMillis();
		
		//------------------------------
		
		long startExtract = System.currentTimeMillis();

		ExtractService eventsExtractService = context.getBean(ExtractService.class);
		
		try 
		{
			for(Map<String, List<Event>> events : eventsFromCollect)
			{
				eventsExtractService.extractFromCollect(events,null);
			}
		}
		catch (ExtractException error2) 
		{
			logger.fatal("extract fail",error2);
			context.close();	
		}

		long endExtract = System.currentTimeMillis();
		
		//------------------------------
		
		logger.info("load take : "+((endLoad-startLoad)/1000)+" seconds");
		logger.info("search index build take : "+((endSearchIndexBuild-startSearchIndexBuild)/1000)+" seconds");
		logger.info("extract take : "+((endExtract-startExtract)/1000)+" seconds");
		logger.info("all take : "+((endExtract-startLoad)/1000)+" seconds");
		
		context.close();	
	}
	
	public static void main(String[] args) {
		new _4_Cli_Collect_Index().run();
	}
}
