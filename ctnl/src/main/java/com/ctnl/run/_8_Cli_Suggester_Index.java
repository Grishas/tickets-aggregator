package com.ctnl.run;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import com.ctnc.SuggesterDto;
import com.ctnc.shared.IndexKey;
import com.ctnl.load.fs.LoadConfiguration;
import com.ctnl.load.fs.LoadException;
import com.ctnl.load.fs.LoadService;
import com.ctns.suggester.index.build.SuggesterIndexBuildConfiguration;
import com.ctns.suggester.index.build.SuggesterIndexBuildException;
import com.ctns.suggester.index.build.SuggesterIndexBuildService;

public class _8_Cli_Suggester_Index {

	private Logger logger = Logger.getLogger(_8_Cli_Suggester_Index.class);
	
	private boolean suggesterIndexBuildService(AnnotationConfigApplicationContext context, IndexKey indexKey)
	{
		long start = System.currentTimeMillis();

		//Load events from FS - Suggester
		LoadService loadService = context.getBean(LoadService.class);

		List<SuggesterDto> suggesters = null;
		try {
			suggesters = loadService.getSuggesters(indexKey);
		} catch (LoadException e) {
			logger.fatal(e);
			return false;
		}
		
		SuggesterIndexBuildService suggesterIndexBuildService = context.getBean(SuggesterIndexBuildService.class);
		
		try {
			suggesterIndexBuildService.build(suggesters,indexKey);
		} catch (SuggesterIndexBuildException e) {
			logger.fatal(e);
			return false;
		}
						
		try {
			suggesterIndexBuildService.buildSpell(indexKey);
		} catch (SuggesterIndexBuildException e) {
			logger.fatal(e);
			return false;
		}
		
		long end = System.currentTimeMillis();

		logger.info("["+indexKey+"] take : "+((end-start)/1000)+" seconds");
		
		return true;
	}
	
	private void run()
	{
		AnnotationConfigApplicationContext context = 
				new AnnotationConfigApplicationContext(
						LoadConfiguration.class,
						SuggesterIndexBuildConfiguration.class);

		this.suggesterIndexBuildService(context,IndexKey.event);
		this.suggesterIndexBuildService(context,IndexKey.performer);
		this.suggesterIndexBuildService(context,IndexKey.venue);
		this.suggesterIndexBuildService(context,IndexKey.location);
		
		context.close();					
	}
	
	public static void main(String[] args) {
		new _8_Cli_Suggester_Index().run();
	}
}
