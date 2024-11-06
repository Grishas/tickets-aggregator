package com.ctnf.client.activities.search;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import com.ctnc.Constants;
import com.ctnc.shared.IndexKey;
import com.ctnc.shared.Location;
import com.ctnc.shared.QueryLocationIndex;
import com.ctnc.shared.UserLocation;
import com.ctnf.client.Factory;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;
import com.google.gwt.user.datepicker.client.CalendarUtil;

public class SearchPlace extends Place {

	private static final String PREFIX= "!search";
	private static final String EQ = "=";
	private static final String AMPS = "&";
	
	private static final String EVENT 		= "event";
	private static final String VENUE 		= "venue";
	private static final String PERFORMER 	= "performer";
	
	private static final String CITY = "city";
	private static final String SEGMENT_KEY = "country-state";
	
	private static final String LATITUDE = "latitude";
	private static final String LONGITUDE = "longitude";
	
	private static final String FROM = "from";
	private static final String TO = "to";

	private IndexKey 			queryFieldName 		= null;//event , performer or venue
	private String 				query 				= null;

	private String 				from 				= null;
	private String 				to 					= null;
	
	private Location location;
	private QueryLocationIndex 	howToSearchLocation  = null;

	private boolean 			clearSearchView 	= false;

	public SearchPlace(){}

	
	@Override
	public String toString() {
		return "SearchPlace [queryFieldName=" + queryFieldName + ", query="
				+ query + ", from=" + from + ", to=" + to + ", location="
				+ location + ", howToSearchLocation=" + howToSearchLocation
				+ ", clearSearchView=" + clearSearchView + "]";
	}


	//welcome / home case
	public SearchPlace(QueryLocationIndex howToSearchLocation,Location location,boolean clearSearchView) 
	{	
		this.howToSearchLocation = howToSearchLocation;
		this.location = location;
		
		this.clearSearchView = clearSearchView;

		this.queryFieldName = null;
		this.query = null;
	}
	
	public SearchPlace(IndexKey queryFieldName,String query,QueryLocationIndex howToSearchLocation,Location location) 
	{
		this.queryFieldName = queryFieldName;
		this.query = query;
		this.howToSearchLocation = howToSearchLocation;
		this.location = location;
	}
	
	public SearchPlace(String from,String to,QueryLocationIndex queryLocationIndex) {
		this.from = from;
		this.to = to;
		this.howToSearchLocation = queryLocationIndex;
		
	}
	
	public SearchPlace(QueryLocationIndex howToSearchLocation,Location location,String from,String to,boolean clearSearchView) 
	{
		this.howToSearchLocation = howToSearchLocation;
		this.location = location;
		
		this.clearSearchView = clearSearchView;

		this.queryFieldName = null;
		this.query = null;
		
		this.from = from;
		this.to = to;
		
	}
	
	
	
	
	
	public SearchPlace(IndexKey queryFieldName, String query, String from, String to,QueryLocationIndex howToSearchLocation,Location location) {
		
		this.queryFieldName = queryFieldName;
		this.query = query;
		this.from = from;
		this.to = to;
		this.howToSearchLocation = howToSearchLocation;
		this.location = location;

	}
	
	public String getUrl()
	{
		Tokenizer tokenizer = new Tokenizer();
		StringBuilder url = new StringBuilder(); 
		//url.append("compareticketsnow.com");
		url.append(com.google.gwt.core.client.GWT.getHostPageBaseURL());
		url.append("#"+PREFIX+":");
		url.append(tokenizer.getToken(this));		
		return url.toString();
	}
	
	@Prefix(PREFIX)
	public static class Tokenizer implements PlaceTokenizer<SearchPlace>
	{
		@Override
		public String getToken(SearchPlace place) 
		{		
			/*
			
			event/venue/performer=query & from=date & to=data & city=city-name & state-country= seg-key 
			event/venue/performer=query & from=date & to=data & lat=0987 & lon=-01888
			event/venue/performer=query & lat=0987 & lon=-01888
			event/venue/performer=query & from=date & to=data
			event/venue/performer=query 
			from=date & to=data & lat=0987 & lon=-01888
			from=date & to=data & city=city-name & state-country= seg-key 
			lat=0987 & lon=-01888
			from=date & to=data
			.....
			*/
			StringBuilder token = new StringBuilder();

			this.setQuery(		place	, token	 );
			
			this.setLocation(	place	, token  );

			this.setDate( 		place	, token  );
			
			return token.toString().toLowerCase();	
			
		}

		private void setQuery(SearchPlace place,StringBuilder token)
		{
			//event/venue/performer=query
			if(place.getQuery()!=null && place.getQuery().length() > 0)
			{
				String query = place.getQuery().replaceAll("[^A-Za-z0-9]"," ").trim();
				query = query.replaceAll("\\s+", "-");
				token.append( place.getQueryFieldName().name() + EQ + query );
			}	
		}
		
		private void setDate(SearchPlace place,StringBuilder token)
		{
			if( place.from != null && place.to != null )
			{
				//String from = DateTimeFormat.getFormat("d-MMM-yy").format(place.from);
				//String to = DateTimeFormat.getFormat("d-MMM-yy").format(place.to);
				token.append( ((token.length()>0)?AMPS:"") + FROM + EQ + place.from + AMPS + TO + EQ + place.to);	
			}			
		}
		
		private void setLocation(SearchPlace place,StringBuilder token)
		{	
			if(place.getQueryLocationIndex().equals(QueryLocationIndex.ignore))
			{
				return;
			}
			
			if(place.getQueryLocationIndex() != null && place.getQueryLocationIndex().equals(QueryLocationIndex.city))
			{
				if(place.getLocation() !=null && place.getLocation()!=null && place.getLocation().getSegmentedKey()!=null)
				{
					String city = new String(place.getLocation().getCity());
					
					city = 	city.replaceAll("[^A-Za-z0-9]"," ").trim();
					city = city.replaceAll("\\s+", "-");
					
					token.append( ((token.length()>0)?AMPS:"") + CITY + EQ + city );
						
					token.append( AMPS + SEGMENT_KEY + EQ + place.getLocation().getSegmentedKey() );					
				}
			}
			else if(place.getQueryLocationIndex() != null && place.getQueryLocationIndex().equals(QueryLocationIndex.geo))
			{
				if(place.getLocation().getLatitude()!=0 && place.getLocation().getLongitude()!=0)
				{
					token.append( ((token.length()>0)?AMPS:"") + LATITUDE + EQ + place.getLocation().getLatitude() );
					token.append( AMPS + LONGITUDE + EQ + place.getLocation().getLongitude() );					
				}
			}			
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		@Override
		public SearchPlace getPlace(String token) {
			
			GWT.log("getPlace: "+ token);

			/*
			event/venue/performer=query & from=date & to=data & city=city-name & state-country= seg-key 
			event/venue/performer=query & from=date & to=data & lat=0987 & lon=-01888
			event/venue/performer=query & lat=0987 & lon=-01888
			event/venue/performer=query & from=date & to=data
			event/venue/performer=query 
			from=date & to=data & lat=0987 & lon=-01888
			from=date & to=data & city=city-name & state-country= seg-key 
			lat=0987 & lon=-01888
			from=date & to=data
			*/
			
			SearchPlace place = new SearchPlace( );
			
			Map<String, String> parameters = this.parse( token );
			
			IndexKey queryFieldName = this.getIndexType( parameters );			
			
			if(queryFieldName!=null)
			{
				String query = parameters.get( queryFieldName.name() );
				
				place.setQueryFieldName(queryFieldName);
				place.setQuery(query);
			}
			
			this.setDate(place,parameters);
			
			this.setLocation(place,parameters);
			
			return place;
		}
		
		private void setDate(SearchPlace searchPlace, Map<String, String> parameters)
		{
			String from = parameters.get(FROM);
			//Date from_ = null;
			if(from!=null){
				try{
					//from_ = DateTimeFormat.getFormat("d-MMM-yy").parse(from);
					//reset time boundaries for query.From time must be 0
					//CalendarUtil.resetTime(from_);
					searchPlace.setFrom(from);
				}
				catch(IllegalArgumentException e){
					GWT.log(e.getMessage());
				}
			}
			
			String to = parameters.get(TO);
			//Date to_ = null;
			if(to!=null){
				try{
					//to_ = DateTimeFormat.getFormat("d-MMM-yy").parse(to);
					//set time boundaries for query.To time must be 23:59:59
					//CalendarUtil.resetTime(to_);
					//to_.setTime(to_.getTime()+Constants.HOURS_24_MINUS_1_MS);
					searchPlace.setTo(to);
				}
				catch(IllegalArgumentException e){
					GWT.log(e.getMessage());
				}
			}
		}
		
		private void setLocation(SearchPlace place, Map<String, String> parameters)
		{
			String segmentKey = parameters.get(SEGMENT_KEY);
			String city = parameters.get(CITY);
			String latitude = parameters.get(LATITUDE);
			String longitude = parameters.get(LONGITUDE);
			
			if(segmentKey!=null && city!=null)
			{
				place.setQueryLocationIndex(QueryLocationIndex.city);
				
				place.setLocation(this.getSetUserLocation(segmentKey,city,0d,0d));
				return;//city or geo
			}
			else if(latitude!=null && longitude!=null)
			{
				place.setQueryLocationIndex(QueryLocationIndex.geo);
				
				place.setLocation(this.getSetUserLocation(segmentKey,city,Double.valueOf(latitude),Double.valueOf(longitude)));
				return;//city or geo
			}
			else
			{
				place.setQueryLocationIndex(QueryLocationIndex.ignore);
			}
		}
		
		private UserLocation getSetUserLocation(String segmentKey,String city,double latitude,double longitude)
		{
			UserLocation userLocation = new UserLocation();
			userLocation.setSegmentedKey(segmentKey);
			userLocation.setCity(city);
			userLocation.setLatitude(latitude);
			userLocation.setLongitude(longitude);
			return userLocation;
		}
		
		private IndexKey getIndexType(Map<String, String> parameters)
		{
			if(parameters.containsKey(EVENT)){
				return IndexKey.event;
			}
			
			if(parameters.containsKey(PERFORMER)){
				return IndexKey.performer;
			}
			
			if(parameters.containsKey(VENUE)){
				return IndexKey.venue;
			}
						
			return null;
		}
		
		private final Map<String, String> parse(String token) {

		    Map<String, String> map = new HashMap<String, String>();
		    
		    if (token != null) 
		    {
		        String[] parameters = token.split(AMPS);
		        
		        for (String parameter : parameters) 
		        {
		            String[] keyValue = parameter.split(EQ);
		            
		            if (keyValue.length > 1) 
		            {
		                map.put(keyValue[0], keyValue[1]);
		            }
		        }
		    }
		    
		    return map;
		}
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public QueryLocationIndex getQueryLocationIndex() {
		return howToSearchLocation;
	}
	public void setQueryLocationIndex(QueryLocationIndex queryLocationIndex) {
		this.howToSearchLocation = queryLocationIndex;
	}
	public boolean isClearSearchView() {
		return clearSearchView;
	}
	public void setClearSearchView(boolean clearSearchView) {
		this.clearSearchView = clearSearchView;
	}

	public IndexKey getQueryFieldName() {
		return queryFieldName;
	}
	public void setQueryFieldName(IndexKey indexKey) {
		this.queryFieldName = indexKey;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}


	public String getTo() {
		return to;
	}


	public void setTo(String to) {
		this.to = to;
	}


	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

}
