package com.ctnl;

import com.ctnc.shared.IndexKey;
import com.ctnu.io.UtilsIO;

public class Resources {

	public final static String pageCacheFolderName = "page-cache";
	
	public final static String collectFolderName = "collect";
	public final static String collectIndexFolderName = "collect-index";
	
	public final static String mergeFolderName = "merge";
	public final static String mergeIndexFolderName = "merge-index";
	
	public final static String suggesterByEventNameFolder = "suggester-event-name";
	public final static String suggesterByEventNameFile = "suggester-event-name";
	public final static String suggesterByEventNameIndexFolder = "suggester-index-event-name";
	public final static String suggesterByEventNameIndexSpellFolder = "suggester-index-event-name-spell";
	public final static String suggesterByEventNameSpellFile = "suggester-event-name-spell";
	
	public final static String suggesterByVenueNameFolder = "suggester-venue-name";
	public final static String suggesterByVenueNameFile = "suggester-venue-name";
	public final static String suggesterByVenueNameSpellFile = "suggester-venue-name-spell";
	public final static String suggesterByVenueNameIndexFolder = "suggester-index-venue-name";
	public final static String suggesterByVenueNameIndexSpellFolder = "suggester-index-venue-name-spell";

	public final static String suggesterByPerformerNameFolder = "suggester-performer-name";
	public final static String suggesterByPerformerNameFile = "suggester-performer-name";
	public final static String suggesterByPerformerNameSpellFile = "suggester-performer-name-spell";
	public final static String suggesterByPerformerNameIndexFolder = "suggester-index-performer-name";
	public final static String suggesterByPerformerNameIndexSpellFolder = "suggester-index-performer-name-spell";

	public final static String suggesterByLocationNameFolder = "suggester-location-name";
	public final static String suggesterByLocationNameFile = "suggester-location-name";
	public final static String suggesterByLocationNameSpellFile = "suggester-location-name-spell";
	public final static String suggesterByLocationNameIndexFolder = "suggester-index-location-name";
	public final static String suggesterByLocationNameIndexSpellFolder = "suggester-index-location-name-spell";

	public final static String identicalVenuesNameFolder = "identical-venues-name";
	public final static String identicalVenuesNameNameFile = "identical-venues-name";
	
	public final static String identicalLocationsNameFolder = "identical-locations-name";
	public final static String identicalLocationsNameFile = "identical-locations-name";
	
	public final static String geocodingFolder = "geocoding";

	public final static String locationMappingRulesFolder = "location-mapping-rules";
	public final static String locationMappingRulesFile = "location-mapping-rules";
	
	public final static String mergeExtraRulesFolder = "merge-extra-rules";
	public final static String mergeExtraRulesFile = "merge-extra-rules";
	
	
	
	@SuppressWarnings("incomplete-switch")
	public static String getSuggesterIndexSpellFolder(IndexKey indexKey, String baseFolderPath) {
		
		switch (indexKey) 
		{
			case event:
				return baseFolderPath + 
						UtilsIO.getSeparator() +
						Resources.suggesterByEventNameIndexSpellFolder;
			case venue:
				return baseFolderPath + 
						UtilsIO.getSeparator() +
						Resources.suggesterByVenueNameIndexSpellFolder;
			case performer:
				return baseFolderPath + 
						UtilsIO.getSeparator() +
						Resources.suggesterByPerformerNameIndexSpellFolder;
			case location:
				return baseFolderPath + 
						UtilsIO.getSeparator() +
						Resources.suggesterByLocationNameIndexSpellFolder;
		}
		
		return null;
	}
	
	
	
	
	public static String getSuggesterByPerformerNameIndexFolder(String baseFolderPath) {
		return baseFolderPath + 
				UtilsIO.getSeparator() +
				Resources.suggesterByPerformerNameIndexFolder;
	}

	public static String getSuggesterByLocationNameIndexFolder(String baseFolderPath) {
		return baseFolderPath + 
				UtilsIO.getSeparator() +
				Resources.suggesterByLocationNameIndexFolder;
	}
	
	public static String getSuggesterByVenueNameIndexFolder(String baseFolderPath) {
		return baseFolderPath + 
				UtilsIO.getSeparator() +
				Resources.suggesterByVenueNameIndexFolder;
	}
	
	public static String getSuggesterIndexFolder(String baseFolderPath)
	{
			return baseFolderPath + 
					UtilsIO.getSeparator() +
					Resources.suggesterByEventNameIndexFolder;
	}
	
	
	
	public static String getCollectIndexData(String baseFolderPath)
	{
			return baseFolderPath + 
					UtilsIO.getSeparator() +
					Resources.collectIndexFolderName;
	}
	
	public static String getMergeIndexData(String baseFolderPath)
	{
			return baseFolderPath + 
					UtilsIO.getSeparator() +
					Resources.mergeIndexFolderName;
	 }
	
	
	
	 public static String getloadLocationMappingRulesData(String baseFolderPath)
	 {
			return baseFolderPath + 
					UtilsIO.getSeparator() +
					Resources.locationMappingRulesFolder + 
					UtilsIO.getSeparator() + 
					Resources.locationMappingRulesFile;
	 }
	 public static String getBasePathToGeocodingData(String baseFolderPath){
				return baseFolderPath+UtilsIO.getSeparator()+Resources.geocodingFolder;
	 }
		    
	
	@SuppressWarnings("incomplete-switch")
	public static String getPathToSuggesterSpellData(IndexKey indexKey, String baseFolderPath){
			
		 switch (indexKey) 
		 {
				case event:
					return baseFolderPath + 
							UtilsIO.getSeparator() +
							Resources.suggesterByEventNameFolder + 
							UtilsIO.getSeparator() + 
							Resources.suggesterByEventNameSpellFile;
				case venue:
					return baseFolderPath + 
							UtilsIO.getSeparator() +
							Resources.suggesterByVenueNameFolder + 
							UtilsIO.getSeparator() + 
							Resources.suggesterByVenueNameSpellFile;
				case performer:
					return baseFolderPath + 
							UtilsIO.getSeparator() +
							Resources.suggesterByPerformerNameFolder + 
							UtilsIO.getSeparator() + 
							Resources.suggesterByPerformerNameSpellFile;
				case location:
					return baseFolderPath + 
							UtilsIO.getSeparator() +
							Resources.suggesterByLocationNameFolder + 
							UtilsIO.getSeparator() + 
							Resources.suggesterByLocationNameSpellFile;
			}
		 
		 	return null;
	    }
	    
	   
	    
	    
	    
	    
	 
	    
	    
	    
	    
	    
	    public static String getPathToSuggesterData(IndexKey indexKey, String baseFolderPath){
	    	
	    	switch (indexKey) {
				case event:
					return baseFolderPath + 
							UtilsIO.getSeparator() +
							Resources.suggesterByEventNameFolder+
						    UtilsIO.getSeparator() + 
						    Resources.suggesterByEventNameFile;
				case location:
					return baseFolderPath + 
							UtilsIO.getSeparator() +
							Resources.suggesterByLocationNameFolder+
						    UtilsIO.getSeparator() + 
						    Resources.suggesterByLocationNameFile;
				case performer:
					return baseFolderPath + 
							UtilsIO.getSeparator() +
							Resources.suggesterByPerformerNameFolder+
						    UtilsIO.getSeparator() + 
						    Resources.suggesterByPerformerNameFile;
				case venue:
					return baseFolderPath + 
							UtilsIO.getSeparator() +
							Resources.suggesterByVenueNameFolder+
						    UtilsIO.getSeparator() + 
						    Resources.suggesterByVenueNameFile;
				}
	    		return null;
	    }
	    
	    public static String getPathToIdenticalVenuesNameData(String baseFolderPath){
			return baseFolderPath + 
					UtilsIO.getSeparator() +
				   Resources.identicalVenuesNameFolder + 
				   UtilsIO.getSeparator() + 
				   Resources.identicalVenuesNameNameFile;
	    }
	    
	    public static String getPathToIdenticalLocationsData(String baseFolderPath){
			return baseFolderPath + UtilsIO.getSeparator() +
					Resources.identicalLocationsNameFolder + UtilsIO.getSeparator() + 
					Resources.identicalLocationsNameFile;
	    }
	    
	    public static String getBasePathToCollectData(String baseFolderPath){
			return baseFolderPath + UtilsIO.getSeparator()+Resources.collectFolderName;
	    }
	    public static String getBasePathToMergeData(String baseFolderPath){
			return baseFolderPath+UtilsIO.getSeparator()+Resources.mergeFolderName;
	    }
	    public static String getBasePathToPageCacheData(String baseFolderPath){
			return baseFolderPath+UtilsIO.getSeparator()+Resources.pageCacheFolderName;
	    }

		



		
}
