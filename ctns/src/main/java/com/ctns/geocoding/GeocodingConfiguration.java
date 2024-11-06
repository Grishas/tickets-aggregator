package com.ctns.geocoding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;

import com.ctnc.GeocodingDataLevel;
import com.ctnl.load.fs.LoadException;
import com.ctnl.load.fs.LoadService;
import com.ctnu.UtilsException;
import com.ctnu.crypto.CipherUtils;
import com.google.maps.GeoApiContext;

@Configuration
@PropertySource("classpath:geocoding.configuration")
public class GeocodingConfiguration {
	
	@Autowired
	private Environment environment;	
	
	@Autowired
	private  CipherUtils cipherUtils;

	@Autowired
	private  LoadService loadService;
	
	@Bean
	@Scope("singleton")
	public GeocodingService getGeocodingService(){		
		return new GeocodingService();
	}
	
	@Bean
	@Scope("singleton")
	public GeocodingProperties getGeocodingProperties() throws GeocodingException{	
		
		GeocodingProperties properties = new GeocodingProperties();
		
		String apiKey = this.environment.getProperty("ctns.geocoding.api.key");
		try {
			apiKey = cipherUtils.decrypt(apiKey);
		} catch (UtilsException e) {
			throw new GeocodingException(e);
		}
		
		GeoApiContext geoApiContext = new GeoApiContext().setApiKey(apiKey);
		properties.setGeoApiContext(geoApiContext);
		
		properties.setGeocodingDataLevel(
				GeocodingDataLevel.valueOf(
						this.environment.getProperty("ctns.geocoding.data.level")));
		
		try {
			properties.setLocations(loadService.getShortGeocodingData());
		} catch (LoadException e) {
			throw new GeocodingException(e);
		}
	
		return properties;
	}	
}

