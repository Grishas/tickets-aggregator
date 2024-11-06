package com.ctntn;

import javax.xml.stream.EventFilter;
import javax.xml.stream.events.XMLEvent;

public class TNParserEventFilter implements EventFilter {

	@Override
	public boolean accept(XMLEvent xmlEvent) {
//		if (!xmlEvent.isStartElement() && !xmlEvent.isEndElement())
//	        return false;
//	    else
	        return true;
	}

}
