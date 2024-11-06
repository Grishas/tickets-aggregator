package com.ctnl.run;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import com.ctnc.Event;
import com.ctnl.extract.fs.ExtractConfiguration;
import com.ctnl.load.fs.LoadConfiguration;
import com.ctnl.load.fs.LoadException;
import com.ctnl.load.fs.LoadService;
import com.ctns.merge.index.build.MergeIndexBuildConfiguration;
import com.ctns.merge.index.build.MergeIndexBuildException;
import com.ctns.merge.index.build.MergeIndexBuildService;

public class _6_Cli_Merge_Index {

	private Logger logger = Logger.getLogger(_6_Cli_Merge_Index.class);

	private void run()
	{
		AnnotationConfigApplicationContext context = 
				new AnnotationConfigApplicationContext(
						LoadConfiguration.class,
						MergeIndexBuildConfiguration.class,
						ExtractConfiguration.class);

		long startLoad = System.currentTimeMillis();

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
		
		long endLoad = System.currentTimeMillis();
		
		//-----------------------------------------------------------------------------------------------------

		LoadService loadFSService = context.getBean(LoadService.class);
//			
//		Map<String,List<String>> identicalVenues = null;
//		try {
//			identicalVenues = loadFSService.loadIdenticalVenues();
//		} catch (LoadFSException e) {
//			logger.fatal(e);
//			context.close();	
//			return;
//		}
		
		//----------------------------------------------
		
//		Map<String,Set<String>> identicalLocations = null;
//		try {
//			identicalLocations = loadFSService.loadIdenticalLocationsNames();
//		} catch (LoadException e) {
//			logger.fatal(e);
//			context.close();	
//			return;
//		}
		
		long startMergeIndexBuild = System.currentTimeMillis();
		
		MergeIndexBuildService mergeIndexBuildService = context.getBean(MergeIndexBuildService.class);
		
		try {
			mergeIndexBuildService.build(eventsFromMerge,null,/*identicalLocations*/null);
		} catch (MergeIndexBuildException e) {
			logger.fatal(e);
			context.close();	
			return;
		}
				
		long endMergeIndexBuild = System.currentTimeMillis();
		
		logger.info("load take : "+((endLoad-startLoad)/1000)+" seconds");
		logger.info("merge index build take : "+((endMergeIndexBuild-startMergeIndexBuild)/1000)+" seconds");
		logger.info("all take : "+((endMergeIndexBuild-startLoad)/1000)+" seconds");
		
		context.close();	
	}
	
	public static void main(String[] args) {
		new _6_Cli_Merge_Index().run();
	}
}
