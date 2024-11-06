package com.ctns.geocoding;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.ctnc.Event;
import com.ctnc.shared.Location;
import com.ctns.query.QueryBuilderException;
import com.ctns.query.Utils;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;

public class GeocodingService {

	private Logger logger = Logger.getLogger(GeocodingService.class);

	@Autowired 
	private GeocodingProperties properties;
		
	private static final int GEOCODING_API_FREE_QUOTA = 2450;
	
    public GeocodingService(){}
    

	public Location getLocation(String segmentedKey,String city) throws GeocodingException
	{
		try 
		{
			String key = Utils.getLocationKey(segmentedKey, city);
			
			Location location = this.getLocationByKey(key);
			
			if(location==null)
			{
				//go google api
				location = this.tryToFindLocation(segmentedKey,city);
				
				this.properties.getLocations().put(key, location);
			}
			
			return location;
			
		} 
		catch (QueryBuilderException e) 
		{
			throw new GeocodingException(e);
		}
	}
	
	//only in ram
	private Location tryToFindLocation(String segmentedKey,String city) throws GeocodingException
	{
		
			GeocodingResult geocodingResult = 
					this.getGeocodingDataLocation(city+","+segmentedKey);
			
			Location location = new Location();
			location.setLatitude(geocodingResult.geometry.location.lat);
			location.setLongitude(geocodingResult.geometry.location.lng);
			location.setSegmentedKey(segmentedKey);
			location.setCity(city);
			return location;
		
	}
	
    //get keys from utils
    public Location getLocationByKey(String locationKey)
    {
    	return this.properties.getLocations().get(locationKey);
    }
    
    public Map<String,GeocodingResult[]> build(
    		List<Map<String, List<Event>>> eventsFromCollect, 
			Map<String,GeocodingResult[]> geocodingData) 
					throws GeocodingException{
				
		if(geocodingData==null)
		{
			geocodingData = new HashMap<String,GeocodingResult[]>();
		}
		
		for(Map<String, List<Event>> source : eventsFromCollect)
		{
			for(Map.Entry<String, List<Event>> entry : source.entrySet())
			{
				logger.info("---"+entry.getKey()+"---");
				
				for(Event event : entry.getValue())
				{
					//in case if quota limitation issue 
					if(event.getLocation().getLatitude()!=0){
						continue;
					}
					
					for(String city : event.getLocation().getCity().getCityNames())
					{
						//getVenueKey("us_ny", "Las Vegas","The AXIS at Planet Hollywood");
						String key = null;
						try {
							key = Utils.getLocationKey(event.getSegmentKey(),city);
						} catch (QueryBuilderException e) {
							throw new GeocodingException(e);
						}
						
						if(geocodingData.containsKey(key)==true)
						{
							this.setGeocodingDataToEvent(key,event,geocodingData);
							
							//set first and out.is good when we have few cities like LA and Holly...?
							break;
						}
						else
						{	
							String state = event.getLocation().getState().getAbbreviation();
							if(state==null){
								state = "";
							}
							else{
								state = state+",";
							}
							
							String query = city+","+state + event.getLocation().getCountry().getAbbreviation();
							
							try {
								boolean stop = this.getSetGeocodingData( key, query , geocodingData , this.properties.getGeoApiContext() );
								
								if(stop==true)
								{
									return geocodingData;
								}
								
							} catch (Exception e) {
								throw new GeocodingException(e);
							}
							
							this.setGeocodingDataToEvent(key,event, geocodingData);
						}					
					}			
				}
			}
		}
	
		return geocodingData;
    }	
    
	public Map<String,GeocodingResult[]> build(
			Map<String, List<Event>> events, 
			Map<String,GeocodingResult[]> geocodingData) 
					throws GeocodingException{
				
		if(geocodingData==null){
			geocodingData = new HashMap<String,GeocodingResult[]>();
		}
		
		for(Map.Entry<String, List<Event>> entry : events.entrySet())
		{
			logger.info("---"+entry.getKey()+"---");
			
			for(Event event : entry.getValue())
			{
				//in case if quota limitation issue 
				if(event.getLocation().getLatitude()!=0){
					continue;
				}
				
				for(String city : event.getLocation().getCity().getCityNames())
				{
					//getVenueKey("us_ny", "Las Vegas","The AXIS at Planet Hollywood");
					String key = null;
					try {
						key = Utils.getLocationKey(event.getSegmentKey(),city);
					} catch (QueryBuilderException e) {
						throw new GeocodingException(e);
					}
					
					if(geocodingData.containsKey(key)==true)
					{
						this.setGeocodingDataToEvent(key,event,geocodingData);
						
						//set first and out.is good when we have few cities like LA and Holly...?
						break;
					}
					else
					{	
						String state = event.getLocation().getState().getAbbreviation();
						if(state==null){
							state = "";
						}
						else{
							state = state+",";
						}
						
						String query = city+","+state + event.getLocation().getCountry().getAbbreviation();
						
						try {
							boolean stop = this.getSetGeocodingData( key, query , geocodingData , this.properties.getGeoApiContext() );
							
							if(stop==true)
							{
								return geocodingData;
							}
							
						} catch (Exception e) {
							throw new GeocodingException(e);
						}
						
						this.setGeocodingDataToEvent(key,event, geocodingData);
					}					
				}			
			}
		}
		
		return geocodingData;
    }	
	
	private void setGeocodingDataToEvent(String key,Event event,Map<String,GeocodingResult[]> geocodingData)
	{
		double lat = 0;
		double lng = 0;
		try
		{
			lat = geocodingData.get(key)[0].geometry.location.lat;
			lng =  geocodingData.get(key)[0].geometry.location.lng;
		}
		catch(Exception e)
		{
			logger.error(event+" got zero values in latitude/longitude");
		}
		
		event.getLocation().setLatitude(lat);
		event.getLocation().setLongitude(lng);
	}
	
	private static int quotaCounter = 0; 
	private boolean getSetGeocodingData(String key,String query,Map<String,GeocodingResult[]> geocodingData,GeoApiContext geoApiContext) throws Exception
	{
		if(quotaCounter==GEOCODING_API_FREE_QUOTA)
		{
			logger.info("You finish quota for GEOCODING_API_FREE_QUOTA");
			return true;
		}
		
		GeocodingResult[] results = GeocodingApi.geocode(geoApiContext,query).await();
		
		geocodingData.put(key, results);
		
		quotaCounter++;
		
		return false;
	}
	
	public GeocodingResult getGeocodingDataLocation(String citySegmentKey) throws GeocodingException 
	{
		GeocodingResult[] results;
		try {
			results = GeocodingApi.geocode(this.properties.getGeoApiContext(),citySegmentKey).await();
		} catch (Exception e) {
			throw new GeocodingException(e);
		}
		
		if(results==null)
		{
			return null;
		}
		else
		{
			return results[0];
		}
	}
	
	public GeocodingResult[] reverseGeocode(double latitude, double longitude) throws GeocodingException
	{
		LatLng latLng = new LatLng( latitude , longitude );
		
		GeocodingResult[] results = null;

		try {
			results = GeocodingApi.reverseGeocode(this.properties.getGeoApiContext(), latLng).await();			
		} 
		catch (Exception e) {
			throw new GeocodingException(e);
		}
		
		return results;			
	}
	
	public static final double R = 6372.8; // In kilometers
	
	public double haversine(double lat1, double lon1, double lat2, double lon2) 
	{
		double dLat = Math.toRadians(lat2 - lat1);
	    double dLon = Math.toRadians(lon2 - lon1);
	        
	    lat1 = Math.toRadians(lat1);
	    lat2 = Math.toRadians(lat2);
	 
	    double a = Math.pow(Math.sin(dLat / 2),2) + Math.pow(Math.sin(dLon / 2),2) * Math.cos(lat1) * Math.cos(lat2);
	        
	    double c = 2 * Math.asin(Math.sqrt(a));
	        
	    return R * c;
	}
	    
	    public static void main(String[] args) 
	    {
	    	/*
	    	Maryland Heights
	    	 "location": {
	        "lat": 38.7131073,
	        "lng": -90.42984009999999
	        }
	      
     "longName": "Kansas City",

	    
	    "location": {
	        "lat": 39.0997265,
	        "lng": -94.5785667
	      },
	      
	      System.out.println(haversine(38.7131073, -90.42984009999999, 39.0997265, -94.5785667));

	      :-) km 361.6190077473623 google say 376km
	      ----
	      
	      Los Angeles",
	      
	      "location": {
     "lat": 34.0522342,
     "lng": -118.2436849
     
   },
   
   Hollywood
   
    "location": {
     "lat": 34.0928092,
     "lng": -118.3286614
   },
   
   by google 10 km - har ... 9.036667727405876
   
   
   
	    */
	    
	        //System.out.println(haversine(34.0522342, -118.2436849, 34.0928092, -118.3286614));
	    }
}
