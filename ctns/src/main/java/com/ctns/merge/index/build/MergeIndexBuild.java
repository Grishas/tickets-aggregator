package com.ctns.merge.index.build;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoubleField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Autowired;

import com.ctnc.Constants;
import com.ctnc.Event;
import com.ctnc.shared.Performer;
import com.ctnl.Resources;
import com.ctns.query.QueryBuilderException;
import com.ctnu.date.DateUtils;

public class MergeIndexBuild {

	private Logger logger = Logger.getLogger(MergeIndexBuild.class);

	@Autowired 
	private MergeIndexBuildProperties properties;

	@SuppressWarnings("resource")
	public void build(Map<String, List<Event>> events, 
			Map<String, List<String>> identicalVenues, Map<String, Set<String>> identicalLocations) throws MergeIndexBuildException{

		boolean create = true;
		
		Path pathToFile = Paths.get(Resources.getMergeIndexData(this.properties.getBaseFolderPath()));
				
		if( ! Files.exists(pathToFile)){
			try {
				Files.createDirectory(pathToFile);
			} catch (IOException e1) {
				throw new MergeIndexBuildException(e1);
			}
		}
		
		Directory directory = null;
		try {
			directory = FSDirectory.open( Paths.get(Resources.getMergeIndexData(this.properties.getBaseFolderPath())) );
		} catch (IOException e) {
			throw new MergeIndexBuildException(e);
		}
		
		Analyzer analyzer = new EnglishAnalyzer();
	
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
				
		if (create) {
			 //Create a new index in the directory, removing any previously indexed documents
			 indexWriterConfig.setOpenMode(OpenMode.CREATE);
		} 
		else {
			 //Add new documents to an existing index:
			indexWriterConfig.setOpenMode(OpenMode.CREATE_OR_APPEND);
		}
		
		IndexWriter indexWriter = null;
		try {
			indexWriter = new IndexWriter(directory, indexWriterConfig);
		} catch (IOException e) {
			throw new MergeIndexBuildException(e);
		}
		
		int totalDocuments = 0;
		for(Map.Entry<String, List<Event>> entry : events.entrySet())
			{
				logger.info("---"+entry.getKey()+"---");
				
				for(Event event : entry.getValue())
				{
					Document document = new Document();
					
					IndexableField triggeredByInternalId = new StringField(Constants.EVENT_INTERNAL_ID,String.valueOf(event.getInternalId()),Store.YES);
					document.add(triggeredByInternalId);
					
					IndexableField triggeredByEventIdSource = 
							new StringField(
									Constants.SOURCE_EVENT_ID,
									String.valueOf(event.getSource().name())+"_"+event.getId(),Store.YES);
					document.add(triggeredByEventIdSource);
			
					if(event.getMoreSources()!=null&&event.getMoreSources().size()>0)
					{
						for(Event source : event.getMoreSources())
						{
//							IndexableField internalIdS = 
//									new StringField(
//											Constants.EVENT_INTERNAL_IDS,String.valueOf(source.getInternalId()),Store.YES);
//							document.add(internalIdS);
							
							IndexableField eventIdSource = 
											new StringField(
													Constants.SOURCE_EVENT_ID,
													String.valueOf(source.getSource().name())+"_"+source.getId(),Store.YES);
							document.add(eventIdSource);
						}	
					}
				
					IndexableField segmentKey = new StringField(Constants.SEGMENT_KEY,event.getSegmentKey(),Store.YES);
					document.add(segmentKey);

//					IndexableField sourceEventId = new StringField(Constants.ID,String.valueOf(event.getId()),Store.YES);
//					document.add(sourceEventId);

					IndexableField eventName = new TextField(Constants.EVENT_NAME,event.getName(),Store.YES);
					document.add(eventName);
					
					
					//for maps
					IndexableField eventNameSourceFinal = new 
							StringField(Constants.EVENT_NAME_SOURCE_FINAL,
										String.valueOf(event.getSource().name())+"_"+event.getOriginalName(),
									    Store.YES);
					document.add(eventNameSourceFinal);
					
					if(event.getMoreSources()!=null&&event.getMoreSources().size()>0)
					{
						for(Event moreSourcesEvent : event.getMoreSources())
						{
							eventNameSourceFinal = new StringField(Constants.EVENT_NAME_SOURCE_FINAL,
									String.valueOf(moreSourcesEvent.getSource().name())+"_"+moreSourcesEvent.getOriginalName(),Store.YES);
							document.add(eventNameSourceFinal);
						}
					}
					
					//set date for seating charts 
//					if(event.isFinalTime())
					//{
						// UpdatedSince in ISO 8601 Format (Ex. 2013-06-21T05:32:07.65)
						// 2014-01-21T19:30 

						DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
						String dateAsISO8601 = df.format(event.getDate());
	
						IndexableField dateForMap = new StringField(Constants.DATE_FOR_TICKET_UTILS_MAP,dateAsISO8601,Store.YES);	
						document.add(dateForMap);
						
					//}
					
					
					
					
					
					
					
//					IndexableField eventSource = new StringField(Constants.SOURCE,event.getSource().name(),Store.YES);	
//					document.add(eventSource);
					
					
					//NumericRangeQuery for date range
					IndexableField eventDate = new LongField(Constants.DATE,event.getDate().getTime(),Store.YES);
					document.add(eventDate);

					
					String displayDateString = this.getDisplayDate(event);
					IndexableField displayDate = new StringField(Constants.DISPLAY_DATE,displayDateString,Store.YES);	
					document.add(displayDate);

				
					
					
					//date sort
					IndexableField sortedEventDate = new NumericDocValuesField(Constants.DATE_SORT,event.getDate().getTime());					
					document.add(sortedEventDate);
					
					//how to add sort
					//https://lucene.apache.org/core/4_0_0/core/org/apache/lucene/document/LongField.html

					IndexableField isFinalTime = new StringField(Constants.IS_FINAL_TIME,String.valueOf(event.isFinalTime()),Store.YES);	
					document.add(isFinalTime);
					
					
					//venue
					//this.processVenueNames( event , document , identicalVenues );
					
					Set<String> venues = new HashSet<String>();
					
					venues.add(event.getVenue().getName());
					
					if(event.getMoreSources()!=null&&event.getMoreSources().size()>0)
					{
						for(Event moreSourcesEvent : event.getMoreSources())
						{
							venues.add(moreSourcesEvent.getVenue().getName());
						}
					}
					
					for(String venue : venues)
					{
						IndexableField venueNameIndex = new TextField(Constants.VENUE_NAME,venue,Store.YES);
						document.add( venueNameIndex );						
					}
					
					//add org data about venue
					
					IndexableField venueNameSourceFinal = 
							new StringField(Constants.VENUE_NAME_SOURCE_FINAL,
									String.valueOf(event.getSource().name())+"_"+event.getVenue().getOriginalName(),Store.YES);
					document.add( venueNameSourceFinal );	
					
					if(event.getMoreSources()!=null&&event.getMoreSources().size()>0)
					{
						for(Event moreSourcesEvent : event.getMoreSources())
						{
							venueNameSourceFinal = 
									new StringField(Constants.VENUE_NAME_SOURCE_FINAL,
											String.valueOf(moreSourcesEvent.getSource().name())+"_"+moreSourcesEvent.getVenue().getOriginalName(),Store.YES);
							
							document.add( venueNameSourceFinal );							
						}
					}
					
					//

					
					
					
					
					
					
					
					
					
					//end venue
					
					
					IndexableField countryName = new TextField(Constants.COUNTRY_NAME,event.getLocation().getCountry().getName(),Store.YES);
					document.add(countryName);
//					IndexableField countryId = new StringField(Constants.COUNTRY_ID,String.valueOf(event.getLocation().getCountry().getId()),Store.YES);
//					document.add(countryId);
					
					if(event.getLocation().getState()!=null){
						IndexableField stateName = new TextField(Constants.STATE_NAME,event.getLocation().getState().getName(),Store.YES);
						document.add(stateName);
//						IndexableField stateId = new StringField(Constants.STATE_ID,String.valueOf(event.getLocation().getState().getId()),Store.YES);
//						document.add(stateId);
					}

					
					
					
					
					
					
					
					
					
					//cities ---------------------------------------------------------------------------
					
					Set<String> cityNames = new HashSet<String>();
					cityNames.addAll(event.getLocation().getCity().getCityNames());
					
					if(event.getMoreSources()!=null&&event.getMoreSources().size()>0)
					{
						for(Event moreSources : event.getMoreSources())
						{
							cityNames.addAll(moreSources.getLocation().getCity().getCityNames());
						}
					}

					
					//smart complete cities
//					Set<String> tmp = new HashSet<String>();
//
//					for(String cityName : cityNames)
//					{
//						String key;
//						try {
//							key = Utils.getLocationKey(event.getSegmentKey(),cityName);
//						} catch (QueryBuilderException e) {
//							throw new MergeIndexBuildException(e);
//						}
//						
//						if(identicalLocations.containsKey(key))
//						{
//							tmp.addAll(identicalLocations.get(key));
//						}
//					}
//					
//					cityNames.addAll(tmp);

					for(String cityName : cityNames)
					{
						document.add(new TextField(Constants.CITY_NAME,cityName.trim(),Store.YES));
					}
					
					//end cities ---------------------------------------------------------------------------

					
					
					
					
					
					
					
					
					
					
					
					
					
					
					
					
					
					
					
					//geocoding start 				
					
					// Add documents with latitude/longitude location:
					// we index these both as DoubleFields (for bounding box/ranges) and as NumericDocValuesFields (for scoring)
				
					//latitude 
					
					document.add(new DoubleField(Constants.LATITUDE,event.getLocation().getLatitude(), Field.Store.YES));
					
					document.add(new NumericDocValuesField(Constants.LATITUDE, Double.doubleToRawLongBits(event.getLocation().getLatitude())));
					
					//longitude
					
					document.add(new DoubleField(Constants.LONGITUDE, event.getLocation().getLongitude(), Field.Store.YES));
					
					document.add(new NumericDocValuesField(Constants.LONGITUDE, Double.doubleToRawLongBits(event.getLocation().getLongitude())));
					
					//geocoding end
					
					
					
					
					
					

					for(Performer performer : event.getPerformers()){
						IndexableField performerName = new TextField(Constants.PERFORMER_NAME,performer.getName(),Store.YES);
						document.add(performerName);
						//not sure how to create direct relation between performer name and id 
//						IndexableField performerId = new StringField(Constants.PERFORMER_ID,String.valueOf(performer.getId()),Store.YES);
//						document.add(performerId);						
					}
					
					try {
						indexWriter.addDocument(document);
						totalDocuments++;
					} catch (IOException e) {
						throw new MergeIndexBuildException(e);
					}
				}
		}
		
		// NOTE: if you want to maximize search performance,
		// you can optionally call forceMerge here.  This can be
		// a terribly costly operation, so generally it's only
		// worth it when your index is relatively static (ie
		// you're done adding documents to it):
		
		// writer.forceMerge(1);
		
		try {
			logger.info("Merge index build: "+totalDocuments+" documents");			
			indexWriter.close();
		} catch (IOException e) {
			logger.error(e);
		}	
	}
	
	private static String DATE_FORMAT_TIME ="d MMM ''yy,EEE h:mm a";
	private static String DATE_FORMAT_SHORT ="d MMM ''yy,EEE";
	
	private String getDisplayDate(Event event) 
	{
		if(event.isFinalTime()==true)
		{
			return DateUtils.dateToStringByFormat(event.getDate(), DATE_FORMAT_TIME );
		}
		else
		{
			return DateUtils.dateToStringByFormat(event.getDate(), DATE_FORMAT_SHORT )+" TBA";
		}
	}

	private void processVenueNames(Event event,Document document,Map<String, List<String>> identicalVenues) throws MergeIndexBuildException
	{
		List<Event> flatEvent = new ArrayList<Event>();
		flatEvent.add(event);
		if(event.getMoreSources()!=null&&event.getMoreSources().size()>0){
			flatEvent.addAll(event.getMoreSources());
		}
		
		List<String> avoidDuplicate = new ArrayList<String>();
		
		for(Event flat : flatEvent)
		{
			String venueName = flat.getVenue().getName().trim();
			String venueKey = null;
			
			for(String cityName : flat.getLocation().getCity().getCityNames())
			{
				try {
					venueKey = com.ctns.query.Utils.getVenueKey(flat.getSegmentKey(),cityName,flat.getVenue().getName());
				} catch (QueryBuilderException e1) {
					throw new MergeIndexBuildException(e1);
				}				
				
				if( identicalVenues!=null && identicalVenues.containsKey( venueKey )==true )
				{
					logger.info("find venue : \""+venueKey+"\" in identical venues map");
					
					for( String identicalVenue : identicalVenues.get(venueKey) )
					{
						if(avoidDuplicate.contains(identicalVenue.toLowerCase()))
						{
							continue;
						}
						else
						{
							IndexableField venueNameIndex = new TextField(Constants.VENUE_NAME,identicalVenue,Store.YES);
							document.add( venueNameIndex );
							
							avoidDuplicate.add(identicalVenue.toLowerCase());
							
							logger.info("add venue : \""+identicalVenue+"\" to index");
						}
					}
				}
				else
				{
					if(avoidDuplicate.contains(venueName.toLowerCase()))
					{
						continue;
					}
					else
					{
						IndexableField venueNameIndex = new TextField(Constants.VENUE_NAME,venueName,Store.YES);
						document.add( venueNameIndex );

						avoidDuplicate.add(venueName.toLowerCase());
						
						logger.info("add venue : "+venueKey+" to index");

					}
				}
			}
		}
	}
}