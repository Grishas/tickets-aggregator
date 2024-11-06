package com.ctns.run;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.BooleanClause.Occur;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.ctnc.Constants;
import com.ctns.collect.index.CollectIndexConfiguration;
import com.ctns.collect.index.CollectIndexException;
import com.ctns.collect.index.CollectIndexService;

public class Cli_SearchIndexTest {

	private void run()
	{
		AnnotationConfigApplicationContext context = 
				new AnnotationConfigApplicationContext(CollectIndexConfiguration.class);

		CollectIndexService searchService = context.getBean(CollectIndexService.class);
		
//		BooleanQuery booleanQuery = new BooleanQuery();
//        TermQuery tq1 = new TermQuery(new Term("name", "king"));
//        TermQuery tq2 = new TermQuery(new Term("name", "mma"));
//        
//        booleanQuery.add(tq1, Occur.MUST);
//        booleanQuery.add(tq2, Occur.MUST);
        
		QueryParser parser = new QueryParser("name",new EnglishAnalyzer());

        Query query = null;
		try {
			query = parser.parse(
					"+cityName:auburn +venueName:jordan +venueName:hare +venueName:stadium +segmentKey:usal +((+name:auburn +name:tiger +name:vs +name:georgia +name:bulldog) (+performerName:auburn +performerName:tiger +performerName:georgia +performerName:bulldog)) -name:park");

			
//			query = parser.parse(
//					"+name:parking");
		
		} catch (ParseException e1) {
			
		}

		try {
			
			for(Document document : searchService.search(query))
			{
				System.out.println(document.getField(Constants.EVENT_NAME));
				
			}
			
		} catch (CollectIndexException e) {
			e.printStackTrace();
		}
		
		context.close();	
	}
	
	public static void main(String[] args) {
		new Cli_SearchIndexTest().run();
	}
}
