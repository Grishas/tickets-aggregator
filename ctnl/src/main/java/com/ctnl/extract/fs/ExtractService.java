package com.ctnl.extract.fs;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import com.ctnc.Event;
import com.ctnc.SuggesterDto;
import com.ctnc.shared.IndexKey;
import com.ctnc.shared.Source;
import com.ctnl.Resources;
import com.ctns.collect.index.build.LocationMappingRule;
import com.ctnu.UtilsException;
import com.ctnu.io.UtilsIO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.model.GeocodingResult;

public class ExtractService
{
	private Logger logger = Logger.getLogger(ExtractService.class);

	@Autowired
	private ExtractProperties properties;
	
    public ExtractService(){}
    
    private <T> void toJson(Object data, String writeToFile) throws ExtractException
    {
    	Gson gson = null;
		
    	if(this.properties.isPrettyPrinting()){
    		gson = new GsonBuilder().setPrettyPrinting().create();
    	}
    	else{
    		gson = new GsonBuilder().create();
    	}
    	
    	Path pathToFile = Paths.get(writeToFile);
    	try {
			Files.createDirectories(pathToFile.getParent());
		} catch (IOException e1) {
			throw new ExtractException(e1);
		}
    	
    	try {
			Files.createFile(pathToFile);
		} catch (IOException e1) {
			throw new ExtractException(e1);
		}
    	
    	FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(writeToFile);
		} catch (IOException e) {
			throw new ExtractException(e);
		}
		
		gson.toJson(data,fileWriter);
		
		try{}
		finally
		{
			try {
				fileWriter.flush();
				fileWriter.close();
			} 
			catch (IOException error) {
				logger.error(error);
			}
		}
	}
    
    private Source getSourceName(Map<String, List<Event>> source){
		for(List<Event> segment : source.values()){
			for(Event event : segment){
				return event.getSource();
			}
		}
    	return null;
    }

    public void extractFromCollect(Map<String, List<Event>> events,Source source) throws ExtractException
    {
    	String basePath = Resources.getBasePathToCollectData(this.properties.getBaseFolderPath());
    	
    	logger.info("path to collector: "+basePath);
    	
    	if(source==null){
    		source = this.getSourceName(events);
    	}
    	
    	String pathToSource = basePath+UtilsIO.getSeparator()+source;

    	logger.info("path to source: "+pathToSource);

    	if(Files.exists(Paths.get(pathToSource),LinkOption.NOFOLLOW_LINKS))
		{
	   		File fileToRename =  new File(pathToSource);
	   		File newFile =  new File(pathToSource+"_"+System.currentTimeMillis());
	   		fileToRename.renameTo(newFile);
	   		
	   		logger.info("path to source "+pathToSource+" already exist.Rename it to : "+newFile.getName());
	   	}
    	
    	for(Map.Entry<String,List<Event>> segment : events.entrySet())
    	{
    		String pathToData = pathToSource+UtilsIO.getSeparator()+segment.getKey();
    		
	   		logger.info("writing: "+segment.getValue().size()+" events to: "+pathToData);
	   		
    		this.toJson(segment.getValue(),pathToData);
    	}
    }

	public void extractFromMerge(Map<String, List<Event>> eventsFromMerge)throws ExtractException {

		String basePath = Resources.getBasePathToMergeData(this.properties.getBaseFolderPath());
    	
    	logger.info("path : "+basePath);
    	
    	if(Files.exists(Paths.get(basePath),LinkOption.NOFOLLOW_LINKS)){
    		File fileToRename =  new File(basePath);
    		File newFile =  new File(basePath+"_"+System.currentTimeMillis());
    		fileToRename.renameTo(newFile);
    		
    		logger.info("path to "+basePath+" already exist.Rename it to : "+newFile.getName());
    	}
    	
    	for(Map.Entry<String, List<Event>> segment : eventsFromMerge.entrySet())
    	{
    		String pathToSegment = basePath+UtilsIO.getSeparator()+segment.getKey();
    		    		
    		if(this.properties.isExtractOnlyMergedEvents()==true)
    		{
        		List<Event> events = segment.getValue();
    			
        		this.toJson(this.getOnlyMergedEvents(events),pathToSegment);

        		continue;
    		}
    		
    		this.toJson(segment.getValue(),pathToSegment);
    	}
	}

	private List<Event> getOnlyMergedEvents(List<Event> events)
	{
		List<Event> onlyMergedEvents = new ArrayList<Event>(events.size()/2);

		for(Event event : events)
		{
			if(event.getMoreSources()!=null&&event.getMoreSources().size()>0)
			{
				onlyMergedEvents.add(event);
			}
		}
		
		return onlyMergedEvents;
	}
	
	
	
	
	public void extractFromGeocoding(Map<String,GeocodingResult[]> geocodingData)throws ExtractException {

		String basePath = Resources.getBasePathToGeocodingData(this.properties.getBaseFolderPath());
    	
    	logger.info("path : "+basePath);
    	
    	if(Files.exists(Paths.get(basePath),LinkOption.NOFOLLOW_LINKS)){
    		File fileToRename =  new File(basePath);
    		File newFile =  new File(basePath+"_"+System.currentTimeMillis());
    		fileToRename.renameTo(newFile);
    		
    		logger.info("path to "+basePath+" already exist.Rename it to : "+newFile.getName());
    	}
    	
    	for(Map.Entry<String,GeocodingResult[]> entry : geocodingData.entrySet())
    	{
    		String fullPath = basePath+UtilsIO.getSeparator()+entry.getKey();
    		
    		this.toJson(entry.getValue(),fullPath);
    	}
	}
	
	@SuppressWarnings("incomplete-switch")
	public void extractFromSuggested(List<SuggesterDto> suggesters,IndexKey indexKey)throws ExtractException {
		
		String path = Resources.getPathToSuggesterData(indexKey,this.properties.getBaseFolderPath());
		
    	logger.info("path : "+path);
    	
    	if(Files.exists(Paths.get(path),LinkOption.NOFOLLOW_LINKS))
    	{
    		File fileToRename =  new File(path);
    		File newFile =  new File(path+"_"+System.currentTimeMillis());
    		fileToRename.renameTo(newFile);
    		
    		logger.info("path to "+path+" already exist.Rename it to : "+newFile.getName());
    	}
    	
    	this.toJson(suggesters,path);
	}

	public void extractIdenticalVenues(Map<String, List<String>> venuesMapping) throws ExtractException 
	{
		String basePath = Resources.getPathToIdenticalVenuesNameData(this.properties.getBaseFolderPath());
    	
    	logger.info("path : "+basePath);
    	
    	if(Files.exists(Paths.get(basePath),LinkOption.NOFOLLOW_LINKS)){
    		File fileToRename =  new File(basePath);
    		File newFile =  new File(basePath+"_"+System.currentTimeMillis());
    		fileToRename.renameTo(newFile);
    		
    		logger.info("path to "+basePath+" already exist.Rename it to : "+newFile.getName());
    	}

    	this.toJson(venuesMapping,basePath);
	}
	
	public void extractLocationMappingRules(Map<String,List<LocationMappingRule>> rules) 
	{
		String basePath = Resources.getPathToIdenticalVenuesNameData(this.properties.getBaseFolderPath());
    	
    	logger.info("path : "+basePath);
    	
    	if(Files.exists(Paths.get(basePath),LinkOption.NOFOLLOW_LINKS)){
    		File fileToRename =  new File(basePath);
    		File newFile =  new File(basePath+"_"+System.currentTimeMillis());
    		fileToRename.renameTo(newFile);
    		
    		logger.info("path to "+basePath+" already exist.Rename it to : "+newFile.getName());
    	}

    	try {
			this.toJson(rules,basePath);
		} catch (ExtractException e) {
			e.printStackTrace();
		}
	}

	public void extractIdenticalLocations(Map<String, Set<String>> mergeLocation) throws ExtractException 
	{
		String basePath = Resources.getPathToIdenticalLocationsData(this.properties.getBaseFolderPath());
    	
    	logger.info("path : "+basePath);
    	
    	if(Files.exists(Paths.get(basePath),LinkOption.NOFOLLOW_LINKS)){
    		File fileToRename =  new File(basePath);
    		File newFile =  new File(basePath+"_"+System.currentTimeMillis());
    		fileToRename.renameTo(newFile);
    		
    		logger.info("path to "+basePath+" already exist.Rename it to : "+newFile.getName());
    	}

    	this.toJson(mergeLocation,basePath);
	}

	public void extractFromSuggestersSpell( String data,IndexKey indexKey) throws ExtractException {

		String path = Resources.getPathToSuggesterSpellData(indexKey,this.properties.getBaseFolderPath());
		
    	logger.info("path : "+path);
    	
    	if(Files.exists(Paths.get(path),LinkOption.NOFOLLOW_LINKS))
    	{
    		File fileToRename =  new File(path);
    		File newFile =  new File(path+"_"+System.currentTimeMillis());
    		fileToRename.renameTo(newFile);
    		
    		logger.info("path to "+path+" already exist.Rename it to : "+newFile.getName());
    	}
		
		try {
			UtilsIO.writeToFile(path, data);
		} catch (UtilsException e) {
			throw new ExtractException(e);
		}
	}

//	public void extractLocations(Set<String> locations) throws ExtractFSException {
//
//		String basePath = this.getPathToLocationsData();
//    	
//    	logger.info("path : "+basePath);
//    	
//    	if(Files.exists(Paths.get(basePath),LinkOption.NOFOLLOW_LINKS)){
//    		File fileToRename =  new File(basePath);
//    		File newFile =  new File(basePath+"_"+System.currentTimeMillis());
//    		fileToRename.renameTo(newFile);
//    		
//    		logger.info("path to "+basePath+" already exist.Rename it to : "+newFile.getName());
//    	}
//
//    	this.toJson(locations,basePath);
//	}
	
}
