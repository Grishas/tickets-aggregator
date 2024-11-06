package com.ctntc;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import com.ctnc.Country;

public class TCGetCountriesParser extends TCBaseParser{

	public List<Country> parse(InputStream inputStream,String encoding) throws TCException
	{
		List<Country> countries = new ArrayList<Country>();
	
		XMLEventReader xmlEventReader = super.getXMLEventReader(inputStream,encoding);

		Country country  = null;
		
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
		        
				try
				{            
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
			      
			       
				}
				catch(NullPointerException e){
					throw new TCException("Attribute key changed",e);
				}
		    }
		
			if (xmlEvent.isEndElement() && xmlEvent.asEndElement().getName().getLocalPart() == (COUNTRY)) 
			{
				countries.add(country);
			}
		}
		
		return countries;
	}
}
