package com.ctnc;
import java.util.Set;
import com.ctnc.shared.IndexKey;

public class SuggesterDto {

	private long triggerEventInternalId = 0;
	private String rawKeyword = null;
	private String keyword = null;
	private String query = null;
	private long weight = 0;
	private IndexKey indexKey;
	private Set<String> keywords = null;
	private Set<Long> events = null;
	private Set<String> segmentedKeys = null;
	
	public IndexKey getIndexKey() {
		return indexKey;
	}
	public void setIndexKey(IndexKey indexKey) {
		this.indexKey = indexKey;
	}
	public long getTriggerEventInternalId() {
		return triggerEventInternalId;
	}
	public void setTriggerEventInternalId(long triggerEventInternalId) {
		this.triggerEventInternalId = triggerEventInternalId;
	}
	public String getRawKeyword() {
		return rawKeyword;
	}
	public void setRawKeyword(String rawKeyword) {
		this.rawKeyword = rawKeyword;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public long getWeight() {
		return weight;
	}
	public void setWeight(long weight) {
		this.weight = weight;
	}
	public Set<String> getKeywords() {
		return keywords;
	}
	public void setKeywords(Set<String> keywords) {
		this.keywords = keywords;
	}
	public Set<Long> getEvents() {
		return events;
	}
	public void setEvents(Set<Long> events) {
		this.events = events;
	}
	public Set<String> getSegmentedKeys() {
		return segmentedKeys;
	}
	public void setSegmentedKeys(Set<String> segmentedKeys) {
		this.segmentedKeys = segmentedKeys;
	}
	@Override
	public String toString() {
		return "Suggester [triggerEventInternalId=" + triggerEventInternalId
				+ ", rawKeyword=" + rawKeyword + ", keyword=" + keyword
				+ ", query=" + query + ", weight=" + weight + ", indexKey=" + indexKey
				+ ", events=" + events + ", segmentedKeys=" + segmentedKeys
				+ ", keywords=" + keywords + "]";
	}
}
