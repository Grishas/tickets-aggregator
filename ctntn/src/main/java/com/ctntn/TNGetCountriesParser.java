package com.ctntn;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import com.ctnc.Country;

public class TNGetCountriesParser extends TNBaseParser{

	public List<Country> parse(InputStream inputStream,String encoding) throws TNException
	{	
		XMLEventReader xmlEventReader = super.getXMLEventReader(inputStream,encoding);

		com.ctnc.Country country = null;
		List<Country> countries = new ArrayList<Country>(); 
		
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
				
					if (startElement.getName().getLocalPart().equals(COUNTRY)){	
						country = new Country();
						continue;
					}
					
					if (startElement.getName().getLocalPart().equals(ABBREVIATION)){
						country.setAbbreviation(
								this.getData(xmlEventReader,ABBREVIATION));
						continue;
					}
					if (startElement.getName().getLocalPart().equals(ID)){
						country.setId(
								Long.valueOf(this.getData(xmlEventReader,ID)));
						continue;
					}
					if (startElement.getName().getLocalPart().equals(NAME)){
						country.setName(
								this.getData(xmlEventReader,NAME));
						continue;
					}		
		    }
		
			if (xmlEvent.isEndElement() && xmlEvent.asEndElement().getName().getLocalPart() == (COUNTRY)) {
				countries.add(country);
			}
		}
		return countries;
	}
	
}
