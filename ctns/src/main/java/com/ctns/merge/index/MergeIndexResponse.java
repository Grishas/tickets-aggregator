package com.ctns.merge.index;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.document.Document;

public class MergeIndexResponse {

	private int totalHits = 0;

	private List<String> test = new ArrayList<String>();
	private List<Document> documents = null;
			
//	private String city,segmentKey;
//	
//	public String getCity() {
//		return city;
//	}
//	public void setCity(String city) {
//		this.city = city;
//	}
//
//	public String getSegmentKey() {
//		return segmentKey;
//	}
//
//	public void setSegmentKey(String segmentKey) {
//		this.segmentKey = segmentKey;
//	}

	public int getTotalHits() {
		return totalHits;
	}

	public List<String> getTest() {
		return test;
	}

	public void setTest(List<String> test) {
		this.test = test;
	}

	public void setTotalHits(int totalHits) {
		this.totalHits = totalHits;
	}

	public List<Document> getDocuments() {
		return documents;
	}

	public void setDocuments(List<Document> documents) {
		this.documents = documents;
	}
	
	
	

}
