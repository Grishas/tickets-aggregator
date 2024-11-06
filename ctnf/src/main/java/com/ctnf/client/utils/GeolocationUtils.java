package com.ctnf.client.utils;
import com.ctnc.shared.UserLocation;
import com.ctnf.client.Factory;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.geolocation.client.Geolocation;
import com.google.gwt.geolocation.client.Position;
import com.google.gwt.geolocation.client.PositionError;

public class GeolocationUtils {
	
	public static void getSetGeolocation(final Factory factory)
	{
		if( Geolocation.isSupported() == false )
		{
			return;
		}
	
		Geolocation.getIfSupported().getCurrentPosition(new Callback<Position, PositionError>() 
		{
			@Override
			public void onSuccess(Position position) 
			{
				if(factory.getUserLocation()==null)
				{
					UserLocation userLocation = new UserLocation();
					userLocation.setLatitude(position.getCoordinates().getLatitude());
					userLocation.setLongitude(position.getCoordinates().getLongitude());
					factory.setUserLocation(userLocation);					
				}
				else
				{
					factory.getUserLocation().setLatitude(position.getCoordinates().getLatitude());
					factory.getUserLocation().setLongitude(position.getCoordinates().getLongitude());
				}
				
				GWT.log("User allow location tracking: "+factory.getUserLocation().toString());

				
				//geolocation.setAccuracy(position.getCoordinates().getAccuracy());
				//geolocation.setAltitude(position.getCoordinates().getAltitude());
				//geolocation.setAltitudeAccuracy(position.getCoordinates().getAltitudeAccuracy());
				//geolocation.setHeading(position.getCoordinates().getHeading());
				//geolocation.setSpeed(position.getCoordinates().getSpeed());
				//labelClientGeolocationInfo.setText(geolocation.toString());
			}
			
			@Override
			public void onFailure(PositionError reason) {}
		});	
	}
}
