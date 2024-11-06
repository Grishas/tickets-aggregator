package com.ctnl.run;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import com.ctnc.Event;
import com.ctnl.extract.fs.ExtractConfiguration;
import com.ctnl.extract.fs.ExtractException;
import com.ctnl.extract.fs.ExtractService;
import com.ctns.geocoding.GeocodingConfiguration;
import com.ctns.geocoding.GeocodingException;
import com.ctns.geocoding.GeocodingService;
import com.ctnl.load.fs.LoadConfiguration;
import com.ctnl.load.fs.LoadException;
import com.ctnl.load.fs.LoadService;
import com.ctnu.config.UtilsConfiguration;
import com.google.maps.model.GeocodingResult;

public class _3_Cli_Geocoding {

	private Logger logger = Logger.getLogger(_3_Cli_Geocoding.class);

	private void runOnEventsFromCollector()
	{
		AnnotationConfigApplicationContext context = 
				new AnnotationConfigApplicationContext(
						LoadConfiguration.class,
						GeocodingConfiguration.class,
						ExtractConfiguration.class,
						UtilsConfiguration.class);

		long start = System.currentTimeMillis();

		//Load events from FS - Merge
		LoadService eventsLoadFSService = context.getBean(LoadService.class);
		
		List<Map<String, List<Event>>> eventsFromCollector = null;

		try {
			eventsFromCollector = eventsLoadFSService.eventsFromCollect();
		} catch (LoadException e) {
			logger.fatal(e);
			context.close();	
			return;
		}
				
		//-----------------------------------------------------------------------------------------------------

		LoadService loadFSService = context.getBean(LoadService.class);

		Map<String,GeocodingResult[]> geocodingData = null;
		int geocodingDataCurrentCounter = 0;
		try 
		{
			geocodingData = loadFSService.getGeocodingData();
			geocodingDataCurrentCounter = geocodingData.size();
		} catch (LoadException e) {
			logger.fatal(e);
			context.close();	
			return;
		}
		
		//----------------------------------------------
		
		long startGeocoding = System.currentTimeMillis();
		
		GeocodingService geocodingService = context.getBean(GeocodingService.class);
		int geocodingDataNewCounter = 0;
		try {
			geocodingService.build(eventsFromCollector,geocodingData);
			geocodingDataNewCounter = geocodingData.size();
		} catch (GeocodingException e) {
			logger.fatal(e);
			context.close();	
			return;
		}
		
		long endGeocoding = System.currentTimeMillis();
		
		//----------------------------------------------------
		
		ExtractService extractService = context.getBean(ExtractService.class);
		try 
		{
			//Save data in case of delta.For update delete data under geocoding folder
			if(geocodingDataCurrentCounter!=geocodingDataNewCounter)
			{
				extractService.extractFromGeocoding(geocodingData);
			}
		} 
		catch (ExtractException e) {
			logger.fatal(e);
			context.close();	
			return;
		}

		//----------------------------------------------------

		extractService = context.getBean(ExtractService.class);
		
		try 
		{
			for(Map<String, List<Event>> source : eventsFromCollector)
			{
				extractService.extractFromCollect(source,null);
			}			
		} 
		catch (ExtractException e) {
			logger.fatal(e);
			context.close();	
			return;
		}
		
		long end = System.currentTimeMillis();

		logger.info("geocoding take : "+((endGeocoding-startGeocoding)/1000)+" seconds");
		logger.info("all take : "+((end-start)/1000)+" seconds");
	
		context.close();	
	}
	
	
	public static void main(String[] args) {
		new _3_Cli_Geocoding().runOnEventsFromCollector();
		//not in use for now new _2_Cli_Geocoding().runOnEventsFromMerge();
		
	}
	
	
	private void runOnEventsFromMerge()
	{
		AnnotationConfigApplicationContext context = 
				new AnnotationConfigApplicationContext(
						LoadConfiguration.class,
						GeocodingConfiguration.class,
						ExtractConfiguration.class,
						UtilsConfiguration.class);

		long start = System.currentTimeMillis();

		//Load events from FS - Merge
		LoadService eventsLoadFSService = context.getBean(LoadService.class);
		
		Map<String, List<Event>> eventsFromMerge = null;

		try {
			eventsFromMerge = eventsLoadFSService.eventsFromMerge();
		} catch (LoadException e) {
			logger.fatal(e);
			context.close();	
			return;
		}
				
		//-----------------------------------------------------------------------------------------------------

		LoadService loadFSService = context.getBean(LoadService.class);

		Map<String,GeocodingResult[]> geocodingData = null;
		int geocodingDataCurrentCounter = 0;
		try 
		{
			geocodingData = loadFSService.getGeocodingData();
			geocodingDataCurrentCounter = geocodingData.size();
		} catch (LoadException e) {
			logger.fatal(e);
			context.close();	
			return;
		}
		
		//----------------------------------------------
		
		long startGeocoding = System.currentTimeMillis();
		
		GeocodingService geocodingService = context.getBean(GeocodingService.class);
		int geocodingDataNewCounter = 0;
		try {
			geocodingService.build(eventsFromMerge,geocodingData);
			geocodingDataNewCounter = geocodingData.size();
		} catch (GeocodingException e) {
			logger.fatal(e);
			context.close();	
			return;
		}
		
		long endGeocoding = System.currentTimeMillis();
		
		//----------------------------------------------------
		
		ExtractService extractService = context.getBean(ExtractService.class);
		try 
		{
			//Save data in case of delta.For update delete data under geocoding folder
			if(geocodingDataCurrentCounter!=geocodingDataNewCounter)
			{
				extractService.extractFromGeocoding(geocodingData);
			}
		} 
		catch (ExtractException e) {
			logger.fatal(e);
			context.close();	
			return;
		}

		//----------------------------------------------------

		extractService = context.getBean(ExtractService.class);
		
		try 
		{
			extractService.extractFromMerge(eventsFromMerge);
		} 
		catch (ExtractException e) {
			logger.fatal(e);
			context.close();	
			return;
		}
		
		long end = System.currentTimeMillis();

		logger.info("geocoding take : "+((endGeocoding-startGeocoding)/1000)+" seconds");
		logger.info("all take : "+((end-start)/1000)+" seconds");
	
		context.close();	
	}
	
}
