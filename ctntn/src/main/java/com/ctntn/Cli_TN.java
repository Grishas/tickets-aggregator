package com.ctntn;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.ctnc.Event;
import com.ctnc.SourceException;
import com.ctnc.shared.Tickets;
import com.ctnc.shared.TicketsBlock;

public class Cli_TN {

	public static void main(String[] args) {	
		
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TNConfiguration.class);
		
		TNService tnService = context.getBean(TNService.class);
		
		Tickets tickets = null;
		try {
			tickets = tnService.getTickets(2583424l);
		} catch (SourceException e) {
			e.printStackTrace();
		}
		
		System.out.println(tickets);
		for(Map.Entry<String,List<TicketsBlock>> entry : tickets.getBlocks().entrySet())
		{
			System.out.println("******************");
			System.out.println("******************");
			System.out.println("******************");
			
			System.out.println(entry.getKey());
			
			for(TicketsBlock tb : entry.getValue())
			{
				System.out.println(tb);
				System.out.println("******************");
				
			}
			
			
		}
		
		
//		String[] countries = new String[]{"US"};
//
//		try {
//			Map<String, List<Event>> events = tnService.getEventsByCountriesAbbreviation(countries);
//			
//			for(Map.Entry<String, List<Event>> eventsSegment :  events.entrySet())
//			{
//				System.out.println(eventsSegment.getKey());
//				
//				for(Event event : eventsSegment.getValue())
//				{
//					//System.out.println(event);
//				}
//			}
//	
//		} catch (TNException e) {
//			e.printStackTrace();
//		}
		
		context.close();
	}
}
