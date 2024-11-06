package com.ctntn;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.springframework.beans.factory.annotation.Autowired;

import com.ctnc.Category;
import com.ctnc.City;
import com.ctnc.Country;
import com.ctnc.Event;
import com.ctnc.Location;
import com.ctnc.State;
import com.ctnc.Venue;
import com.ctnc.shared.Performer;
import com.ctnc.shared.Source;
import com.ctntn.dto.internal.EventPerformer;
import com.ctnu.UtilsException;
import com.ctnu.date.DateUtils;

public class TNGetEventsParser extends TNBaseParser{

	@Autowired
	private TNService tnService;
	
	private boolean setDate(Date dateOut,String sourceDate,
			String sourceDisplayDate,String eventDateFormat,String eventDisplayDateWithoutTimeStringDetection) throws TNException{
		
		boolean isFinalTime = false;
		try {
			//contain TBA flag.It mean that event without time
			if(sourceDisplayDate.contains(eventDisplayDateWithoutTimeStringDetection))
			{
				dateOut.setTime(
						DateUtils.parseDate(sourceDate,eventDateFormat).getTime());

				//clear not correct time set 00:00:00
				dateOut.setTime(
						DateUtils.clearTime(dateOut).getTime());
				
				return isFinalTime;
			}
			else
			{
				dateOut.setTime(
						DateUtils.parseDate(sourceDate,eventDateFormat).getTime());

				isFinalTime = true;
				return isFinalTime;
			}
			
		} catch (UtilsException e) {
			throw new TNException(e);
		}		
	}
	
	protected String getSegmentKey(Country country,State state)
	{
		String countryAbbreviation = country.getAbbreviation().toLowerCase();
		String key = null;
		
		if(country.getAbbreviation().toLowerCase().equals("uk")){
			return "uk";
		}
		//
		//so on..... ummee :-)
		
		String stateAbbreviation = state.getAbbreviation().toLowerCase();
		key = stateAbbreviation.equals("")?
											countryAbbreviation:
											countryAbbreviation+"_"+stateAbbreviation;
		
		return key;
	}
	
	public List<Event> parse(
			InputStream inputStream,
			String encoding,
			State state,
			String eventDateFormat,
			String eventDisplayDateWithoutTimeStringDetection) throws TNException{
		
		XMLEventReader xmlEventReader = super.getXMLEventReader(inputStream,encoding);

		Event 		event = null;
	    Venue 		venue = null;
		City 		city= null;
		String 		tmpDate = null;
		String 		tmpDisplayDate = null;
		long 		tmpChildCategoryId = 0;
		long 		tmpGrandChildCategoryId = 0;
		long 		tmpParentCategoryId = 0;
	    long        tmpCountryId = 0;
	    
	    List<Event> events = new ArrayList<Event>();  
	    
		while (xmlEventReader.hasNext()) 
		{
			XMLEvent xmlEvent = null;
			try {
				xmlEvent = xmlEventReader.nextEvent();
			} catch (XMLStreamException e) {
				throw new TNException("xmlEventReader.nextEvent() fail",e);
			}

			if ( xmlEvent.isStartElement() ) 
		    {
				StartElement startElement = xmlEvent.asStartElement();
				
					if (startElement.getName().getLocalPart().equals(EVENT)){	
						event 		= new Event();
						venue	 	= new Venue();
						continue;
					}
					
					if (startElement.getName().getLocalPart().equals(CHILD_CATEGORY_ID)){
						tmpChildCategoryId = Long.valueOf(this.getData(xmlEventReader,CHILD_CATEGORY_ID));
						continue;
					}
					if (startElement.getName().getLocalPart().equals(GRAND_CHILD_CATEGORY_ID)){
						tmpGrandChildCategoryId = Long.valueOf(this.getData(xmlEventReader,GRAND_CHILD_CATEGORY_ID));
						continue;
					}
					if (startElement.getName().getLocalPart().equals(PARENT_CATEGORY_ID)){
						tmpParentCategoryId = Long.valueOf(this.getData(xmlEventReader,PARENT_CATEGORY_ID));
						continue;
					}
					
					if (startElement.getName().getLocalPart().equals(CITY)){
						city = new City();
						city.getCityNames().add(this.getData(xmlEventReader,CITY));
						continue;
					}
					
					if (startElement.getName().getLocalPart().equals(DATE)){
						tmpDate = this.getData(xmlEventReader,DATE);
						continue;
					}
					
					if (startElement.getName().getLocalPart().equals(DISPLAY_DATE)){
						tmpDisplayDate = this.getData(xmlEventReader,DISPLAY_DATE);
						continue;
					}
					
					if (startElement.getName().getLocalPart().equals(ID)){
						event.setId(
								Long.valueOf(this.getData(xmlEventReader,ID)));
						

//						if(event.getId()==2545831)
//						{
////							 "date": "Jan 3, 2016 12:00:00 AM",
////							  "displayDate": "01/03/2016 12:00PM",
////							    
//							System.out.println("");
//						}
						
						continue;
					}

					
					
					
					if (startElement.getName().getLocalPart().equals(NAME)){
						
						String name = this.getData(xmlEventReader,NAME);
						event.setName(name);
						event.setOriginalName(name);
						continue;
					}
					if (startElement.getName().getLocalPart().equals(VENUE)){
						String name = this.getData(xmlEventReader,VENUE);
						venue.setName(name);
						venue.setOriginalName(name);
						continue;
					}
					if (startElement.getName().getLocalPart().equals(VENUE_ID)){
						venue.setId(
								Long.valueOf(this.getData(xmlEventReader,VENUE_ID)));
						continue;
					}
					if (startElement.getName().getLocalPart().equals(COUNTRY_ID)){
						tmpCountryId = Long.valueOf(this.getData(xmlEventReader,COUNTRY_ID));
						continue;
					}
		    }
		
			if (xmlEvent.isEndElement() && xmlEvent.asEndElement().getName().getLocalPart() == (EVENT)) {
				
				Location location = new Location();
				
				Country country = this.tnService.getCountry(tmpCountryId);
				location.setCountry(country);
				
				location.setState(state);
				
				location.setCity(city);
				
				event.setLocation(location);
				
				event.setSource(Source.ticketnetwork);
				
				event.setVenue(venue);
				
				event.setSegmentKey(this.getSegmentKey(country, state));
				
				//category 
				com.ctntn.dto.internal.Category extraCategory = 
						this.tnService.getCategory(tmpChildCategoryId,tmpParentCategoryId,tmpGrandChildCategoryId);
				Category category = new Category();
				category.setChildName(extraCategory.getChildCategoryDescription());
				category.setParentName(extraCategory.getParentCategoryDescription());
				category.setGrandChildName(extraCategory.getGrandchildCategoryDescription());
				event.setCategory(category);

				//date 
				Date dateOut = new Date();
				boolean isFinalTime = 
						this.setDate(dateOut,tmpDate,
								tmpDisplayDate,
								eventDateFormat,
								eventDisplayDateWithoutTimeStringDetection);
				
				event.setDate(dateOut);
				event.setFinalTime(isFinalTime);
				event.setDisplayDate(tmpDisplayDate);
				
//				System.out.println("-------------------------");
//				System.out.println("date-----------------------"+tmpDate);
//				System.out.println("display date---------------"+tmpDisplayDate);
//				System.out.println("date after normalization---"+dateOut);
				
				//performer
				List<EventPerformer> eventPerformers = 
						this.tnService.getPerformersByEventId(Long.valueOf(event.getId()));
				
				if(eventPerformers!=null&&eventPerformers.size()>0){
					Performer performer = null;
					for(EventPerformer eventPerformer : eventPerformers){
						performer = new Performer();
						performer.setId(eventPerformer.getPerformerID());
						performer.setName(eventPerformer.getPerformerName());
						event.getPerformers().add(performer);
					}
				}
				
				events.add(event);				
			}
		}
		return events;
	}
}

//if (startElement.getName().getLocalPart().equals(CLICKS)){
//event.setClicks(
//		this.getData(xmlEventReader,CLICKS));
//continue;
//}

//if (startElement.getName().getLocalPart().equals(IS_WOMEN_EVENT)){
//	event.setIsWomensEvent(
//			this.getData(xmlEventReader,IS_WOMEN_EVENT));
//	continue;
//}

//if (startElement.getName().getLocalPart().equals(MAP_URL)){
//	event.setMapURL(
//			this.getData(xmlEventReader,MAP_URL));
//	continue;
//}
//if (startElement.getName().getLocalPart().equals(INTERACTIVE_MAP_URL)){
//	event.setInteractiveMapURL(
//			this.getData(xmlEventReader,INTERACTIVE_MAP_URL));
//	continue;
//}

//if (startElement.getName().getLocalPart().equals(VENUE_CONFIGURATION_ID)){
//	event.setVenueConfigurationID(
//			this.getData(xmlEventReader,VENUE_CONFIGURATION_ID));
//	continue;
//}
//if (startElement.getName().getLocalPart().equals(STATE_PROVINCE)){
//	state.setAbbreviation(
//			this.getData(xmlEventReader,STATE_PROVINCE));
//	continue;
//}
//if (startElement.getName().getLocalPart().equals(STATE_PROVINCE_ID)){
//	state.setId(
//			Long.valueOf(this.getData(xmlEventReader,STATE_PROVINCE_ID)));
//	continue;
//}
