package com.ctnl.events.collector;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import com.ctnc.Event;
import com.ctnc.SourceService;

public class CollectService 
{
	private Logger logger = Logger.getLogger(CollectService.class);

	@Autowired
	private CollectProperties properties;
	
    public CollectService(){}
    
    public Map<String, List<Event>> collect(SourceService sourceService) 
    {   
    	Map<String, List<Event>> events = null;	
		int retryCounter = 0;
		
		do
		{	
			try 
			{
				events = sourceService.getEventsByCountriesAbbreviation( this.properties.getCountriesAbbreviation() );
				
				if(events==null)
				{
					retryCounter++;
					
					logger.error("Failure null in collect.Going to "+retryCounter+" retry...");
					
					Thread.sleep(5000);
					
					continue;
				}
				else
				{
					return events;
				}
				
			} 
			catch (Throwable e) 
			{
				retryCounter++;

				logger.error("Failure in collect.Going to "+retryCounter+" retry...",e);
							
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e1) {
					logger.error(e1);
				}

				continue;
				
			}
			
		}while( retryCounter != this.properties.getRetryTimes() );
		
		return events;
    }
}
