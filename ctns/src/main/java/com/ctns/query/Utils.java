package com.ctns.query;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

public class Utils {

	public static String stamp(String key,String value) throws QueryBuilderException
	{
		TokenStream tokenStream = null;
		try {
			tokenStream = new EnglishAnalyzer().tokenStream(key,new StringReader(value));
		} catch (IOException e) {
			throw new QueryBuilderException(e);
		}
	
		CharTermAttribute cattr = tokenStream.addAttribute(CharTermAttribute.class);
		try {
			tokenStream.reset(); // Resets this stream to the beginning. (Required)
		} catch (IOException e) {
			throw new QueryBuilderException(e);
		}

		StringBuilder result = new StringBuilder();
		
		try {
			while (tokenStream.incrementToken() ) {
				result.append(cattr.toString()+" ");
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
	
		return result.toString().trim();	
	}
	
	public static String getVenueKey(String segmentedKey,String city,String venueName) throws QueryBuilderException{
		return 
			Utils.stamp("field",venueName)+
			"#"+segmentedKey+
			"#"+Utils.stamp("field",city);
	}
	
	public static String getLocationKey(String segmentedKey,String city) throws QueryBuilderException{
		return 
			segmentedKey+
			"#"+Utils.stamp("field",city);
	}
	
	public static Set<String> removeDuplicateWords(String text) throws QueryBuilderException
	{
		String stamp = stamp("key",text);
		
		String[] words = stamp.split("\\s+"); 
		
		Set<String> result = new HashSet<String>(4);
				
		for(String word : words)
		{ 
			result.add(word);
		} 
		
		return result;
	}
	
	
	
	public static void main(String[] args) {
		/*
		
		test me 
		
		...
		
		Arkansas State University Convocation Center-->ASU Convocation Center
		Jonesboro,AR,US

		
		Tabernacle Atlanta
		Atlanta,GA,US

		The Tabernacle - GA
		Atlanta,GA,US
		
		*/
		
		try {
			System.out.println(getVenueKey("us_ga", "Atlanta","The Tabernacle - GA"));
		} catch (QueryBuilderException e) {
			e.printStackTrace();
		}
		
		
		
		
		
//		try {
//			System.out.println(getLocationKey("us_sc", "North Charleston"));
//		} catch (EventsQueryException e) {
//			e.printStackTrace();
//		}
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
}
