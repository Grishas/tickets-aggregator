package com.ctnl.run;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import com.ctnc.Event;
import com.ctnc.SourceService;
import com.ctnc.shared.Source;
import com.ctnl.events.collector.CollectConfiguration;
import com.ctnl.events.collector.CollectService;
import com.ctnl.extract.fs.ExtractConfiguration;
import com.ctnl.extract.fs.ExtractException;
import com.ctnl.extract.fs.ExtractService;
import com.ctnl.load.fs.LoadConfiguration;
import com.ctntc.TCConfiguration;
import com.ctntc.TCService;
import com.ctntn.TNConfiguration;
import com.ctntn.TNService;

import org.springframework.core.env.Environment;

public class _1_Cli_Collect {

	private Logger logger = Logger.getLogger(_1_Cli_Collect.class);
	
	private void run()
	{
		AnnotationConfigApplicationContext context = 
				new AnnotationConfigApplicationContext(
				CollectConfiguration.class,
				ExtractConfiguration.class,
				LoadConfiguration.class,
				TNConfiguration.class,
				TCConfiguration.class);

		
		//-----------------------------------------------------------------------------------
		
		//Collect
		long startCollect = System.currentTimeMillis();

		CollectService collectorService = context.getBean(CollectService.class);
		
		Environment environment = context.getBean(Environment.class);
		
		Source source = Source.valueOf(environment.getProperty("source"));
		
		SourceService sourceService = this.getSourceService(context,source);

		Map<String, List<Event>> eventsFromCollect = collectorService.collect(sourceService);
		 
		if (eventsFromCollect==null) 
		{
			logger.fatal("eventsFromCollect==null");
			context.close();	
			return;
		}
		
		long endCollect = System.currentTimeMillis();

		//-----------------------------------------------------------------------------------

		long startExtract = System.currentTimeMillis();

		ExtractService eventsExtractService = context.getBean(ExtractService.class);
		
		try 
		{
			eventsExtractService.extractFromCollect(eventsFromCollect,source);
		}
		catch (ExtractException error2) 
		{
			logger.fatal("extract fail",error2);
			context.close();	
		}

		long endExtract = System.currentTimeMillis();

		logger.info("collect take : "+((endCollect-startCollect)/1000)+" seconds");
		logger.info("extract take : "+((endExtract-startExtract)/1000)+" seconds");
		logger.info("all take : "+((endExtract-startCollect)/1000)+" seconds");
		
		context.close();	
	}
	
	private SourceService getSourceService(AnnotationConfigApplicationContext context,Source source){

		switch (source) 
		{		
			case ticketcity:
				return context.getBean(TCService.class);
				
			case ticketnetwork:
				return context.getBean(TNService.class);
		}
		
		return null;
	}

	public static void main(String[] args) {
		new _1_Cli_Collect().run();
	}
}
