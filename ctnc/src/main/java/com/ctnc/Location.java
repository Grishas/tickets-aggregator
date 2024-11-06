package com.ctnc;

public class Location {

	private Country country;
	
	private State state;
	
	private City city;

	private GeocodingDataLevel geocodingDataLevel;
	private double latitude=0;
	private double longitude=0;
	
	public GeocodingDataLevel getGeocodingDataLevel() {
		return geocodingDataLevel;
	}

	public void setGeocodingDataLevel(GeocodingDataLevel geocodingDataLevel) {
		this.geocodingDataLevel = geocodingDataLevel;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public City getCity() {
		return city;
	}

	public void setCity(City city) {
		this.city = city;
	}

	@Override
	public String toString() {
		return "Location [country=" + country + ", state=" + state + ", city="
				+ city + "]";
	}
}
