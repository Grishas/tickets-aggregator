package com.ctnl.events.merge;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.Query;
import org.springframework.beans.factory.annotation.Autowired;

import com.ctnc.Constants;
import com.ctnc.Event;
import com.ctns.collect.index.CollectIndex;
import com.ctns.collect.index.CollectIndexException;
import com.ctns.geocoding.GeocodingService;
import com.ctnu.environment.Environment;
import com.google.gwt.user.client.rpc.core.java.util.Collections;

public class Merge{

	private Logger logger = Logger.getLogger(Merge.class);

	@Autowired 
	private MergeProperties properties;
	
	@Autowired
	private CollectIndex collectIndex;

	@Autowired
	private MergeQueryBuilder mergeQueryBuilder;
	
	@Autowired 
	private GeocodingService geocodingService; 
	
	private Map<Long,Long> 			invertMergeIndex = new HashMap<Long,Long>();	
	private Map<Long,List<Long>> 	mergeIndex = new HashMap<Long, List<Long>>();
	private List<Long> 				singleIndex = new ArrayList<Long>();
	private List<Long> 				errorIndex = new ArrayList<Long>();
	private MergeCaseDecision caseDecision = new MergeCaseDecision();
	private MergeReport report = new MergeReport();
	
	public Map<String, List<Event>> process(List<Map<String, List<Event>>> events) throws MergeException
	{	
		int sourcesNumber = events.size();
		
		int mergeCounter = 0;
		int mergeDifferentCityCounter = 0;
		int notMergeDifferentCityCounter = 0;
		int singleCounter = 0;
		int mergedCounter = 0;
		int mergeOverrideCounter = 0;
		int errorCounter = 0;
		int moreResultsThanMaxNumberOfSourcesCounter = 0;
		int zeroResultsCounter = 0;
		int duplicateSourceInMergeCounter = 0;
		int nothingTodoCounter = 0;
		int totalEventsCounter = 0;
	
		StringBuilder mergeReport = new StringBuilder();	
		StringBuilder mergeDifferentCity = new StringBuilder();	
		StringBuilder notMergeDifferentCity = new StringBuilder();	
		StringBuilder singleReport = new StringBuilder();
		StringBuilder mergedReport = new StringBuilder();
		StringBuilder mergeOverrideReport = new StringBuilder();
		StringBuilder errorReport = new StringBuilder();
		StringBuilder moreResultsThanMaxNumberOfSourcesReport = new StringBuilder();
		StringBuilder zeroResultsReport = new StringBuilder();
		StringBuilder duplicateSourceInMergeResultReport = new StringBuilder();
		StringBuilder nothingTodoReport = new StringBuilder();
		
		for(Map<String, List<Event>> source : events)
		{
			for(Map.Entry<String, List<Event>>  segment : source.entrySet())
			{				
				totalEventsCounter+=segment.getValue().size();
				
				logger.info("---"+segment.getKey()+"--- " + segment.getValue().size());
				
				for(Event event : segment.getValue())
				{
					Query query = this.mergeQueryBuilder.build(event);
					List<Document> documents = null;
					try {
						documents = this.collectIndex.search(query,this.properties.getMaxResults());		 
					} catch (CollectIndexException e) {
						throw new MergeException(e);
					}	
					
					MergeDecision decision = this.caseDecision.decide( event , sourcesNumber , documents , mergeIndex , singleIndex , invertMergeIndex );
					
					Set<Point> points = null;
					
					switch (decision) {
					
					case Merge:	
					
						//location points.For example:LA <> Hollywod
						//if have more than one point check that distance between points is acceptable 
						points = this.getLocation( documents );
						
						//diff cities
						if( points.size() > 1 )
						{
							if( this.isDistanceAcceptable( points, this.properties.getDistanceMaxThreshold() , notMergeDifferentCity , mergeDifferentCity )==true)
							{
								this.merge(sourcesNumber,documents,event);
								mergeCounter++;	
								mergeDifferentCityCounter++;
								this.report.format(decision,documents, query, event, mergeDifferentCity);
							}
							else
							{
								notMergeDifferentCityCounter++;
								errorCounter++;
								this.error(event);
								this.report.format(decision,documents, query, event, notMergeDifferentCity );
								break;
							}							
						}
						else
						{
							this.merge(sourcesNumber,documents,event);
							mergeCounter++;	
							this.report.format(decision,documents, query, event, mergeReport);
						}
						
						break;
						
					case Single:
						this.single(event);
						singleCounter++;
						this.report.format(decision,documents, query, event, singleReport);
						break;
				
					case Merged:
						//just log this
						mergedCounter++;
						this.report.format(decision,documents, query, event, mergedReport);
						break;
					
					case MergeOverride:
						
						
						//location points.For example:LA <> Hollywod
						//if have more than one point check that distance between points is acceptable 
						points = this.getLocation( documents );
						
						//diff cities
						if( points.size() > 1 )
						{
							if( this.isDistanceAcceptable( points, this.properties.getDistanceMaxThreshold() , errorReport , mergeOverrideReport )==true)
							{
								this.removePreviousMerge(event);
								this.merge(sourcesNumber,documents,event);
								mergeOverrideCounter++;
								this.report.format(decision,documents, query, event, mergeOverrideReport);
							}
							else	
							//put attention that event stay merged
							{
								this.error(event);
								errorCounter++;
								this.report.format(decision,documents, query, event, errorReport);
								break;
							}							
						}
						else
						{
							this.removePreviousMerge(event);
							this.merge(sourcesNumber,documents,event);
							mergeOverrideCounter++;
							this.report.format(decision,documents, query, event, mergeOverrideReport);
						}
						
						break;
				
					case Error:
						this.error(event);
						errorCounter++;
						this.report.format(decision,documents, query, event, errorReport);
						break;

					case DuplicateSourceInMergeResult:
						
						 //try to resolve with negative values 
						 //field name , list of values 
//						 Map<String,List<String>> negative = new  HashMap<String, List<String>>();
//						 List<String> values = new ArrayList<String>();
//						 values.add("parking");
//						 negative.put(Constants.NAME,values);
						 
						 
						duplicateSourceInMergeCounter++;
						this.report.format(decision,documents, query, event, duplicateSourceInMergeResultReport);
						break;
						
					case MoreResultsThanMaxNumberOfSources:
						//try to resolve later
						moreResultsThanMaxNumberOfSourcesCounter++;
						this.report.format(decision,documents, query, event, moreResultsThanMaxNumberOfSourcesReport );
						break;
						
					case ZeroResults:
						//try to resolve later 
						zeroResultsCounter++;
						this.report.format(decision,documents, query, event, zeroResultsReport);
						break;
						
					case NothingTodo:
						//just log this
						nothingTodoCounter++;
						this.report.format(decision,documents, query, event, nothingTodoReport);
						break;
					}					
				}
			}
		}	
	
		this.report.report(
				
				sourcesNumber,
				
				invertMergeIndex,
				mergeIndex,
				singleIndex,
				errorIndex,
				
				mergeCounter,
				mergeDifferentCityCounter,
				notMergeDifferentCityCounter,
				singleCounter,
				mergedCounter,	
				mergeOverrideCounter,
				errorCounter,
				moreResultsThanMaxNumberOfSourcesCounter,
				zeroResultsCounter,
				duplicateSourceInMergeCounter,
				nothingTodoCounter,
				totalEventsCounter,
				
				mergeReport,	
				mergeDifferentCity,
				notMergeDifferentCity,
				singleReport,
				mergedReport,
				mergeOverrideReport,
				errorReport,
				moreResultsThanMaxNumberOfSourcesReport,
				zeroResultsReport,
				duplicateSourceInMergeResultReport,
				nothingTodoReport
				
				
				);
		
		return this.toResults(events,totalEventsCounter);	
	}	
	
	private class Point
	{
		String locationName;
		double latitude;
		double longitude;
		
		@Override
		public String toString() {
			return "Point [locationName=" + locationName + ", latitude="
					+ latitude + ", longitude=" + longitude + "]"+Environment.getLineSeparator();
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			long temp;
			temp = Double.doubleToLongBits(latitude);
			result = prime * result + (int) (temp ^ (temp >>> 32));
			result = prime * result
					+ ((locationName == null) ? 0 : locationName.hashCode());
			temp = Double.doubleToLongBits(longitude);
			result = prime * result + (int) (temp ^ (temp >>> 32));
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Point other = (Point) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (Double.doubleToLongBits(latitude) != Double
					.doubleToLongBits(other.latitude))
				return false;
			if (locationName == null) {
				if (other.locationName != null)
					return false;
			} else if (!locationName.equals(other.locationName))
				return false;
			if (Double.doubleToLongBits(longitude) != Double
					.doubleToLongBits(other.longitude))
				return false;
			return true;
		}
		private Merge getOuterType() {
			return Merge.this;
		}
		
	}
	
	private Set<Point> getLocation(List<Document> documents) {
		
		Set<Point> points = new HashSet<Point>(1);
		
		for(Document document : documents){
			
			String segmentKey = document.getField(Constants.SEGMENT_KEY).stringValue();
			
			String[] tmp = segmentKey.split("_");
			
			if(tmp.length==1)
			{
				segmentKey = ","+tmp[0];
			}
			else
			{
				segmentKey =   ","+tmp[1]+","+ tmp[0];
			}
			
			String city = document.getField(Constants.CITY_NAME).stringValue();
			
//			double latitude = Double.valueOf(document.get(Constants.LATITUDE));
//			double longitude = Double.valueOf(document.get(Constants.LONGITUDE));
			
			double latitude = document.getField(Constants.LATITUDE).numericValue().doubleValue();
			double longitude = document.getField(Constants.LONGITUDE).numericValue().doubleValue();

			Point point = new Point();
			point.locationName= city + segmentKey;
			point.latitude= latitude;
			point.longitude= longitude;
			
			points.add(point);
		}
		
		return points;
	}

	
	private boolean isDistanceAcceptable(Set<Point> points, double distanceMaxThreshold, StringBuilder errorReport ,StringBuilder mergeReport) 
	{
		Set<Double> result = new HashSet<Double>();
		
		Point[] p = new Point[points.size()];
		points.toArray(p);
		
		StringBuilder report = new StringBuilder();
		
		for(int i =0; i<points.size(); i++ )
		{
			for(int j = i + 1; j < points.size() ; j++ )
			{
				Point pi = p[i];
				Point pj = p[j];
				
				Double distance = 
						this.geocodingService.haversine(
								pi.latitude , pi.longitude , pj.latitude , pj.longitude );
				
				result.add(distance);
				
				report.append(pi.toString()+pj.toString()+".Distance="+distance+".Distance max threshold parameter="+distanceMaxThreshold+Environment.getLineSeparator());
				
			}
		}
		
		Double maxDistance = java.util.Collections.max(result);
		
		if( maxDistance >= distanceMaxThreshold )
		{
			logger.error(report.toString());
			
			errorReport.append(report);
			errorReport.append("maxDistance="+distanceMaxThreshold+" >= distanceMaxThreshold="+distanceMaxThreshold+Environment.getLineSeparator());

			return false;
		}
		
		report.append("maxDistance="+distanceMaxThreshold+" < distanceMaxThreshold="+distanceMaxThreshold+Environment.getLineSeparator());
		
		mergeReport.append(report);
		
		return true;
	}

	private void error(Event event) throws MergeException 
	{
		//debug
		
		//check that not in single index --- can't occured,just for tests after remove it
		if(this.singleIndex.contains(event.getInternalId())){
			throw new MergeException("this.singleIndex.contains(event.getInternalId())");
		}
		//check that not in merge index --- can't occured,just for tests after remove it
		if(this.invertMergeIndex.containsKey(event.getInternalId())){
			throw new MergeException("this.invertMergeIndex.containsKey(event.getInternalId())");
		}
		
		//debug
		
		this.errorIndex.add(event.getInternalId());
	}

	private void removePreviousMerge(Event event)
	{
		//get parent of previous event 
		Long previousMerge = this.invertMergeIndex.get(event.getInternalId());
		//get all related events of previous event 
		List<Long> previousMergeEvents = this.mergeIndex.get(previousMerge);
		
		//remove previous from merge
		this.mergeIndex.remove(previousMerge);
		//remove from help map	
		for(Long related : previousMergeEvents){
			this.invertMergeIndex.remove(related);
		}
	}
	
	private void single(Event event)
	{
		this.singleIndex.add(event.getInternalId());
	}
	
	private void merge(int sourcesNumber, List<Document> documents, Event event)
	{
		//final step in merge 
		List<Long> similar = new ArrayList<Long>(sourcesNumber);
		for(Document document : documents)
		{	
			Long internalId = 
					Long.valueOf(document.getField(Constants.EVENT_INTERNAL_ID).stringValue());	
			
			if( ! (internalId.longValue()==event.getInternalId()) )
			{
				//if related events in single index list then remove it
				if( this.singleIndex.contains( internalId ) )
				{
					this.singleIndex.remove( internalId );
				}
				
				similar.add(internalId);
				
				//helper 
				this.invertMergeIndex.put(internalId, event.getInternalId());
			}
		}
		
		this.mergeIndex.put(event.getInternalId() , similar);
	}	

	private Map<String, List<Event>> toResults(List<Map<String, List<Event>>> events, int totalEventsCounter) {
		
		Map<Long,Event> tmp = new HashMap<Long, Event>(totalEventsCounter);
		for(Map<String, List<Event>> source : events){
			for(Map.Entry<String, List<Event>>  segment : source.entrySet()){								
				for(Event event : segment.getValue()){
					tmp.put(event.getInternalId(),event);
				}
			}
		}
		
		Map<String, List<Event>> result = new HashMap<String, List<Event>>();
		
		//merge
		for(Map.Entry<Long,List<Long>> entry : this.mergeIndex.entrySet()){
			
			Event parent = tmp.get(entry.getKey());
			
			List<Event> childs = new ArrayList<Event>();
			
			for(Long child : entry.getValue())
			{
				childs.add(tmp.get(child));
			}
			
			parent.setMoreSources(childs);
			
			if(result.containsKey(parent.getSegmentKey())){
				result.get(parent.getSegmentKey()).add(parent);
			}
			else{
				List<Event> events_ = new ArrayList<Event>();
				events_.add(parent);
				result.put(parent.getSegmentKey(),events_);
			}
		}
		
		//single
		for(Long internalId : this.singleIndex)
		{
			Event event = tmp.get(internalId);
			
			if(result.containsKey(event.getSegmentKey())){
				result.get(event.getSegmentKey()).add(event);
			}
			else{
				List<Event> events_ = new ArrayList<Event>();
				events_.add(event);
				result.put(event.getSegmentKey(),events_);
			}
		}
		
		return result;
	}
}