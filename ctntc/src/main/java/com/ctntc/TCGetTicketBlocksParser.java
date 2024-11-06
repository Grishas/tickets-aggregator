package com.ctntc;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import com.ctnc.GetTicketsStatus;
import com.ctnc.shared.Source;
import com.ctnc.shared.Tickets;
import com.ctnc.shared.TicketsBlock;

public class TCGetTicketBlocksParser extends TCBaseParser{

	public Tickets parse(InputStream inputStream,String encoding) throws TCException{
		
		XMLEventReader xmlEventReader = super.getXMLEventReader(inputStream,encoding);

		Tickets tickets = null;
		TicketsBlock ticketsBlock = null;
		Map<String,List<TicketsBlock>> blocksBySection = null;

		while (xmlEventReader.hasNext()) 
		{
			XMLEvent xmlEvent = null;
			try {
				xmlEvent = xmlEventReader.nextEvent();
			} catch (XMLStreamException e) {
				throw new TCException("xmlEventReader.nextEvent() fail",e);
			}

			if ( xmlEvent.isStartElement() ){
				
				StartElement startElement = xmlEvent.asStartElement();
		        
				if (startElement.getName().getLocalPart().equals(TICKET_BLOCKS ))
				{
					int statusCode = Integer.valueOf(startElement.getAttributeByName(new QName(STATUS_CODE)).getValue());
					
					if(statusCode==0)
					{
						tickets = new Tickets();
						tickets.setStatus(GetTicketsStatus.found_tickest);
						continue;
					}

					//<TicketBlocks Version="3.0" Method="GetTicketBlocks" StatusCode="3" StatusMsg="No Tickets Available" xmlns="">
					if(statusCode==3)
					{
						tickets = new Tickets();
						tickets.setStatus(GetTicketsStatus.not_found_tickest);
						tickets.setSource(Source.ticketcity);		
						tickets.setBlocks(new HashMap<String,List<TicketsBlock>>(0));
						return tickets;
					}
					
					//<TicketBlocks StatusCode='2' Method='GetTicketBlocks' StatusMsg='Invalid APIKey' Version='3.0' xmlns:='null'>
					if(statusCode==2)
					{
						tickets = new Tickets();
						tickets.setStatus(GetTicketsStatus.access_error);
						tickets.setSource(Source.ticketcity);						
						return tickets;
					}
					
					continue;
				}
				
				if (startElement.getName().getLocalPart().equals(EVENT)){
				
					blocksBySection = new HashMap<String,List<TicketsBlock>>();
					tickets.setBlocks(blocksBySection);

				    tickets.setPage(startElement.getAttributeByName(new QName(PAGE)).getValue());  	
					
					tickets.setSource(Source.ticketcity);

//					tickets.setDate(startElement.getAttributeByName(new QName(EVENT_DATE_TIME)).getValue());  	

				    //debug
					tickets.setEventId(Long.valueOf(startElement.getAttributeByName(new QName(ID)).getValue()));
//					tickets.setEventName(startElement.getAttributeByName(new QName(NAME)).getValue());  
					//debug

					continue;
				}
				
				if (startElement.getName().getLocalPart().equals(TICKET_BLOCK )){
					
					ticketsBlock = new TicketsBlock();
					ticketsBlock.setBlockId(Long.valueOf(startElement.getAttributeByName(new QName(ID)).getValue()));
					ticketsBlock.setSection(startElement.getAttributeByName(new QName(SECTION)).getValue().toUpperCase());
					ticketsBlock.setRow(startElement.getAttributeByName(new QName(ROW)).getValue().toUpperCase());
					
					BigDecimal serviceCharge = new BigDecimal(startElement.getAttributeByName(new QName(SERVICE_CHARGE)).getValue());
					BigDecimal priceEach = new BigDecimal(startElement.getAttributeByName(new QName(PRICE_EACH)).getValue());
					
					//ticketsBlock.setPrice(serviceCharge.add(priceEach));
					//remove service fee cause of don't know delivery price, show service fee in comments
					ticketsBlock.setPrice(priceEach);
					ticketsBlock.setServiceCharge(serviceCharge);
		
					//ticketsBlock.setServiceCharge(new BigDecimal(startElement.getAttributeByName(new QName(SERVICE_CHARGE)).getValue()));
					ticketsBlock.setComments(startElement.getAttributeByName(new QName(COMMENTS)).getValue());
					ticketsBlock.setEticket(Boolean.valueOf(startElement.getAttributeByName(new QName(IS_E_TICKET)).getValue()));
					ticketsBlock.setInstantDownload(Boolean.valueOf(startElement.getAttributeByName(new QName(IS_INSTANT_DOWNLOAD)).getValue()));
					ticketsBlock.setTickets(tickets);
					
//					ticketsBlock.setPriceTest(new BigDecimal(startElement.getAttributeByName(new QName(PRICE_EACH)).getValue()));
					
					//ticketsBlock.getPriceTest().doubleValue();

					String section = ticketsBlock.getSection();
					
					if( blocksBySection.containsKey( section ) ){
						blocksBySection.get( section ).add( ticketsBlock );
					}
					else{
						List<TicketsBlock> ticketsBlocks = new ArrayList<TicketsBlock>();
						ticketsBlocks.add( ticketsBlock );
						blocksBySection.put( section , ticketsBlocks );
					}
					
				    continue;
				 }
				
				if (startElement.getName().getLocalPart().equals(SALE_SIZE)){
					ticketsBlock.getSaleSize().add(
							Integer.valueOf(startElement.getAttributeByName(new QName(QTY)).getValue()));
					continue;
				}
			}
		}	
		
		if(tickets==null){
			tickets = new Tickets();
			tickets.setStatus(GetTicketsStatus.not_found_tickest);
			tickets.setSource(Source.ticketcity);	
			tickets.setBlocks(new HashMap<String,List<TicketsBlock>>(0));			
		}
		
		return tickets;
	}
}
