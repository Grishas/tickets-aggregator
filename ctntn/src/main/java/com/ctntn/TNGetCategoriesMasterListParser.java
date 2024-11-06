package com.ctntn;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import com.ctntn.dto.internal.Category;

public class TNGetCategoriesMasterListParser extends TNBaseParser{

	public List<Category> parse(InputStream inputStream,String encoding) throws TNException
	{	
		XMLEventReader xmlEventReader = super.getXMLEventReader(inputStream,encoding);

		Category category = null;
		List<Category> categories = new ArrayList<Category>(); 
		
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
				
					if (startElement.getName().getLocalPart().equals(CATEGORY)){	
						category = new Category();
						continue;
					}
					
					if (startElement.getName().getLocalPart().equals(CHILD_CATEGORY_DESCRIPTION)){
						category.setChildCategoryDescription(
								this.getData(xmlEventReader,CHILD_CATEGORY_DESCRIPTION));
						continue;
					}
					if (startElement.getName().getLocalPart().equals(CHILD_CATEGORY_ID)){
						category.setChildCategoryID(
								Long.valueOf(this.getData(xmlEventReader,CHILD_CATEGORY_ID)));
						continue;
					}
					if (startElement.getName().getLocalPart().equals(GRAND_CHILD_CATEGORY_DESCRIPTION)){
						category.setGrandchildCategoryDescription(
								this.getData(xmlEventReader,GRAND_CHILD_CATEGORY_DESCRIPTION));
						continue;
					}
					if (startElement.getName().getLocalPart().equals(GRAND_CHILD_CATEGORY_ID)){
						category.setGrandchildCategoryID(
								Long.valueOf(this.getData(xmlEventReader,GRAND_CHILD_CATEGORY_ID)));
						continue;
					}
					if (startElement.getName().getLocalPart().equals(PARENT_CATEGORY_DESCRIPTION)){
						category.setParentCategoryDescription(
								this.getData(xmlEventReader,PARENT_CATEGORY_DESCRIPTION));
						continue;
					}
					if (startElement.getName().getLocalPart().equals(PARENT_CATEGORY_ID)){
						category.setParentCategoryID(
								Long.valueOf(this.getData(xmlEventReader,PARENT_CATEGORY_ID)));
						continue;
					}
		    }
		
			if (xmlEvent.isEndElement() && xmlEvent.asEndElement().getName().getLocalPart() == (CATEGORY)) {
				categories.add(category);
			}
		}
		return categories;
	}
}
