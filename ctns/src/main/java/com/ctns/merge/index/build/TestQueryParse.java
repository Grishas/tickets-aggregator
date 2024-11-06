package com.ctns.merge.index.build;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

import com.ctnc.Constants;

public class TestQueryParse {

	private List<Query> queryBuilder(String fieldName,String data)
	{
		TokenStream tokenStream = null;
		try {
			tokenStream = new EnglishAnalyzer().tokenStream(fieldName,new StringReader(data));
		} catch (IOException e) {
		}
		
		//OffsetAttribute offsetAtt = tokenStream.addAttribute(OffsetAttribute.class);
	 
		CharTermAttribute cattr = tokenStream.addAttribute(CharTermAttribute.class);

		try {
			tokenStream.reset(); // Resets this stream to the beginning. (Required)
		} catch (IOException e) {
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
				
				if(fieldName.equals(Constants.DATE)){
					terms.add(
							NumericRangeQuery.newLongRange(fieldName,Long.valueOf(data),Long.valueOf(data), true, true));
				}
				else{
					terms.add( new TermQuery(new Term(fieldName,cattr.toString())));
				} 
			 }
		} catch (IOException e) {
		}
		
	//	logger.info(sb.toString());

		try {	
			tokenStream.end(); // Perform end-of-stream operations, e.g. set the final offset.
		} catch (IOException e) {
		}  
		
		try {
			tokenStream.close();// Release resources associated with this stream.
		} catch (IOException e) {
		} 
	
		return terms;	
	}

	public static void main(String[] args) {

		
		for(Query query : new TestQueryParse().queryBuilder("ok","park"))
		{
			System.out.println(query.toString());
		}
		
		
	}

}
