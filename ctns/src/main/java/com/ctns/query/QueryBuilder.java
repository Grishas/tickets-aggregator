package com.ctns.query;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.facet.range.DoubleRange;
import org.apache.lucene.index.Term;
import org.apache.lucene.sandbox.queries.FuzzyLikeThisQuery;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.NGramPhraseQuery;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.search.TermAutomatonQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.util.SloppyMath;

import com.ctnc.Constants;
import com.ctnc.shared.IndexKey;
import com.ctnc.shared.Source;
import com.ctns.merge.index.MergeIndexException;

public class QueryBuilder {
	
	private Logger logger = Logger.getLogger(QueryBuilder.class);

	/*Given a latitude and longitude (in degrees) and the
	maximum great circle (surface of the earth) distance,
	returns a simple Filter bounding box to "fast match" candidates.*/
	public Filter getBoundingBoxFilter(double originLat, double originLng, double maxDistanceKM) 
	{
		// Basic bounding box geo math from http://JanMatuschek.de/LatitudeLongitudeBoundingCoordinates,
		// licensed under creative commons 3.0 http://creativecommons.org/licenses/by/3.0
		
		// TODO: maybe switch to recursive prefix tree instead
		// (in lucene/spatial)?  It should be more efficient
		// since it's a 2D trie...
	
		// Degrees -> Radians:
		double originLatRadians = Math.toRadians(originLat);
		double originLngRadians = Math.toRadians(originLng);
		
		double angle = maxDistanceKM / (SloppyMath.earthDiameter(originLat) / 2.0);
	
		double minLat = originLatRadians - angle;
		double maxLat = originLatRadians + angle;
		
		double minLng;
		double maxLng;
		
		if (minLat > Math.toRadians(-90) && maxLat < Math.toRadians(90)) 
		{
			double delta = Math.asin(Math.sin(angle)/Math.cos(originLatRadians));
			minLng = originLngRadians - delta;
			
			if (minLng < Math.toRadians(-180)) 
			{
		        minLng += 2 * Math.PI;
	        }
		
			maxLng = originLngRadians + delta;
		      
			if (maxLng > Math.toRadians(180)) 
			{
		        maxLng -= 2 * Math.PI;
		    }
		} 
		else 
		{
			// The query includes a pole!
			minLat = Math.max(minLat, Math.toRadians(-90));
			maxLat = Math.min(maxLat, Math.toRadians(90));
			minLng = Math.toRadians(-180);
			maxLng = Math.toRadians(180);
		 }
		
		BooleanQuery f = new BooleanQuery();
	
		// Add latitude range filter:
		f.add(NumericRangeQuery.newDoubleRange(
				Constants.LATITUDE, 
				Math.toDegrees(minLat), 
				Math.toDegrees(maxLat), 
				true, 
				true),
				BooleanClause.Occur.FILTER);
		
		// Add longitude range filter:
		if (minLng > maxLng) 
		{
		    // The bounding box crosses the international date
		
		      BooleanQuery lonF = new BooleanQuery();
		      
		      lonF.add(NumericRangeQuery.newDoubleRange(Constants.LONGITUDE, Math.toDegrees(minLng), null, true, true),
		               BooleanClause.Occur.SHOULD);
		      
		      lonF.add(NumericRangeQuery.newDoubleRange(Constants.LONGITUDE, null, Math.toDegrees(maxLng), true, true),
		               BooleanClause.Occur.SHOULD);
		      
		      f.add(lonF, BooleanClause.Occur.MUST);
		  } 
		  else 
		  {
		      f.add(NumericRangeQuery.newDoubleRange(Constants.LONGITUDE, Math.toDegrees(minLng), Math.toDegrees(maxLng), true, true),
		            BooleanClause.Occur.FILTER);
		  }
		
		return new QueryWrapperFilter(f);
	 }
	
	
	
	
	public Query buildQuery(IndexKey indexKey, String query, Date from, Date to, String city,String segmentKey) throws QueryBuilderException
	{
		String field = null;
		
		BooleanQuery booleanQuery = new BooleanQuery();

		if(indexKey!=null)
		{
			switch (indexKey) 
			{
				case event:
					field = Constants.EVENT_NAME;
					break;
					
				case performer:
					field = Constants.PERFORMER_NAME;
					break;
					
				case venue:
					field = Constants.VENUE_NAME;
					break;
			}
			
			booleanQuery.add( this.buildQuery( field , query ) , Occur.MUST );
		}
		
		this.generateDateQuery(booleanQuery, from, to);
		
		this.generateLocationQuery(booleanQuery, city, segmentKey);		
		
		return booleanQuery;
		
	}
	
	private void generateDateQuery(BooleanQuery booleanQuery,Date from,Date to){
		//always we set this values even without user
		booleanQuery.add(NumericRangeQuery.newLongRange(Constants.DATE , from.getTime() , to.getTime(), true,true),Occur.MUST);
	}
	
	public Query generateQueryBySourceEventId(Map<Source, Long> requests)
	{
		BooleanQuery booleanQuery = new BooleanQuery();
		
		for(Map.Entry<Source,Long> request : requests.entrySet())
		{
			Query query = 
					new TermQuery(new Term(Constants.SOURCE_EVENT_ID,request.getKey().name()+"_"+request.getValue() ));
			
			booleanQuery.add(query,Occur.MUST);	
		}
		
		return booleanQuery;
	}
	
	public void generateLocationQuery(BooleanQuery booleanQuery,String city,String segmentKey) throws QueryBuilderException
	{
		if(segmentKey!=null)
		{
			Query query = new TermQuery(new Term(Constants.SEGMENT_KEY,segmentKey));
			booleanQuery.add(query, Occur.MUST);
		}

		if(city!=null)
		{
			booleanQuery.add(this.buildQuery(Constants.CITY_NAME,city),Occur.MUST);
		}
	}
	
	public Query buildQuery(String field,String name) throws QueryBuilderException
	{
		BooleanQuery booleanQuery = new BooleanQuery();

		for(Query query :  this.queryFieldBuilder(field,name))
		{
			booleanQuery.add(query, Occur.MUST);
		}
		
		return booleanQuery;
	}
	
//	public Query venue(Event event) throws EventsQueryException
//	{
//		BooleanQuery venueBooleanQuery = new BooleanQuery();
//
//		for(Query query :  this.queryFieldBuilder(Constants.VENUE_NAME,event.getVenue().getName()))
//		{
//			venueBooleanQuery.add(query, Occur.MUST);
//		}
//		
//		return venueBooleanQuery;
//	}
	
	public List<Query> queryFieldBuilder(String fieldName,String data) throws QueryBuilderException
	{
		TokenStream tokenStream;
		EnglishAnalyzer englishAnalyzer = new EnglishAnalyzer();
		try {
			tokenStream = englishAnalyzer.tokenStream(fieldName,new StringReader(data));
		} catch (IOException e) {
			throw new QueryBuilderException(e);
		}
		finally {
			englishAnalyzer.close();
		}
		
		//OffsetAttribute offsetAtt = tokenStream.addAttribute(OffsetAttribute.class);
	 
		CharTermAttribute cattr = tokenStream.addAttribute(CharTermAttribute.class);

		try {
			tokenStream.reset(); // Resets this stream to the beginning. (Required)
		} catch (IOException e) {
			throw new QueryBuilderException(e);
		}
		
		StringBuilder sb = new StringBuilder();
		List<Query> terms = new ArrayList<Query>();	
		
		try {
			while (tokenStream.incrementToken() ) {
				sb.append(cattr.toString()+" ");
			    //System.out.println(ts.reflectAsString(true));    
			    //System.out.println("token start offset: " + offsetAtt.startOffset());
				//System.out.println("token end offset   : " + offsetAtt.endOffset());
				
				//terms.add( new TermQuery(new Term(fieldName,cattr.toString())));
				
				terms.add( new PrefixQuery(new Term(fieldName,cattr.toString())));
				
				//terms.add( new FuzzyQuery(new Term(fieldName,cattr.toString())));

//				NGramPhraseQuery query = new NGramPhraseQuery(2);
//				query.add(new Term(fieldName,cattr.toString()));
//				terms.add( query );
				
			 }
		} catch (IOException e) {
			throw new QueryBuilderException(e);
		}
		
		try {
			tokenStream.end(); // Perform end-of-stream operations, e.g. set the final offset.
		} catch (IOException e) {
			throw new QueryBuilderException(e);
		}  
		
		try {
			tokenStream.close();// Release resources associated with this stream.
		} catch (IOException e) {
			throw new QueryBuilderException(e);
		} 
		
		//System.out.println(terms);
		
		return terms;	
	}

	public Query buildQuery(
			Integer maxDistanceKmSteps, 
			String query, 
			Date from, Date to, 
			IndexKey indexKey,
			double latitude, double longitude) throws QueryBuilderException {
		
		BooleanQuery booleanQuery = (BooleanQuery) this.buildQuery(indexKey,query,from,to,null,null);
	
		Filter filter = this.getBoundingBoxFilter( latitude , longitude , maxDistanceKmSteps );
		
		booleanQuery.add(filter, Occur.MUST);
		
		return booleanQuery;
	}
	
	public Query buildQuery(Integer maxDistanceKmSteps,double latitude, double longitude) throws QueryBuilderException {
		
		BooleanQuery booleanQuery = new BooleanQuery();
		
		Filter filter = this.getBoundingBoxFilter( latitude , longitude , maxDistanceKmSteps );
		
		booleanQuery.add(filter, Occur.MUST);
		
		return booleanQuery;
	}
	
	
	
	
	
	
}
