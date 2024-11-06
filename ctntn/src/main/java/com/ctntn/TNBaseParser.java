package com.ctntn;
import java.io.InputStream;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import com.ctnc.Country;
import com.ctntn.dto.internal.Category;

public abstract class TNBaseParser {
	
	protected List<Category> categories = null;
	protected List<Country> countries = null;

	public static final String EVENT = "Event";
	public static final String CHILD_CATEGORY_ID = "ChildCategoryID";
	public static final String CITY = "City";
	public static final String CLICKS = "Clicks";
	public static final String DATE = "Date";
	public static final String DISPLAY_DATE = "DisplayDate";
	public static final String GRAND_CHILD_CATEGORY_ID = "GrandchildCategoryID";
	public static final String ID = "ID";
	public static final String IS_WOMEN_EVENT = "IsWomensEvent";
	public static final String MAP_URL = "MapURL";
	public static final String INTERACTIVE_MAP_URL ="InteractiveMapURL";
	public static final String NAME = "Name";
	public static final String PARENT_CATEGORY_ID = "ParentCategoryID";
	public static final String STATE_PROVINCE = "StateProvince";
	public static final String STATE_PROVINCE_ID = "StateProvinceID";
	public static final String VENUE = "Venue";
	public static final String VENUE_CONFIGURATION_ID = "VenueConfigurationID";
	public static final String VENUE_ID = "VenueID";
	public static final String COUNTRY_ID = "CountryID";
	public static final String COUNTRY = "Country";
	public static final String ABBREVIATION = "Abbreviation";
	public static final String STATES = "States";
	public static final String STATE_PROVINCE_SHORT_DESC = "StateProvinceShortDesc";
	public static final String STATE_PROVINCE_LONG_DESC = "StateProvinceLongDesc";
	public static final String COUNTRY_DESC = "CountryDesc";
	public static final String EVENT_ID = "EventID";
	public static final String PERFORMER_ID = "PerformerID";
	public static final String PERFORMER_NAME = "PerformerName";
	public static final String EVENT_PERFORMER = "EventPerformer";
	public static final String BOX_OFFICE_PHONE = "BoxOfficePhone";
	public static final String CAPACITY = "Capacity";
	public static final String NUMBER_OF_CONFIGURATION = "NumberOfConfigurations";
	public static final String PARKING = "Parking";
	public static final String PUBLIC_TRANSPORTATION = "PublicTransportation";
	public static final String STREET1 = "Street1";
	public static final String STREET2 = "Street2";
	public static final String URL = "URL";
	public static final String WILL_CALL = "WillCall";
	public static final String ZIP_CODE = "ZipCode";
	public static final String CATEGORY = "Category";
	public static final String CHILD_CATEGORY_DESCRIPTION = "ChildCategoryDescription";
	public static final String GRAND_CHILD_CATEGORY_DESCRIPTION = "GrandchildCategoryDescription";
	public static final String PARENT_CATEGORY_DESCRIPTION = "ParentCategoryDescription";
	public static final String TICKET_GROUP = "TicketGroup";
	public static final String NOTES = "Notes";
	public static final String ROW = "Row";
	public static final String TICKET_QUANTITY = "TicketQuantity";
	public static final String VALID_SPLITS = "ValidSplits";
	public static final String SECTION = "Section";    
	public static final String RETAIL_PRICE = "RetailPrice";
	public static final String ACTUAL_PRICE = "ActualPrice";
	public static final String WHOLESALE_PRICE = "WholesalePrice";
	public static final String FACE_PRICE = "FacePrice";  
	public static final String GET_TICKETS_RESULT  = "GetTicketsResult";
	public static final String INT  = "int";
	public static final String CONVERTED_ACTUAL_PRICE  = "convertedActualPrice";
	
	protected XMLEventReader getXMLEventReader(InputStream inputStream,String encoding) throws TNException
	{
		XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
		xmlInputFactory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES,Boolean.TRUE);
		xmlInputFactory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES,Boolean.FALSE);
		xmlInputFactory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE,Boolean.TRUE);
		xmlInputFactory.setProperty(XMLInputFactory.IS_COALESCING,Boolean.TRUE);
		
		//https://docs.oracle.com/javase/tutorial/jaxp/stax/api.html
		//Iterator API
		XMLEventReader xmlEventReader = null;
		try {
			xmlEventReader = 
					xmlInputFactory.createFilteredReader(
							xmlInputFactory.createXMLEventReader(inputStream,encoding),new TNParserEventFilter());
			
		} catch (XMLStreamException e) {
			throw new TNException("Can't create XMLEventReader",e);
		}
		
		return xmlEventReader;
	}
	
	protected String getData(XMLEventReader xmlEventReader,String field) throws TNException{
		
		XMLEvent xmlEvent = null;
		try {
			xmlEvent = xmlEventReader.nextEvent();
		} catch (XMLStreamException e) {
			throw new TNException("xmlEventReader.nextEvent().Fail to get data for: "+field,e);
		}
		
		//no data
		if(xmlEvent.isCharacters()==true){
			return xmlEvent.asCharacters().getData();
		}
		else{
			return null;
		}
	}
}
