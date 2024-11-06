package com.ctnl.load.fs;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import com.ctnc.Event;
import com.ctnc.SuggesterDto;
import com.ctnc.shared.IndexKey;
import com.ctnc.shared.Location;
import com.ctnl.Resources;
import com.ctns.collect.index.build.LocationMappingRule;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.maps.model.GeocodingResult;

public class LoadService 
{
	private Logger logger = Logger.getLogger(LoadService.class);

	@Autowired
	private LoadProperties properties;
	
	public LoadProperties getLoadFSProperties()
	{
		return this.properties;
	}
	
    public LoadService(){}

    public Map<String,Location> getShortGeocodingData()throws LoadException {

    	Map<String,GeocodingResult[]> results = this.getGeocodingData();
    	Map<String,Location> shortResults = new HashMap<String,Location>(results.size());

    	for(Map.Entry<String,GeocodingResult[]> entry : results.entrySet())
    	{
    		Location location = new Location();
    		
    		try
    		{
        		location.setLatitude(entry.getValue()[0].geometry.location.lat);
        		location.setLongitude(entry.getValue()[0].geometry.location.lng);
    		}
    		catch(java.lang.ArrayIndexOutOfBoundsException e)
    		{
    			logger.error("Can't set short geocoding data for:"+entry.getKey());
    		}
    		
    		shortResults.put(entry.getKey(), location);
    	}
 
		return shortResults;
	}
    
    public Map<String,GeocodingResult[]> getGeocodingData()throws LoadException {

		String basePathToGeocodingData = Resources.getBasePathToGeocodingData(this.properties.getBaseFolderPath());
		
		Map<String,GeocodingResult[]> results = new HashMap<String,GeocodingResult[]>(); 
		
    	logger.info("path : "+basePathToGeocodingData);
    	
    	File baseMergeFolder = new File(basePathToGeocodingData);
		
		//us_tx,ca_so,uk etc...
		File[] listOfSegments = baseMergeFolder.listFiles();

		for (File segment : listOfSegments) 
		{
			FileReader fileReader = null;
			
			try {
				fileReader = new FileReader(segment);
			} catch (FileNotFoundException e) {
				throw new LoadException(e);
			}
			
			Gson gson = new GsonBuilder().create();
			
			GeocodingResult[] geo = gson.fromJson(fileReader,GeocodingResult[].class);
			
			results.put(segment.getName(), geo);
			
			try {
				fileReader.close();
			} catch (IOException e) {logger.error(e);}
		}
		
		return results;
	}

    
	public Map<String, List<Event>> eventsFromMerge()throws LoadException {

		String basePathToMergeData = Resources.getBasePathToMergeData(this.properties.getBaseFolderPath());
    	
    	logger.info("path : "+basePathToMergeData);
    	
    	if(Files.notExists(Paths.get(basePathToMergeData),LinkOption.NOFOLLOW_LINKS)){
    		throw new LoadException("Expect to input directory from Merge as : "+basePathToMergeData);
    	}
    	
    	Map<String, List<Event>> eventsFromMerge = new HashMap<String, List<Event>>();

    	File baseMergeFolder = new File(basePathToMergeData);
		
		//us:tx,ca:so,uk etc...
		File[] listOfSegments = baseMergeFolder.listFiles();

		for (File segment : listOfSegments) 
		{
			FileReader fileReader = null;
			
			try {
				fileReader = new FileReader(segment);
			} catch (FileNotFoundException e) {
				throw new LoadException(e);
			}
			
			Gson gson = new GsonBuilder().create();
			
			Event[] events = gson.fromJson(fileReader,Event[].class);
			
			eventsFromMerge.put(segment.getName(),Arrays.asList(events));
			
			try {
				fileReader.close();
			} catch (IOException e) {logger.error(e);}
		}
		
		return eventsFromMerge;
	}
    
	public List<Map<String, List<Event>>> eventsFromCollect()throws LoadException {
	
		String basePathToCollectData = Resources.getBasePathToCollectData(this.properties.getBaseFolderPath());
		
		if(Files.notExists(Paths.get(basePathToCollectData),LinkOption.NOFOLLOW_LINKS)){
    		throw new LoadException("Expect to input directory from Collect as : "+basePathToCollectData);
    	}
		
		List<Map<String, List<Event>>> eventsFromCollect = new ArrayList<Map<String,List<Event>>>();
		
		//Collect_04-06-2015
		File baseCollectFolder = new File(basePathToCollectData);
		
		FilenameFilter filter = new FilenameFilter() 
		{
			public boolean accept(File directory, String fileName) 
			{
	            return (!fileName.contains("_"));
	        }
	    };
	    
		//TC , TN etc ...
		File[] listOfSources = baseCollectFolder.listFiles(filter);

		for (File source : listOfSources) 
		{    
			File[] keys = source.listFiles();
		
			Map<String, List<Event>> eventsFromSource = new HashMap<String, List<Event>>();
			
			for (File key : keys) 
			{
				FileReader fileReader = null;
				
				try {
					fileReader = new FileReader(key);
				} catch (FileNotFoundException e) {
					throw new LoadException(e);
				}
				
				Gson gson = new GsonBuilder().create();
				
				Event[] events = gson.fromJson(fileReader,Event[].class);
				
				eventsFromSource.put(key.getName(),Arrays.asList(events));
				
				try {
					fileReader.close();
				} catch (IOException e) {logger.error(e);}
			}
			
			eventsFromCollect.add(eventsFromSource);
		}
		
		return eventsFromCollect;	
	}
	
	public List<SuggesterDto> getSuggesters(IndexKey indexKey)throws LoadException {
		
		String path = Resources.getPathToSuggesterData(indexKey,this.properties.getBaseFolderPath());
	
		if(Files.notExists(Paths.get(path),LinkOption.NOFOLLOW_LINKS)){
    		throw new LoadException("Expect to input file from Suggester as : "+path);
    	}
								
		Gson gson = new GsonBuilder().create();
				
		FileReader fileReader = null;
		
		try {
			fileReader = new FileReader(path);
		} catch (FileNotFoundException e) {
			throw new LoadException(e);
		}
		
		SuggesterDto[] suggesters = gson.fromJson(fileReader,SuggesterDto[].class);
		
		try {
			fileReader.close();
		} catch (IOException e) {logger.error(e);}
		
		return Arrays.asList(suggesters);	
	}

	public Map<String,List<String>> loadIdenticalVenues()throws LoadException {
		
		String path = Resources.getPathToIdenticalVenuesNameData(this.properties.getBaseFolderPath());
		
		if(Files.notExists(Paths.get(path),LinkOption.NOFOLLOW_LINKS)){
    		throw new LoadException("Expect to input file from identical venues as : "+path);
    	}
						
		Gson gson = new GsonBuilder().create();
				
		FileReader fileReader = null;
		
		try {
			fileReader = new FileReader(path);
		} catch (FileNotFoundException e) {
			throw new LoadException(e);
		}
		
		java.lang.reflect.Type type = new TypeToken<Map<String, ArrayList<String>>>(){}.getType();
		
		@SuppressWarnings("unchecked")
		Map<String,List<String>> result = (Map<String,List<String>>)gson.fromJson(fileReader,type);
		
		try {
			fileReader.close();
		} catch (IOException e) {logger.error(e);}
		
		return result;	
	}
	
	public Map<String,List<LocationMappingRule>> loadLocationMappingRules() throws LoadException {
		
		String path = Resources.getloadLocationMappingRulesData(this.properties.getBaseFolderPath());
		
		if(Files.notExists(Paths.get(path),LinkOption.NOFOLLOW_LINKS)){
    		throw new LoadException("Expect to input file from location mapping rules as : "+path);
    	}
								
		Gson gson = new GsonBuilder().create();
				
		FileReader fileReader = null;
		
		try {
			fileReader = new FileReader(path);
		} catch (FileNotFoundException e) {
			throw new LoadException(e);
		}
	
		java.lang.reflect.Type type = new TypeToken<Map<String, ArrayList<LocationMappingRule>>>(){}.getType();
		
		@SuppressWarnings("unchecked")
		Map<String,List<LocationMappingRule>> result = (Map<String,List<LocationMappingRule>>)gson.fromJson(fileReader,type);
		
		try {
			fileReader.close();
		} catch (IOException e) {logger.error(e);}
		
		return result;		
	}
	
	public Map<String,Set<String>> loadIdenticalLocationsNames()throws LoadException {
		
		String path = Resources.getPathToIdenticalLocationsData(this.properties.getBaseFolderPath());
		
		if(Files.notExists(Paths.get(path),LinkOption.NOFOLLOW_LINKS)){
    		throw new LoadException("Expect to input file from identical locations as : "+path);
    	}
						
		Gson gson = new GsonBuilder().create();
				
		FileReader fileReader = null;
		
		try {
			fileReader = new FileReader(path);
		} catch (FileNotFoundException e) {
			throw new LoadException(e);
		}
		
		java.lang.reflect.Type type = new TypeToken<Map<String, Set<String>>>(){}.getType();
		
		@SuppressWarnings("unchecked")
		Map<String,Set<String>> result = (Map<String,Set<String>>)gson.fromJson(fileReader,type);
		
		try {
			fileReader.close();
		} catch (IOException e) {logger.error(e);}
		
		return result;	
	}

	public Map<String, String> getPageCacheMetadata() {
		
		String path = Resources.getBasePathToPageCacheData(this.properties.getBaseFolderPath());
		
		if(Files.notExists(Paths.get(path),LinkOption.NOFOLLOW_LINKS)){
    		return null;
    	}
		
		File pageCacheFolder = new File(path);
		
		File[] pageCacheFiles = pageCacheFolder.listFiles();

		Map<String,String> pageCacheMetadata = new ConcurrentHashMap<String,String>();

		for (File pageCacheFile : pageCacheFiles) 
		{
			pageCacheMetadata.put( pageCacheFile.getName() , pageCacheFile.getAbsolutePath());
			
//			FileReader fileReader = null;
//			
//			try 
//			{
//				fileReader = new FileReader(pageCacheFile);
//			} 
//			catch (FileNotFoundException e) 
//			{
//				
//			}
		}
		
		return pageCacheMetadata;
	}
	
	public File[] getPageCacheFiles() {
		
		String path = Resources.getBasePathToPageCacheData(this.properties.getBaseFolderPath());
		
		if(Files.notExists(Paths.get(path),LinkOption.NOFOLLOW_LINKS)){
    		return null;
    	}
		
		File pageCacheFolder = new File(path);
		
		File[] pageCacheFiles = pageCacheFolder.listFiles();

		return pageCacheFiles;
	}
	
	
	
	
	
	
	
	

//	public Set<String> loadLocationsNames()throws LoadFSException {
//		
//		String path = this.getPathToLocationsData();
//		
//		if(Files.notExists(Paths.get(path),LinkOption.NOFOLLOW_LINKS)){
//    		throw new LoadFSException("Expect to input file from locations as : "+path);
//    	}
//						
//		Gson gson = new GsonBuilder().create();
//				
//		FileReader fileReader = null;
//		
//		try {
//			fileReader = new FileReader(path);
//		} catch (FileNotFoundException e) {
//			throw new LoadFSException(e);
//		}
//		
//		java.lang.reflect.Type type = new TypeToken<Set<String>>(){}.getType();
//		
//		@SuppressWarnings("unchecked")
//		Set<String> result = (Set<String>)gson.fromJson(fileReader,type);
//		
//		try {
//			fileReader.close();
//		} catch (IOException e) {logger.error(e);}
//		
//		return result;	
//	}
	
}
