package com.ctnl.events.merge;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import com.ctnc.Constants;
import com.ctnc.Event;
import com.ctnc.shared.Performer;

public class MergeQueryBuilder {

//	public Query performerName(Event event) throws EventsQueryException
//	{
//		BooleanQuery performerBooleanQuery = new BooleanQuery();
//		
//		if(event.getPerformers()!=null && event.getPerformers().size() > 0 )
//		{
//			for(Performer performer : event.getPerformers())
//			{
//				for(Query query :  this.queryFieldBuilder(Constants.PERFORMER_NAME,performer.getName()))
//				{
//					performerBooleanQuery.add(query, Occur.MUST);
//				}
//			}			
//		}	
//		
//		return performerBooleanQuery;
//	}
	
	public Query eventName(Event event) throws MergeException
	{
		BooleanQuery nameBooleanQuery = new BooleanQuery();

		for(Query query :  this.queryFieldBuilder(Constants.EVENT_NAME,event.getName()))
		{
			nameBooleanQuery.add(query, Occur.MUST);
		}
		
		return nameBooleanQuery;
	}
	
//	public Query venueName(Event event) throws EventsQueryException
//	{
//		BooleanQuery nameBooleanQuery = new BooleanQuery();
//
//		for(Query query :  this.queryFieldBuilder(Constants.VENUE_NAME,event.getVenue().getName()))
//		{
//			nameBooleanQuery.add(query, Occur.MUST);
//		}
//		
//		return nameBooleanQuery;
//	}

	public Query build(Event event) throws MergeException{	
		return this.build(event,null);
	}
	
	/*based on collector index builder fields*/
	private void buildQueryForStringField(Event event,BooleanQuery booleanQuery) throws MergeException{
		
		Query query = new TermQuery(new Term(Constants.IS_FINAL_TIME,String.valueOf(event.isFinalTime())));
		booleanQuery.add(query,Occur.MUST);
		
		query = new TermQuery(new Term(Constants.SEGMENT_KEY,event.getSegmentKey()));
		booleanQuery.add(query, Occur.MUST);
		
		//-----------------------------------------------------------------------------
		
//		Set<String> venueName;
//		try {
//			venueName = Utils.removeDuplicateWords(event.getVenue().getName());
//		} catch (EventsQueryException e) {
//			throw new MergeException(e);
//		}
//		
//		for(String venueName_ : venueName){
//			booleanQuery.add(new TermQuery(new Term(Constants.VENUE_NAME,venueName_)), Occur.MUST);
//		}
//		
//		BooleanQuery nameOrPerformers = new BooleanQuery();
//		
//		BooleanQuery booleanQueryEventName = new BooleanQuery();
//		Set<String> eventName;
//		try {
//			eventName = Utils.removeDuplicateWords(event.getName());
//		} catch (EventsQueryException e) {
//			throw new MergeException(e);
//		}
//		for(String eventName_ : eventName){
//			booleanQueryEventName.add(new TermQuery(new Term(Constants.EVENT_NAME,eventName_)), Occur.MUST);
//		}
//		
//		nameOrPerformers.add(booleanQueryEventName,Occur.SHOULD);
//		
//		if(event.getPerformers().size()>0)
//		{	
//			BooleanQuery booleanQueryPerformer = new BooleanQuery();
//			
//			for( Performer performer : event.getPerformers() )
//			{
//				Set<String> performerName = null;
//				try {
//					performerName = Utils.removeDuplicateWords(performer.getName());
//				} catch (EventsQueryException e) {
//					throw new MergeException(e);
//				}
//				
//				for(String performerName_ :  performerName)
//				{
//					booleanQueryPerformer.add(new TermQuery(new Term(Constants.PERFORMER_NAME,performerName_)), Occur.MUST);
//				}
//			}
//			
//			nameOrPerformers.add(booleanQueryPerformer,Occur.SHOULD);
//		}
//		
//		booleanQuery.add(nameOrPerformers,Occur.MUST);
		
		//end and ( with event name or performer name )
		
//		System.out.println(booleanQuery.toString());
		//--------------------------------------------------------------------------------	
	}
	
	private void buildQueryForTextField(Event event,BooleanQuery booleanQuery) throws MergeException{
		
//		for(Query query :  this.queryFieldBuilder(Constants.CITY_NAME,event.getLocation().getCity().getName())){
//			booleanQuery.add(query, Occur.MUST);
//		}
		
		for(Query query :  this.queryFieldBuilder(Constants.VENUE_NAME,event.getVenue().getName())){
			booleanQuery.add(query, Occur.MUST);
		}
		
		//and ( with event name or performer name )
		
		BooleanQuery nameOrPerformers = new BooleanQuery();
		
		BooleanQuery booleanQueryName = new BooleanQuery();
		for(Query query :  this.queryFieldBuilder(Constants.EVENT_NAME,event.getName())){
			booleanQueryName.add(query, Occur.MUST);
		}
		nameOrPerformers.add(booleanQueryName,Occur.SHOULD);
		
		if(event.getPerformers().size()>0){
			BooleanQuery booleanQueryPerformer = new BooleanQuery();
			for(Performer performer : event.getPerformers()){
				for(Query query :  this.queryFieldBuilder(Constants.PERFORMER_NAME,performer.getName())){
					booleanQueryPerformer.add(query, Occur.MUST);
				}
			}
			
			nameOrPerformers.add(booleanQueryPerformer,Occur.SHOULD);
		}
		
		booleanQuery.add(nameOrPerformers,Occur.MUST);
		
		//end and ( with event name or performer name )
		
	}
	
	private void buildQueryForNumericRangeQuery(Event event,BooleanQuery booleanQuery) throws MergeException{
		
		booleanQuery.add(
				NumericRangeQuery.newLongRange(
						Constants.DATE,
						Long.valueOf(event.getDate().getTime()),
						Long.valueOf(event.getDate().getTime()), true, true),
																		Occur.MUST);
	}
	
	public Query build(Event event,Map<String,List<String>> negative) throws MergeException
	{
		BooleanQuery booleanQuery = new BooleanQuery();

		//build query for NumericRangeQuery
		this.buildQueryForNumericRangeQuery(event,booleanQuery);
		
		//build query for StringField
		this.buildQueryForStringField(event,booleanQuery);
		
		//build query for TextField
		this.buildQueryForTextField(event,booleanQuery);
		
		
		//negative
//		if(negative!=null && negative.size()>0)
//		{
//			//BooleanQuery negativeQuery = new BooleanQuery();
//
//			for(Map.Entry<String,List<String>> entry : negative.entrySet())
//			{
//				for(String value : entry.getValue())
//				{
//					for(Query query : this.queryFieldBuilder(entry.getKey(),value))
//					{
//						//negativeQuery.add(query,Occur.MUST_NOT);	
//						
//						booleanQuery.add(query,Occur.MUST_NOT);	
//					}
//				}
//			}
//			
//			//booleanQuery.add(negativeQuery,Occur.SHOULD);
//		}
		//negative
	
		return booleanQuery;
	}
	
	public List<Query> queryFieldBuilder(String fieldName,String data) throws MergeException
	{
		TokenStream tokenStream;
		try {
			tokenStream = new EnglishAnalyzer().tokenStream(fieldName,new StringReader(data));
		} catch (IOException e) {
			throw new MergeException(e);
		}
		
		//OffsetAttribute offsetAtt = tokenStream.addAttribute(OffsetAttribute.class);
	 
		CharTermAttribute cattr = tokenStream.addAttribute(CharTermAttribute.class);

		try {
			tokenStream.reset(); // Resets this stream to the beginning. (Required)
		} catch (IOException e) {
			throw new MergeException(e);
		}

		//logger.info(fieldName+" : "+data);
		
		StringBuilder sb = new StringBuilder();
		List<Query> terms = new ArrayList<Query>();	
		try {
			while (tokenStream.incrementToken() ) {
				sb.append(cattr.toString()+" ");
			    //System.out.println(ts.reflectAsString(true));    
			    //System.out.println("token start offset: " + offsetAtt.startOffset());
				//System.out.println("token end offset   : " + offsetAtt.endOffset());
				
				terms.add( new TermQuery(new Term(fieldName,cattr.toString())));
				 				
			 }
		} catch (IOException e) {
			throw new MergeException(e);
		}
		
		try {
			tokenStream.end(); // Perform end-of-stream operations, e.g. set the final offset.
		} catch (IOException e) {
			throw new MergeException(e);
		}  
		
		try {
			tokenStream.close();// Release resources associated with this stream.
		} catch (IOException e) {
			throw new MergeException(e);
		} 
	
		return terms;	
	}
}
