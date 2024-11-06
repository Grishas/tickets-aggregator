package com.ctntn;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import com.ctnc.GetTicketsStatus;
import com.ctnc.shared.Source;
import com.ctnc.shared.Tickets;
import com.ctnc.shared.TicketsBlock;

public class TNGetTicketsParser extends TNBaseParser{
	
	public Tickets parse(InputStream inputStream,String encoding) throws TNException{
		
		XMLEventReader xmlEventReader = super.getXMLEventReader(inputStream,encoding);

		Tickets tickets = new Tickets();
		
		Map<String,List<TicketsBlock>> blocksBySection = new HashMap<String, List<TicketsBlock>>();
		tickets.setBlocks(blocksBySection);
		
		TicketsBlock ticketsBlock = null;
		
		long eventId = 0;
		BigDecimal facePrice = null;
        long blockId = 0;
        String notes = null;
        BigDecimal retailPrice= null;
        String row = null;
        int ticketQuantity = 0;
//        List<Integer> validSplits = null;
        BigDecimal convertedActualPrice= null;
        BigDecimal actualPrice= null;
        String section = null;
        BigDecimal wholesalePrice= null;

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
				
				if (startElement.getName().getLocalPart().equals(TICKET_GROUP)){	
					ticketsBlock = new TicketsBlock();
					continue;
				}
				
				if (startElement.getName().getLocalPart().equals(EVENT_ID)){
					eventId = Long.valueOf(this.getData(xmlEventReader,EVENT_ID));
					continue;
				}
				
				
				if (startElement.getName().getLocalPart().equals(ID)){
					blockId = Long.valueOf(this.getData(xmlEventReader,ID));
					continue;
				}
				
				if (startElement.getName().getLocalPart().equals(NOTES)){
					notes = this.getData(xmlEventReader,NOTES);
					continue;
				}
								
				if (startElement.getName().getLocalPart().equals(ROW)){
					row = this.getData(xmlEventReader,ROW);
					continue;
				}
				
				if (startElement.getName().getLocalPart().equals(TICKET_QUANTITY)){
					ticketQuantity = Integer.valueOf(this.getData(xmlEventReader,TICKET_QUANTITY));
					continue;
				}

				if (startElement.getName().getLocalPart().equals(INT)){
					ticketsBlock.getSaleSize().add(Integer.valueOf(this.getData(xmlEventReader,INT)));
					continue;
				}
				
				if (startElement.getName().getLocalPart().equals(ACTUAL_PRICE)){
					actualPrice = new BigDecimal(this.getData(xmlEventReader,ACTUAL_PRICE));
					continue;
				}
				
				if (startElement.getName().getLocalPart().equals(CONVERTED_ACTUAL_PRICE)){
					convertedActualPrice = new BigDecimal(this.getData(xmlEventReader,CONVERTED_ACTUAL_PRICE));
					continue;
				}
				
				if (startElement.getName().getLocalPart().equals(SECTION)){
					section = this.getData(xmlEventReader,SECTION);
					continue;
				}
			}
		
			if ( xmlEvent.isEndElement() ) 
			{
				if (xmlEvent.asEndElement().getName().getLocalPart() == (TICKET_GROUP)) {
					
					ticketsBlock.setBlockId(blockId);
					ticketsBlock.setComments(notes);
					ticketsBlock.setPrice(actualPrice);
					
					
					if(row!=null)
					{
						ticketsBlock.setRow(row.toUpperCase());
					}
					else
					{
						ticketsBlock.setRow("");
					}
					
					
					ticketsBlock.setSection(section.toUpperCase());
					ticketsBlock.setTickets(tickets);
					
					ticketsBlock.setTest(
							"wholesalePrice="+wholesalePrice+
							",convertedActualPrice="+convertedActualPrice+
							",actualPrice="+actualPrice+
							",facePrice="+facePrice+
							",retailPrice="+retailPrice+
							",ticketQuantity="+ticketQuantity);
										
					String section_ = ticketsBlock.getSection();
					if(blocksBySection.containsKey( section_ )){
						blocksBySection.get( section_ ).add(ticketsBlock);
					}
					else{
						List<TicketsBlock> ticketsBlocks = new ArrayList<TicketsBlock>();
						ticketsBlocks.add(ticketsBlock);
						blocksBySection.put( section_ , ticketsBlocks);
					}
				}
				
				if (xmlEvent.asEndElement().getName().getLocalPart() == (GET_TICKETS_RESULT)) {
					tickets.setEventId(eventId);
					tickets.setSource(Source.ticketnetwork);
					tickets.setStatus(GetTicketsStatus.found_tickest);
				}
			}
		}
		
		if(tickets.getBlocks().size()==0)
		{
			tickets.setStatus(GetTicketsStatus.not_found_tickest);
		}
		
		return tickets;
	}
}
