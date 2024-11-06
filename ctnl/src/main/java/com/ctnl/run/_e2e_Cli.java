package com.ctnl.run;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.ctnl.events.collector.CollectConfiguration;
import com.ctnl.extract.fs.ExtractConfiguration;

public class _e2e_Cli {

	private Logger logger = Logger.getLogger(_e2e_Cli.class);
	
	private void run()
	{
		AnnotationConfigApplicationContext context = 
				new AnnotationConfigApplicationContext(
				CollectConfiguration.class,
				ExtractConfiguration.class);
	
		long start = System.currentTimeMillis();
		
		//run independently check that process not running before e2e_1_Cli_Collect.main(null);
			
		_2_Cli_Data_Normalization.main(null);
		_3_Cli_Geocoding.main(null);		
		_4_Cli_Collect_Index.main(null);
		_5_Cli_Merge.main(null);
		_6_Cli_Merge_Index.main(null);
		_7_Cli_Suggester.main(null);
		_8_Cli_Suggester_Index.main(null);
		_9_Cli_Sitemap.main(null);
		
		
		long end = System.currentTimeMillis();

		logger.info("e2e take : "+((end-start)/1000)+" seconds");

		context.close();	
	}
	
	public static void main(String[] args) {
		new _e2e_Cli().run();
	}
}
