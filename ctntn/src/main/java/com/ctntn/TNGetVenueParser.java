package com.ctntn;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import com.ctntn.dto.internal.Venue;

public class TNGetVenueParser extends TNBaseParser{

	public List<Venue> parse(InputStream inputStream,String encoding) throws TNException
	{	
		XMLEventReader xmlEventReader = super.getXMLEventReader(inputStream,encoding);

		Venue venue = null;
		List<Venue> venues = new ArrayList<Venue>(); 
		
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
				
					if (startElement.getName().getLocalPart().equals(VENUE)){	
						venue = new Venue();
						continue;
					}
					
					if (startElement.getName().getLocalPart().equals(BOX_OFFICE_PHONE)){
						venue.setBoxOfficePhone(
								this.getData(xmlEventReader,BOX_OFFICE_PHONE));
						continue;
					}
					
					if (startElement.getName().getLocalPart().equals(CAPACITY)){
						venue.setCapacity(
								this.getData(xmlEventReader,CAPACITY));
						continue;
					}
					
					if (startElement.getName().getLocalPart().equals(BOX_OFFICE_PHONE)){
						venue.setBoxOfficePhone(
								this.getData(xmlEventReader,BOX_OFFICE_PHONE));
						continue;
					}
					
					if (startElement.getName().getLocalPart().equals(CITY)){
						venue.setCity(
								this.getData(xmlEventReader,CITY));
						continue;
					}
					if (startElement.getName().getLocalPart().equals(COUNTRY)){
						venue.setCountry(
								this.getData(xmlEventReader,COUNTRY));
						continue;
					}
					if (startElement.getName().getLocalPart().equals(ID)){
						venue.setId(
								this.getData(xmlEventReader,ID));
						continue;
					}
					if (startElement.getName().getLocalPart().equals(NAME)){
						venue.setName(
								this.getData(xmlEventReader,NAME));
						continue;
					}
					if (startElement.getName().getLocalPart().equals(NUMBER_OF_CONFIGURATION)){
						venue.setNumberOfConfigurations(
								this.getData(xmlEventReader,NUMBER_OF_CONFIGURATION));
						continue;
					}
					if (startElement.getName().getLocalPart().equals(PARKING)){
						venue.setParking(
								this.getData(xmlEventReader,PARKING));
						continue;
					}
					if (startElement.getName().getLocalPart().equals(PUBLIC_TRANSPORTATION)){
						venue.setPublicTransportation(
								this.getData(xmlEventReader,PUBLIC_TRANSPORTATION));
						continue;
					}
					if (startElement.getName().getLocalPart().equals(STATE_PROVINCE)){
						venue.setStateProvince(
								this.getData(xmlEventReader,STATE_PROVINCE));
						continue;
					}
					
					if (startElement.getName().getLocalPart().equals(STREET1)){
						venue.setStreet1(
								this.getData(xmlEventReader,STREET1));
						continue;
					}
					
					if (startElement.getName().getLocalPart().equals(STREET2)){
						venue.setStreet2(
								this.getData(xmlEventReader,STREET2));
						continue;
					}
					if (startElement.getName().getLocalPart().equals(URL)){
						venue.setUrl(
								this.getData(xmlEventReader,URL));
						continue;
					}
					if (startElement.getName().getLocalPart().equals(WILL_CALL)){
						venue.setWillCall(
								this.getData(xmlEventReader,WILL_CALL));
						continue;
					}
					if (startElement.getName().getLocalPart().equals(ZIP_CODE)){
						venue.setZipCode(
								this.getData(xmlEventReader,ZIP_CODE));
						continue;
					}	
		    }
		
			if (xmlEvent.isEndElement() && xmlEvent.asEndElement().getName().getLocalPart() == (VENUE)) {
				venues.add(venue);
			}
		}
		
		return venues;
	}
	
}
