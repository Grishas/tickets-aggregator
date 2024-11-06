package com.ctntc;

import java.io.InputStream;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

public abstract class TCBaseParser {
	
	public static final String CITY = "City";
	public static final String ID = "ID";
	public static final String NAME = "Name";
	public static final String COUNTRY = "Country";
	public static final String ABBR = "Abbr";
	public static final String STATE = "State";
	public static final String EVENT = "Event";
	public static final String PAGE = "Page";
	public static final String EVENT_DATE_TIME = "EventDateTime";
	public static final String STARTING_PRICE = "StartingPrice";
	public static final String PERFORMER = "Performer";
	public static final String PRIMARY = "Primary";
	public static final String VENUE = "Venue";
	public static final String TICKET_BLOCK = "TicketBlock";
	
	public static final String TICKET_BLOCKS = "TicketBlocks";
	public static final String STATUS_CODE = "StatusCode";
	
	public static final String SECTION = "Section";
	public static final String ROW = "Row";
	public static final String PRICE_EACH = "PriceEach";
	public static final String SERVICE_CHARGE = "ServiceCharge";
	public static final String COMMENTS = "Comments";
	public static final String IS_E_TICKET = "IsETicket";
	public static final String IS_INSTANT_DOWNLOAD = "IsInstantDownload";
	public static final String SALE_SIZE = "SaleSize";
	public static final String QTY = "Qty";

	public XMLEventReader getXMLEventReader(InputStream inputStream,String encoding) throws TCException
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
							xmlInputFactory.createXMLEventReader(inputStream,encoding),new TCParserEventFilter());
			
		} catch (XMLStreamException e) {
			throw new TCException("Can't create XMLEventReader",e);
		}
		
		return xmlEventReader;
	}

}
