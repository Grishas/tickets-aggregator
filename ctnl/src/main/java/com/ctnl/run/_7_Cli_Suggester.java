package com.ctnl.run;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.ctnc.Event;
import com.ctnc.SuggesterDto;
import com.ctnc.shared.IndexKey;
import com.ctnl.events.merge.MergeConfiguration;
import com.ctnl.events.suggester.SuggesterConfiguration;
import com.ctnl.events.suggester.SuggesterException;
import com.ctnl.events.suggester.SuggesterService;
import com.ctnl.extract.fs.ExtractConfiguration;
import com.ctnl.extract.fs.ExtractException;
import com.ctnl.extract.fs.ExtractService;
import com.ctnl.load.fs.LoadConfiguration;
import com.ctnl.load.fs.LoadException;
import com.ctnl.load.fs.LoadService;
import com.ctns.geocoding.GeocodingConfiguration;
import com.ctns.merge.index.MergeIndexConfiguration;
import com.ctnu.config.UtilsConfiguration;

public class _7_Cli_Suggester {

	private Logger logger = Logger.getLogger(_7_Cli_Suggester.class);
	
	private boolean buildSuggesterByEventName(SuggesterService eventsSuggesterService,
			ExtractService eventsExtractService,Map<String, List<Event>> eventsFromMerge){
				
		List<SuggesterDto> suggestersByEventName = null;
		try {
			suggestersByEventName = eventsSuggesterService.buildSuggesterByEventName(eventsFromMerge);
		} catch (SuggesterException e) {
			logger.fatal(e);
			return false;
		}
		
		try {
			eventsExtractService.extractFromSuggested(suggestersByEventName,IndexKey.event);
		} catch (ExtractException error2) {
			logger.fatal("extract fail",error2);
			return false;
		}
		
		String suggestersByEventNameSpell = eventsSuggesterService.buildSuggesterByEventNameSpell(suggestersByEventName);
		try {
			eventsExtractService.extractFromSuggestersSpell(suggestersByEventNameSpell,IndexKey.event);
		} catch (ExtractException e) {
			logger.fatal("extract fail",e);
			return false;
		}
		
		return true;
	}
	
	private boolean buildSuggesterByVenueName(SuggesterService eventsSuggesterService,ExtractService eventsExtractService,Map<String, List<Event>> eventsFromMerge, Map<String, List<String>> identicalVenues)
	{
		List<SuggesterDto> suggestersByVenueName = null;
		try {
			suggestersByVenueName = eventsSuggesterService.buildSuggesterByVenueName(eventsFromMerge,identicalVenues);
		} catch (SuggesterException e) {
			logger.fatal(e);
			return false;
		}
		
		try {
			eventsExtractService.extractFromSuggested(suggestersByVenueName,IndexKey.venue);
		} catch (ExtractException error2) {
			logger.fatal("extract fail",error2);
			return false;
		}
		
		String suggestersByVenueNameSpell = eventsSuggesterService.buildSuggesterByVenueNameSpell(suggestersByVenueName);
		try {
			eventsExtractService.extractFromSuggestersSpell(suggestersByVenueNameSpell,IndexKey.venue);
		} catch (ExtractException e) {
			logger.fatal("extract fail",e);
			return false;
		}
				
		return true;
	}
	
	private boolean buildSuggesterByPerformerName(SuggesterService eventsSuggesterService,ExtractService eventsExtractService,Map<String, List<Event>> eventsFromMerge)
	{		
		List<SuggesterDto> suggestersByPerformerName = null;
		try {
			suggestersByPerformerName = eventsSuggesterService.buildSuggesterByPerformerName(eventsFromMerge);
		} catch (SuggesterException e) {
			logger.fatal(e);
			return false;
		}
		
		try {
			eventsExtractService.extractFromSuggested(suggestersByPerformerName,IndexKey.performer);
		} catch (ExtractException error2) {
			logger.fatal("extract fail",error2);
			return false;
		}
		
		String suggestersByPerformerNameSpell = eventsSuggesterService.buildSuggesterByPerformerNameSpell(suggestersByPerformerName);
		try {
			eventsExtractService.extractFromSuggestersSpell(suggestersByPerformerNameSpell,IndexKey.performer);
		} catch (ExtractException e) {
			logger.fatal("extract fail",e);
			return false;
		}
		
		return true;
	}
	
	private Map<String, List<Event>> getEventsFromMerge(AnnotationConfigApplicationContext context)
	{
		//Load events from FS - Merge
		LoadService eventsLoadFSService = context.getBean(LoadService.class);
		
		Map<String, List<Event>> eventsFromMerge = null;

		try {
			eventsFromMerge = eventsLoadFSService.eventsFromMerge();
		} catch (LoadException e) {
			logger.fatal(e);
			return null;
		}
				
		return eventsFromMerge;
	}
	
	private boolean buildSuggesterByLocationName(SuggesterService suggesterService,
			ExtractService eventsExtractService,Map<String, List<Event>> eventsFromMerge)
	{
		List<SuggesterDto> suggestersByLocationName = null;
		try {
			suggestersByLocationName = suggesterService.buildSuggesterByLocationName(eventsFromMerge);
		} catch (SuggesterException e) {
			logger.fatal(e);
			return false;
		}
		
		try {
			eventsExtractService.extractFromSuggested(suggestersByLocationName,IndexKey.location);
		} catch (ExtractException error2) {
			logger.fatal("extract fail",error2);
			return false;
		}
		
		String suggestersByLocationNameSpell = suggesterService.buildSuggesterByLocationNameSpell(suggestersByLocationName);
		try {
			eventsExtractService.extractFromSuggestersSpell(suggestersByLocationNameSpell,IndexKey.location);
		} catch (ExtractException e) {
			logger.fatal("extract fail",e);
			return false;
		}
		
		return true;
	}

	private void run()
	{
	
		long start = System.currentTimeMillis();
		
		AnnotationConfigApplicationContext  context = new AnnotationConfigApplicationContext(
							MergeIndexConfiguration.class,
							SuggesterConfiguration.class,
							ExtractConfiguration.class,
							LoadConfiguration.class,
							MergeConfiguration.class,
							GeocodingConfiguration.class,
							UtilsConfiguration.class);
		
		SuggesterService suggesterService = context.getBean(SuggesterService.class);
		ExtractService eventsExtractService = context.getBean(ExtractService.class);
		
		Map<String, List<Event>> eventsFromMerge = this.getEventsFromMerge(context);
		
		this.buildSuggesterByEventName(suggesterService,eventsExtractService,eventsFromMerge);
		
		this.buildSuggesterByPerformerName(suggesterService,eventsExtractService,eventsFromMerge);

		this.buildSuggesterByVenueName(suggesterService,eventsExtractService,eventsFromMerge,null);
				
		this.buildSuggesterByLocationName(suggesterService,eventsExtractService,eventsFromMerge);
		
		context.close();		
		
		long end = System.currentTimeMillis();
		logger.info("all take : "+((end-start)/1000)+" seconds");
	}
	
	public static void main(String[] args) {
		new _7_Cli_Suggester().run();
	}
}
