package com.ctntc;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.ctnc.Event;
import com.ctnc.SourceException;

public class Cli_TC {

	private static Logger logger = Logger.getLogger(Cli_TC.class);

	public static void main(String[] args) {	
						
		AnnotationConfigApplicationContext context = 
				new AnnotationConfigApplicationContext(TCConfiguration.class);
		
		TCService ticketCityService = context.getBean(TCService.class);
		
		String[] countries = new String[]{"US"};

		try {
			Map<String, List<Event>> events = ticketCityService.getEventsByCountriesAbbreviation(countries);
			
			for(Map.Entry<String, List<Event>> eventsSegment :  events.entrySet())
			{
				System.out.println(eventsSegment.getKey());
				
				for(Event event : eventsSegment.getValue())
				{
					System.out.println(event);
				}
			}
	
		} catch (SourceException e) {
			e.printStackTrace();
		}
		
		context.close();
	}
}
