package com.ctns.merge.index;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.Query;
import org.springframework.beans.factory.annotation.Autowired;
import com.ctnc.shared.IndexKey;
import com.ctns.query.QueryBuilder;
import com.ctns.query.QueryBuilderException;

public class MergeIndexService {

	private Logger logger = Logger.getLogger(MergeIndexService.class);

	@Autowired 
	private MergeIndexProperties properties;
	
	@Autowired
	private MergeIndex mergeIndex;
	
	@Autowired
	private QueryBuilder queryBuilder;
	
    public MergeIndexService(){}

	public List<Document> search(Query query,int maxResults) throws MergeIndexException {	
		return this.mergeIndex.search(query,maxResults);
    }
	
	public MergeIndexResponse pagingSearch(
			int pageSize ,int startRange ,String query,Date from, Date to, String city,String segmentKey, IndexKey searchIndexKey) throws MergeIndexException {	
    
		Query query_ = null;
		try {
			query_ = this.queryBuilder.buildQuery(searchIndexKey,query,from,to,city,segmentKey);
			logger.info(query_);
		} catch (QueryBuilderException e) {
			throw new MergeIndexException(e);
		}
		
		return this.mergeIndex.pagingSearch(pageSize, startRange, query_);
	}
	
	public List<Document> search(String query) throws MergeIndexException {	
	    
		Query query_;
		try {
			query_ = this.queryBuilder.buildQuery("name", query);
		} catch (QueryBuilderException e) {
			throw new MergeIndexException(e);
		}
		
		return this.search( query_ ,1000);
	}
	
	public MergeIndexResponse pagingSearch(
			int pageSize ,int startRange ,
			String query,Date from, Date to, IndexKey searchIndexKey,
			double latitude, double longitude) throws MergeIndexException {	
    
		MergeIndexResponse response = null;
		//each step will increase radius of search
		for(Integer maxDistanceKmStep : this.properties.getMaxDistanceKmSteps())
		{
			Query query_=null;
			try {
				query_ = this.queryBuilder.buildQuery(
						maxDistanceKmStep , query , from , to , searchIndexKey , latitude ,  longitude );
				
				logger.info(query_);

			} catch (QueryBuilderException e) {
				throw new MergeIndexException(e);
			}
			
			response =  this.mergeIndex.pagingSearch(pageSize, startRange,latitude,longitude,query_);
			
			if(response.getTotalHits()>0)
			{
				return response;
			}
			else
			{
				continue;
			}
		}
		
		return response;
	}
	
	public MergeIndexResponse pagingSearch(int pageSize ,int startRange,double latitude, double longitude) throws MergeIndexException {	
    
		MergeIndexResponse response = null;
		//each step will increase radius of search
		for(Integer maxDistanceKmStep : this.properties.getMaxDistanceKmSteps())
		{
			Query query_=null;
			try 
			{
				query_ = this.queryBuilder.buildQuery(maxDistanceKmStep , latitude ,  longitude );
				
				logger.info(query_);

			} catch (QueryBuilderException e) {
				throw new MergeIndexException(e);
			}
			
			response =  this.mergeIndex.pagingSearch(pageSize, startRange,latitude,longitude,query_);
			
			if(response.getTotalHits()>0)
			{
				return response;
			}
			else
			{
				continue;
			}
		}
		
		return response;
	}

	
	
	
	
	
	
	
	
}
