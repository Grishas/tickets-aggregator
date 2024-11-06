package com.ctns.collect.index;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.springframework.beans.factory.annotation.Autowired;

public class CollectIndex {

	@Autowired
	private IndexSearcher indexSearcher;

	public List<Document> search(Query query,int maxResults) throws CollectIndexException {
		
		try {
			
			TopDocs topDocs = this.indexSearcher.search(query,maxResults);
			
			List<Document> documents = new ArrayList<Document>(maxResults);
		
			for(ScoreDoc scoreDoc : topDocs.scoreDocs){
				documents.add( this.indexSearcher.doc(scoreDoc.doc));		
			}
			
			return documents;
		} 
		catch (IOException e){
			throw new CollectIndexException(e);
		}
	}
}