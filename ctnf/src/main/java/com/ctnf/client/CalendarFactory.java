package com.ctnf.client;
import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;

public class CalendarFactory {

	//EEE, MMM d, ''yy"	Wed, July 10, '96
	//h:mm a"	12:08 PM
	
	private static String DATE_FORMAT_TIME ="d MMM ''yy,EEE h:mm a";
	private static String DATE_FORMAT_SHORT ="d MMM ''yy,EEE";
	
	public static DateTimeFormat getFormatter(boolean showTime) 
	{	
		if(showTime==true)
		{	
			return DateTimeFormat.getFormat(DATE_FORMAT_TIME);
		}
		else
		{
			return DateTimeFormat.getFormat(DATE_FORMAT_SHORT);
		}
	}
	
	public static Date toDate(String date,boolean showTime) 
	{	
		if(showTime==true)
		{	
			return DateTimeFormat.getFormat(DATE_FORMAT_TIME).parse(date);
		}
		else
		{
			return DateTimeFormat.getFormat(DATE_FORMAT_SHORT).parse(date);
		}
	}
}
