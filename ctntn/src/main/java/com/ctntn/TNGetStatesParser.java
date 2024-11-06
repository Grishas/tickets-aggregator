package com.ctntn;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import com.ctnc.State;

public class TNGetStatesParser extends TNBaseParser{

	public List<State> parse(InputStream inputStream,String encoding) throws TNException
	{	
		XMLEventReader xmlEventReader = super.getXMLEventReader(inputStream,encoding);

		State state = null;
		List<State> states = new ArrayList<State>(); 
		
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
				
					if (startElement.getName().getLocalPart().equals(STATES)){	
						state = new State();
						continue;
					}
					
					if (startElement.getName().getLocalPart().equals(STATE_PROVINCE_SHORT_DESC)){
						state.setAbbreviation(
								this.getData(xmlEventReader,STATE_PROVINCE_SHORT_DESC));
						continue;
					}
					
					if (startElement.getName().getLocalPart().equals(STATE_PROVINCE_LONG_DESC)){
						state.setName(
								this.getData(xmlEventReader,STATE_PROVINCE_LONG_DESC));
						continue;
					}
					
					if (startElement.getName().getLocalPart().equals(STATE_PROVINCE_ID)){
						state.setId(
								Long.valueOf(this.getData(xmlEventReader,STATE_PROVINCE_ID)));
						continue;
					}
					
//					if (startElement.getName().getLocalPart().equals(COUNTRY_DESC)){
//						state.setCountryDesc(
//								this.getData(xmlEventReader,COUNTRY_DESC));
//						continue;
//					}
		    }
		
			if (xmlEvent.isEndElement() && xmlEvent.asEndElement().getName().getLocalPart() == (STATES)) {
				states.add(state);
			}
		}
		return states;
	}
}
