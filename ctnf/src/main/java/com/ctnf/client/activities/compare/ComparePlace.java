package com.ctnf.client.activities.compare;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ctnc.shared.Source;
import com.ctnf.client.CalendarFactory;
import com.ctnf.client.activities.search.SearchPlace;
import com.ctnf.client.utils.Utils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class ComparePlace extends Place {
	
		private static final String PREFIX= "!compare";
		private static final String EVENT_NAME= "event";
		private static final String DATE= "date";
		private static final String CITIES_CLOSE = "nearby-cities";
		private static final String SEGMENT_KEY = "country-state";
		private static final String VENUE= "venue";
		private static final String MARKET = "market";
		private static final String EQ = "=";
		private static final String AMPS = "&";
		
		private String eventName = null;
		private String date = null;
		private List<String> citiesClose = null;
		private String segmentKey = null;
		private String venue = null;
		private Map<Source,Long> sources = null;
		private SearchPlace searchPlace = null;
		
		public ComparePlace(String eventName,String date,List<String> citiesClose,String segmentKey,String venue,Map<Source,Long> sources, SearchPlace searchPlace) {
			this.eventName = eventName;
			this.date = date;
			this.citiesClose = citiesClose;
			this.segmentKey = segmentKey;
			this.venue = venue;
			this.sources = sources;
			this.searchPlace = searchPlace;
		}

		
		
		public SearchPlace getSearchPlace() {
			return searchPlace;
		}



		public void setSearchPlace(SearchPlace searchPlace) {
			this.searchPlace = searchPlace;
		}

		public Map<Source, Long> getSources() {
			return sources;
		}

		public void setSources(Map<Source, Long> sources) {
			this.sources = sources;
		}

		public String getEventName() {
			return eventName;
		}

		public void setEventName(String eventName) {
			this.eventName = eventName;
		}

		

		public String getDate() {
			return date;
		}



		public void setDate(String date) {
			this.date = date;
		}



		public List<String> getCitiesClose() {
			return citiesClose;
		}

		public void setCitiesClose(List<String> citiesClose) {
			this.citiesClose = citiesClose;
		}

		public String getSegmentKey() {
			return segmentKey;
		}

		public void setSegmentKey(String segmentKey) {
			this.segmentKey = segmentKey;
		}

		public String getVenue() {
			return venue;
		}

		public void setVenue(String venue) {
			this.venue = venue;
		}

		public String getUrl()
		{
			Tokenizer tokenizer = new Tokenizer();
			StringBuilder url = new StringBuilder(); 
			url.append(com.google.gwt.core.client.GWT.getHostPageBaseURL());
			url.append("#"+PREFIX+":");
			url.append(tokenizer.getToken(this));		
			return url.toString();
		}

		@Prefix(PREFIX)
		public static class Tokenizer implements PlaceTokenizer<ComparePlace>
		{
			@Override
			public ComparePlace getPlace(String token) 
			{
				return this.toComparePlace(token);
			}

			@Override
			public String getToken(ComparePlace place) 
			{	
//				DateTimeFormat dateTimeFormat = (DateTimeFormat)CalendarFactory.getFormatter(place.isFinalTime());
//				
//				String date = dateTimeFormat.format(place.getDate());
//				
//				if(place.isFinalTime()==false)
//				{
//					date = date+" TBA";
//				}

				StringBuilder sourceId = new StringBuilder();
				
				for(Map.Entry<Source,Long> entry : place.getSources().entrySet())
				{
					sourceId.append(MARKET+EQ+entry.getKey().name()+"_"+entry.getValue()+AMPS);
				}
				sourceId.deleteCharAt(sourceId.length()-1);
				
				String url = EVENT_NAME 	 +EQ+  Utils.normalizeTokenText(place.getEventName())   	+AMPS+ 
						DATE 	     +EQ+  place.getDate().replace(" ","-").toLowerCase()               +AMPS+ 
						VENUE 		 +EQ+  Utils.normalizeTokenText(place.getVenue())	    			+AMPS+
						CITIES_CLOSE +EQ+  Utils.getCitiesCloseAsString(place.getCitiesClose(),true)	+AMPS+ 
						SEGMENT_KEY  +EQ+  place.getSegmentKey()						 				+AMPS+ 
						sourceId;
								
				return url;
				
			}
			
			private ComparePlace toComparePlace(String token)
			{
				Map<String,Object> parameters = this.parse(token);
				
				String eventName = String.valueOf(parameters.get(EVENT_NAME)).replace("-"," ");//.replace("'","");
				
				String dateAsString = String.valueOf(parameters.get(DATE)).toLowerCase();
				
				//in case of direct access to urls date is in lower case
				dateAsString = this.getPresentationDate(dateAsString);
				
				
				
				
				
				String citiesClose = String.valueOf(parameters.get(CITIES_CLOSE));
				String[] citiesCloseArray = citiesClose.split(",");
				List<String> citiesCloseList = new ArrayList<String>(citiesCloseArray.length);
				for(String city : citiesCloseArray)
				{
					citiesCloseList.add(city.replace("-"," "));
				}
				
				String segmentKey = String.valueOf(parameters.get(SEGMENT_KEY));
				
				String venue = String.valueOf(parameters.get(VENUE)).replace("-"," ");
				
				
				@SuppressWarnings("unchecked")
				List<String> markets =(ArrayList<String>)parameters.get(MARKET);
				Map<Source,Long> sources = new HashMap<>();
				
				for(String market : markets)
				{
					String[] tmp = market.split("_");
					sources.put(Source.valueOf(tmp[0]),Long.valueOf(tmp[1]));
				}
				
				return new ComparePlace(eventName,dateAsString,citiesCloseList,segmentKey,venue,sources,null);
			}
			
			private String getPresentationDate(String dateAsString)
			{
				boolean isFinalTime = true;
				if(dateAsString.contains("tba")||dateAsString.toLowerCase().contains("tbd"))
				{
					isFinalTime = false;
					dateAsString = dateAsString.replace("-tba","");
					dateAsString = dateAsString.replace("-tbd","");
				}
				dateAsString = dateAsString.replace("-"," ");
				
				Date date = null;
				if(dateAsString!=null)
				{
					try
					{
						date = CalendarFactory.toDate(dateAsString, isFinalTime);
					}
					catch(IllegalArgumentException e){
						GWT.log(e.getMessage());
						return dateAsString;
					}
				}
				
				DateTimeFormat dateTimeFormat = (DateTimeFormat)CalendarFactory.getFormatter(isFinalTime);
				
			    dateAsString = dateTimeFormat.format(date);
				
				if(isFinalTime==false)
				{
					dateAsString = dateAsString+" TBA";
				}
				
				return dateAsString;
			}
			
			
			
			
			@SuppressWarnings("unchecked")
			private final Map<String, Object> parse(String token) {

			    Map<String, Object> map = new HashMap<String, Object>();
			    
			    if (token != null) 
			    {
			        String[] parameters = token.split(AMPS);
			        
			        for (String parameter : parameters) 
			        {
				            String[] keyValue = parameter.split(EQ);
	
				            if (keyValue.length > 1) 
				            {
					            if(keyValue[0].equals(MARKET)&&map.containsKey(MARKET))
					            {
					            	((ArrayList<String>)map.get(MARKET)).add(keyValue[1]);
					            	continue;
					            }
					            else if(keyValue[0].equals(MARKET))
					            {
					            	List<String> markets = new ArrayList<String>();
					            	markets.add(keyValue[1]);
					            	map.put(MARKET,markets);
					            	continue;
					            }
					            
				            	map.put(keyValue[0], keyValue[1]);
				            }
			         }
			    }
			    
			    return map;
			}
		}
}
