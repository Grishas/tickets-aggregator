package com.ctns.collect.index.build;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.DoubleField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
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

public class CollectIndexBuild {

	private Logger logger = Logger.getLogger(CollectIndexBuild.class);

	@Autowired 
	private CollectIndexBuildProperties properties;

	@SuppressWarnings("resource")
	public void build(List<Map<String, List<Event>>> events) throws CollectIndexBuildException{

		boolean create = true;
		
		Path pathToFile = Paths.get(Resources.getCollectIndexData(this.properties.getBaseFolderPath()));
		if( ! Files.exists(pathToFile)){
			try {
				Files.createDirectory(pathToFile);
			} catch (IOException e1) {
				throw new CollectIndexBuildException(e1);
			}
		}
		
		Directory directory = null;
		try {
			directory = FSDirectory.open( Paths.get(Resources.getCollectIndexData(this.properties.getBaseFolderPath())) );
		} catch (IOException e) {
			throw new CollectIndexBuildException(e);
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
			throw new CollectIndexBuildException(e);
		}
		
		int totalDocuments = 0;
		for(Map<String, List<Event>> source : events)
		{
			for(Map.Entry<String, List<Event>> segment : source.entrySet())
			{
				logger.info(segment.getKey());
				
				for(Event event : segment.getValue())
				{
					Document document = new Document();
					
					IndexableField internalId = new StringField(Constants.EVENT_INTERNAL_ID,String.valueOf(event.getInternalId()),Store.YES);
					document.add(internalId);
					
					IndexableField segmentKey = new StringField(Constants.SEGMENT_KEY,event.getSegmentKey(),Store.YES);
					document.add(segmentKey);

					IndexableField sourceEventId = new StringField(Constants.ID,String.valueOf(event.getId()),Store.YES);
					document.add(sourceEventId);

					IndexableField eventName = new TextField(Constants.EVENT_NAME,event.getName(),Store.YES);
					document.add(eventName);
					
					IndexableField eventSource = new StringField(Constants.SOURCE,event.getSource().name(),Store.YES);	
					document.add(eventSource);
					
					//for testing
					IndexableField displayDate = new StringField(Constants.DISPLAY_DATE,event.getDisplayDate(),Store.YES);	
					document.add(displayDate);
					//for testing
					
					//NumericRangeQuery for date range
					IndexableField eventDate = new LongField(Constants.DATE,event.getDate().getTime(),Store.YES);
					document.add(eventDate);
					
					IndexableField isFinalTime = new StringField(Constants.IS_FINAL_TIME,String.valueOf(event.isFinalTime()),Store.YES);
					document.add(isFinalTime);

					IndexableField venueName = new TextField(Constants.VENUE_NAME,event.getVenue().getName(),Store.YES);
					document.add(venueName);
					//IndexableField venueId = new StringField(Constants.VENUE_ID,String.valueOf(event.getVenue().getId()),Store.YES);
					//document.add(venueId);
					
					IndexableField countryName = new TextField(Constants.COUNTRY_NAME,event.getLocation().getCountry().getName(),Store.YES);
					document.add(countryName);
					//IndexableField countryId = new StringField(Constants.COUNTRY_ID,String.valueOf(event.getLocation().getCountry().getId()),Store.YES);
					//document.add(countryId);
					
					if(event.getLocation().getState()!=null){
						IndexableField stateName = new TextField(Constants.STATE_NAME,event.getLocation().getState().getName(),Store.YES);
						document.add(stateName);
						//IndexableField stateId = new StringField(Constants.STATE_ID,String.valueOf(event.getLocation().getState().getId()),Store.YES);
						//document.add(stateId);
					}
					
					for(String cityName_ : event.getLocation().getCity().getCityNames())
					{
						IndexableField cityName = new TextField(Constants.CITY_NAME,cityName_,Store.YES);
						document.add(cityName);
					}

					for(Performer performer : event.getPerformers()){
						IndexableField performerName = new TextField(Constants.PERFORMER_NAME,performer.getName(),Store.YES);
						document.add(performerName);
						//not sure how to create direct relation between performer name and id 
						//IndexableField performerId = new StringField(Constants.PERFORMER_ID,String.valueOf(performer.getId()),Store.YES);
						//document.add(performerId);						
					}
					
					//latitude 
					
					document.add(new DoubleField(Constants.LATITUDE,event.getLocation().getLatitude(), Field.Store.YES));
					
					//document.add(new NumericDocValuesField(Constants.LATITUDE, Double.doubleToRawLongBits(event.getLocation().getLatitude())));
					
					//longitude
					
					document.add(new DoubleField(Constants.LONGITUDE, event.getLocation().getLongitude(), Field.Store.YES));
					
					//document.add(new NumericDocValuesField(Constants.LONGITUDE, Double.doubleToRawLongBits(event.getLocation().getLongitude())));
					
					//geocoding end
					
					try {
						indexWriter.addDocument(document);
						totalDocuments++;
					} catch (IOException e) {
						throw new CollectIndexBuildException(e);
					}
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
			logger.info("Search build create index with :"+totalDocuments+" documents");			
			indexWriter.close();
		} catch (IOException e) {
			logger.error(e);
		}	
	}
}