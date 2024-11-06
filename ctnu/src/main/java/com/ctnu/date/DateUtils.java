package com.ctnu.date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.ctnu.UtilsException;

public class DateUtils {

	private static Map<String, SimpleDateFormat> hashFormatters = new HashMap<String, SimpleDateFormat>();
	public final static String DATE_FORMAT = "dd/MM/yyyy";  // "d MMM yy";


	public static Date parseDate(String date, String format) throws UtilsException
	{
	    SimpleDateFormat formatter = hashFormatters.get(format);

	    if (formatter == null){
	        formatter = new SimpleDateFormat(format);
	        hashFormatters.put(format, formatter);
	    }

	    Date result = null;
		try {
			result = formatter.parse(date);
		} catch (ParseException e) {
			throw new UtilsException(e); 
		}
	    
	    return result;
	}
	

	public static void main(String[] args) {
		
//		Calendar test = Calendar.getInstance();
//		
//		test.setTimeInMillis(1441918800000l);
//		
//		System.out.println(test.getTime());
		
//		try {
//			
//			//tc
//			Date date1 = DateUtils.parseDate("2016-01-03T12:00:00", "yyyy-MM-dd'T'HH:mm:ss");
//			System.out.println(date1);//Tue Jan 19 19:30:00 IST 2016
//			
////			Date date2 = DateUtils.parseDate("02/19/2016 7:30PM", "MM/dd/yyyy H:mma");
////			System.out.println(date2);//Fri Feb 19 07:30:00 IST 2016
//			
//			//tn
////			Date date3 = DateUtils.parseDate("02/19/2016 7:30PM", "mm/dd/yyyy hh:mma");
////			System.out.println(date3);
//			
//			
//		} catch (UtilsException e) {
//			e.printStackTrace();
//		}
		
	}
	
	
	public static Date clearTime(Date date) {
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.clear(Calendar.HOUR_OF_DAY);
		calendar.clear(Calendar.HOUR);
		calendar.clear(Calendar.AM_PM);
		calendar.clear(Calendar.MINUTE);
		calendar.clear(Calendar.SECOND);
		calendar.clear(Calendar.MILLISECOND);
		return calendar.getTime();

    }

	public static String dateToStringByFormat(Date date, String format) {
		
	    SimpleDateFormat formatter = hashFormatters.get(format);

	    if (formatter == null){
	        formatter = new SimpleDateFormat(format);
	        hashFormatters.put(format, formatter);
	    }
	    
	    return formatter.format(date);	
	}

	public static Date addMillisecondsToDate(Date date, long addMillisecondsToDate) 
	{	
		return new Date( date.getTime() + addMillisecondsToDate );
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
