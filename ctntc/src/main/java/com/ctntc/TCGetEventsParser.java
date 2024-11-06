package com.ctntc;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import com.ctnc.City;
import com.ctnc.Country;
import com.ctnc.Event;
import com.ctnc.Location;
import com.ctnc.State;
import com.ctnc.Venue;
import com.ctnc.shared.Performer;
import com.ctnc.shared.Source;
import com.ctnu.UtilsException;
import com.ctnu.date.DateUtils;

public class TCGetEventsParser extends TCBaseParser{

	private boolean getDate(Date dateOut,String sourceDate,
			String eventDateWithoutTimeStringDetection,
			String eventDateFormat,
			String eventDateTimeFormat) throws TCException{
		
		String date = null;
		String format = null;
		boolean isFinalTime = false;
		
		if( sourceDate.contains(eventDateWithoutTimeStringDetection) )
		{
			date = sourceDate.replace(eventDateWithoutTimeStringDetection,"");
			format = eventDateFormat;
			isFinalTime = false;
		}
		else
		{
			date = sourceDate;
			format = eventDateTimeFormat;
			isFinalTime = true;
		}
		
		try {
			dateOut.setTime(DateUtils.parseDate(date,format).getTime());
		} catch (UtilsException e) {
			throw new TCException(e);
		}
		
		return isFinalTime;
	}
	
	public Map<String,List<Event>> parse(
			InputStream inputStream,
			String encoding,
			String eventDateWithoutTimeStringDetection,
			String eventDateFormat,
			String eventDateTimeFormat) throws TCException{
		
		XMLEventReader xmlEventReader = super.getXMLEventReader(inputStream,encoding);

		Event event = null;
		Performer performer = null;
		Venue venue = null;
		
		Location location = null;
		Country country = null;
		State state = null;
		City city = null;
		
		Map<String,List<Event>> events = new HashMap<String,List<Event>>();  
		
		while (xmlEventReader.hasNext()) 
		{
			XMLEvent xmlEvent = null;
			try {
				xmlEvent = xmlEventReader.nextEvent();
			} catch (XMLStreamException e) {
				throw new TCException("xmlEventReader.nextEvent() fail",e);
			}

			if ( xmlEvent.isStartElement() ) 
		    {
				StartElement startElement = xmlEvent.asStartElement();
		        
				try//try to catch nulls if signature changed
				{
					if (startElement.getName().getLocalPart().equals(EVENT))
					{	
						event = new Event();
				            
						event.setId(
								Long.valueOf(startElement.getAttributeByName(new QName(ID)).getValue()));
						
						String name = startElement.getAttributeByName(new QName(NAME)).getValue();
						event.setName(name);  
						event.setOriginalName(name);
						 
//						event.setPage(
//								startElement.getAttributeByName(new QName(PAGE)).getValue());  				  
						
						String date = startElement.getAttributeByName(new QName(EVENT_DATE_TIME)).getValue();
						
						Date dateOut = new Date();
						boolean isFinalTime = 
								this.getDate(dateOut,date,eventDateWithoutTimeStringDetection, eventDateFormat, eventDateTimeFormat);
						
//						System.out.println("date---------------------------"+date);
//						System.out.println("date after normalize-----------"+dateOut);
						
						event.setDate( dateOut );
						event.setFinalTime(isFinalTime);
						event.setDisplayDate(date);

//						event.setStartingPrice(
//								startElement.getAttributeByName(new QName(STARTING_PRICE)).getValue());
						continue;
				    }
			
					if (startElement.getName().getLocalPart().equals(PERFORMER)) 
				    {
						performer = new Performer();
				            
						performer.setId(
								Long.valueOf(startElement.getAttributeByName(new QName(ID)).getValue()));
						
						performer.setName(
								startElement.getAttributeByName(new QName(NAME)).getValue());  
				      
//						performer.setPrimary(
//				        		startElement.getAttributeByName(new QName(PRIMARY)).getValue());  
				        
				        event.getPerformers().add(performer);
				            
				        continue;
				     }
				    
					 if (startElement.getName().getLocalPart().equals(VENUE)) 
				     {
						 venue = new Venue();
				            
						 venue.setId(
								 Long.valueOf(startElement.getAttributeByName(new QName(ID)).getValue()));
						 
						 String name = startElement.getAttributeByName(new QName(NAME)).getValue();
						 venue.setName(name);  
				         venue.setOriginalName(name);
						 
						 event.setVenue(venue);
				            
						 continue;
				     }
					
			        if (startElement.getName().getLocalPart().equals(CITY)) 
			        {
			           city = new City();
			            
//			            city.setId(
//			            		Long.valueOf(startElement.getAttributeByName(new QName(ID)).getValue()));
			            city.getCityNames().add(
			            		startElement.getAttributeByName(new QName(NAME)).getValue());  
			            
			            continue;
			        }
			            
			        if (xmlEvent.asStartElement().getName().getLocalPart().equals(COUNTRY)) 
			        {
			        	country = new Country();
			            	
				        country.setId(
				            		Long.valueOf(startElement.getAttributeByName(new QName(ID)).getValue())); 
			            	
			            country.setName(
				            		startElement.getAttributeByName(new QName(NAME)).getValue()); 
			            	
			            country.setAbbreviation(
				            		startElement.getAttributeByName(new QName(ABBR)).getValue()); 
			            
			            continue;
			        }
			      
			        if (xmlEvent.asStartElement().getName().getLocalPart().equals(STATE)) 
			        {
			        	state = new State();
			            	
				        state.setId(
				            		Long.valueOf(startElement.getAttributeByName(new QName(ID)).getValue())); 
			            	
				        state.setName(
				            		startElement.getAttributeByName(new QName(NAME)).getValue()); 
			            	
				        state.setAbbreviation(
				            		startElement.getAttributeByName(new QName(ABBR)).getValue()); 
				        
			            continue;
			        }
				}
				catch(NullPointerException e){
					throw new TCException("Attribute key changed",e);
				}
		    }
		
			if (xmlEvent.isEndElement() && xmlEvent.asEndElement().getName().getLocalPart() == (EVENT)) {
				
				location = new Location();
				location.setCountry(country);
				location.setState(state);
				location.setCity(city);
				event.setLocation(location);
				event.setSource(Source.ticketcity);
				
				String key = this.getSegmentKey(event);
				
				event.setSegmentKey(key);
				
				if(events.containsKey(key)){
					events.get(key).add(event);
				}
				else{
					List<Event> segmentedEvents = new ArrayList<Event>();
					segmentedEvents.add(event);
					events.put(key, segmentedEvents);
				}
			}
		}
		
		return events;
	}
	
	private String getSegmentKey(Event event)
	{
		String countryAbbreviation = event.getLocation().getCountry().getAbbreviation().toLowerCase();
		String stateAbbreviation = (event.getLocation().getState()!=null)?("_"+event.getLocation().getState().getAbbreviation().toLowerCase()):"";
		
		String key = (stateAbbreviation.equals("")?
				countryAbbreviation:
				countryAbbreviation)+stateAbbreviation;
		return key;
	}
}















