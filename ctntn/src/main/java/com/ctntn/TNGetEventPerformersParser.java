package com.ctntn;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import com.ctntn.dto.internal.EventPerformer;

public class TNGetEventPerformersParser extends TNBaseParser{

	public Map<Long,List<EventPerformer>> parse(InputStream inputStream,String encoding) throws TNException
	{	
		XMLEventReader xmlEventReader = super.getXMLEventReader(inputStream,encoding);

		EventPerformer eventPerformer = null;
		Map<Long,List<EventPerformer>> eventPerformersByEventId= new HashMap<Long,List<EventPerformer>>();
		
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
				
					if (startElement.getName().getLocalPart().equals(EVENT_PERFORMER)){	
						eventPerformer = new EventPerformer();
						continue;
					}
					
					if (startElement.getName().getLocalPart().equals(EVENT_ID)){
						eventPerformer.setEventID(
								Long.valueOf(this.getData(xmlEventReader,EVENT_ID)));
						continue;
					}
					
					if (startElement.getName().getLocalPart().equals(PERFORMER_ID)){
						eventPerformer.setPerformerID(
								Long.valueOf(this.getData(xmlEventReader,PERFORMER_ID)));
						continue;
					}
					
					if (startElement.getName().getLocalPart().equals(PERFORMER_NAME)){
						eventPerformer.setPerformerName(
								this.getData(xmlEventReader,PERFORMER_NAME));
						continue;
					}
		    }
		
			if (xmlEvent.isEndElement() && xmlEvent.asEndElement().getName().getLocalPart() == (EVENT_PERFORMER)) 
			{
				if( eventPerformersByEventId.containsKey(eventPerformer.getEventID()) ){
					eventPerformersByEventId.get(eventPerformer.getEventID()).add(eventPerformer);
				}
				else{
					List<EventPerformer> eventPerformers = new ArrayList<EventPerformer>();
					eventPerformers.add(eventPerformer);
					eventPerformersByEventId.put(eventPerformer.getEventID(),eventPerformers);
				}
			}
		}
		return eventPerformersByEventId;
	}
}
























