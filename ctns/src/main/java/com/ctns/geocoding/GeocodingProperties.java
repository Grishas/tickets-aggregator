package com.ctns.geocoding;

import java.util.Map;

import com.ctnc.GeocodingDataLevel;
import com.ctnc.shared.Location;
import com.google.maps.GeoApiContext;

public class GeocodingProperties {
		
	private GeocodingDataLevel geocodingDataLevel;
	
	private GeoApiContext geoApiContext;
		
	private Map<String,Location> locations;

	public Map<String, Location> getLocations() {
		return locations;
	}

	public void setLocations(Map<String, Location> locations) {
		this.locations = locations;
	}

	public GeoApiContext getGeoApiContext() {
		return geoApiContext;
	}

	public void setGeoApiContext(GeoApiContext geoApiContext) {
		this.geoApiContext = geoApiContext;
	}

	public GeocodingDataLevel getGeocodingDataLevel() {
		return geocodingDataLevel;
	}

	public void setGeocodingDataLevel(GeocodingDataLevel geocodingDataLevel) {
		this.geocodingDataLevel = geocodingDataLevel;
	}
	
	
}
