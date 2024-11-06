package com.ctns.collect.index;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.language.Metaphone;
import org.apache.commons.codec.language.Soundex;
import org.apache.lucene.search.spell.JaroWinklerDistance;
import org.apache.lucene.search.spell.LevensteinDistance;
import org.apache.lucene.search.spell.LuceneLevenshteinDistance;
import org.apache.lucene.search.spell.NGramDistance;

public class TestDistance {
	
//	http://stackoverflow.com/questions/18008999/how-to-add-custom-stop-words-using-lucene-in-java
//	http://stackoverflow.com/questions/6122545/stop-words-and-stemmer-in-java
	    	
	public static void main(String[] args) {

		LevensteinDistance  		levensteinDistance 			= new LevensteinDistance();
		LuceneLevenshteinDistance 	luceneLevenshteinDistance 	= new LuceneLevenshteinDistance(); 
		NGramDistance 				ngramDistance 				= new NGramDistance();
		JaroWinklerDistance			jaroWinklerDistance 		= new JaroWinklerDistance();
//		---
//		new york mets
//		new york jets
//		distance=0.98422086
//		---
//		---
//		san diego state aztecs football
//		san diego state aztecs basketball
//		distance=0.97131276
//		---

		
		String one = "york mets";
		String two = "york met";
		
		float a = levensteinDistance.getDistance(one,two);
		float b = luceneLevenshteinDistance.getDistance(one,two);
		float c = ngramDistance.getDistance(one, two);
		float d = jaroWinklerDistance.getDistance(one, two);

		System.out.println(a+"-->LevensteinDistance");
		System.out.println(b+"-->LuceneLevenshteinDistance");
		System.out.println(c+"-->NGramDistance");
		System.out.println(d+"-->JaroWinklerDistance");
		
		
		
	}

}
