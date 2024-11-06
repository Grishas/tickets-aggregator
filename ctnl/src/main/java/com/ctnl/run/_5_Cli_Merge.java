
package com.ctnl.run;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.ctnc.Event;
import com.ctnl.events.merge.MergeConfiguration;
import com.ctnl.events.merge.MergeException;
import com.ctnl.events.merge.MergeService;
import com.ctnl.events.merge.MergeService.MergeLocationResult;
import com.ctnl.extract.fs.ExtractConfiguration;
import com.ctnl.extract.fs.ExtractException;
import com.ctnl.extract.fs.ExtractService;
import com.ctnl.load.fs.LoadConfiguration;
import com.ctnl.load.fs.LoadException;
import com.ctnl.load.fs.LoadService;
import com.ctns.geocoding.GeocodingConfiguration;
import com.ctnu.config.UtilsConfiguration;

public class _5_Cli_Merge {

	private Logger logger = Logger.getLogger(_5_Cli_Merge.class);
	
	private void run()
	{
		
		AnnotationConfigApplicationContext context = 
				new AnnotationConfigApplicationContext(
						LoadConfiguration.class,
						MergeConfiguration.class,
						ExtractConfiguration.class,
						UtilsConfiguration.class,
						GeocodingConfiguration.class);

		long startLoad = System.currentTimeMillis();

		//Load events from FS - Collect
		LoadService eventsLoadFSService = context.getBean(LoadService.class);
		
		List<Map<String, List<Event>>> eventsFromCollect = null;

		try {
			eventsFromCollect = eventsLoadFSService.eventsFromCollect();
		} catch (LoadException e) {
			logger.fatal(e);
			context.close();	
			return;
		}
		
		long endLoad = System.currentTimeMillis();
	
		long startMerge = System.currentTimeMillis();
		
		//Merge events
		MergeService mergeService = context.getBean(MergeService.class);
		
		Map<String, List<Event>> eventsFromMerge = null;

		try {
			eventsFromMerge = mergeService.mergeEvent(eventsFromCollect);
		} catch (MergeException e) {
			logger.fatal(e);
			context.close();	
			return;
		}
		
		long endMerge = System.currentTimeMillis();

		//Extract Merge to FS
		long startExtract = System.currentTimeMillis();

		ExtractService extractService = context.getBean(ExtractService.class);
		
		try {
			extractService.extractFromMerge(eventsFromMerge);
		} catch (ExtractException e) {
			logger.fatal(e);
			context.close();	
			return;
		}

		long endExtract = System.currentTimeMillis();

		logger.info("load take : "+((endLoad-startLoad)/1000)+" seconds");
		logger.info("merge take : "+((endMerge-startMerge)/1000)+" seconds");
		logger.info("extract take : "+((endExtract-startExtract)/1000)+" seconds");
		logger.info("all take : "+((endExtract-startLoad)/1000)+" seconds");
	
//		Map<String, List<String>> venuesMapping;
//		try {
//			venuesMapping = mergeService.mergeVenues(eventsFromMerge);
//		} catch (MergeException e) {
//			logger.fatal(e);
//			context.close();	
//			return;
//		}
//		
//		try {
//			extractService.extractIdenticalVenues(venuesMapping);
//		} catch (ExtractFSException e) {
//			logger.fatal(e);
//			context.close();	
//			return;		
//		}
		
		//-------------------------
		
		try {
			  mergeService.reportMergedEventsWithDifferentCities(eventsFromMerge);
		} catch (MergeException e) {
			logger.fatal(e);
			context.close();	
			return;
		}
				
		//-------------------------
		
//		Map<String,Set<String>> mergeLocation = null;
//		try {
//			mergeLocation =  mergeService.mergeLocation(eventsFromMerge,MergeLocationResult.LEVEL2);
//		} catch (MergeException e) {
//			logger.fatal(e);
//			context.close();	
//			return;
//		}
//		
//		try {
//			extractService.extractIdenticalLocations(mergeLocation);
//		} catch (ExtractException e) {
//			logger.fatal(e);
//			context.close();	
//			return;
//		}
		
		//----------------------------
		
//		Set<String> locations = null;
//		
//		try {
//			locations =  mergeService.mergeLocation(eventsFromMerge);
//		} catch (MergeException e) {
//			logger.fatal(e);
//			context.close();	
//			return;
//		}
//		
//		try {
//			extractService.extractLocations(locations);
//		} catch (ExtractFSException e) {
//			logger.fatal(e);
//			context.close();	
//			return;
//		}
		
		context.close();					
	}
	
	public static void main(String[] args) {
		new _5_Cli_Merge().run();
	}
}
