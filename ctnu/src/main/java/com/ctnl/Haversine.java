package com.ctnl;

public class Haversine {

	    public static final double R = 6372.8; // In kilometers
	    
	    public static double haversine(double lat1, double lon1, double lat2, double lon2) 
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
	    
	        System.out.println(haversine(34.0522342, -118.2436849, 34.0928092, -118.3286614));
	    }
	}
