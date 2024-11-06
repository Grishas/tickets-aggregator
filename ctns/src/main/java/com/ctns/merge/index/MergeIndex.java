package com.ctns.merge.index;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.expressions.Expression;
import org.apache.lucene.expressions.SimpleBindings;
import org.apache.lucene.expressions.js.JavascriptCompiler;
import org.apache.lucene.facet.DrillDownQuery;
import org.apache.lucene.facet.DrillSideways;
import org.apache.lucene.facet.FacetResult;
import org.apache.lucene.facet.Facets;
import org.apache.lucene.facet.FacetsCollector;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.facet.DrillSideways.DrillSidewaysResult;
import org.apache.lucene.facet.range.DoubleRange;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.FieldDoc;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.BooleanClause.Occur;
import org.springframework.beans.factory.annotation.Autowired;

import com.ctnc.Constants;
import com.ctnc.shared.IndexKey;
import com.ctns.query.QueryBuilderException;

import org.apache.lucene.facet.range.DoubleRangeFacetCounts;
import org.apache.lucene.facet.taxonomy.TaxonomyReader;
import org.apache.lucene.queries.function.ValueSource;

public class MergeIndex {

	private Logger logger = Logger.getLogger(MergeIndex.class);

	@Autowired
	private IndexSearcher index;
	
	@Autowired
	private Sort sort;

	public MergeIndexResponse pagingSearch(int pageSize ,int startRange ,Query query)throws MergeIndexException {
				
		MergeIndexResponse mergeIndexResponse = new MergeIndexResponse();
		
		TopDocs topDocs;
		try {
			topDocs = this.index.search( query, ( pageSize + startRange ), this.sort );
		} catch (IOException e1) {
			throw new MergeIndexException(e1);
		}
		
		mergeIndexResponse.setTotalHits( topDocs.totalHits );
		
		this.logger.debug("---Paging search on merge index---");
		this.logger.debug("page size-------------"+pageSize);
		this.logger.debug("start range-----------"+startRange);
		this.logger.debug("query-----------------"+query);
		this.logger.debug("top docs total hits---"+topDocs.totalHits);
		
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		
		List<Document> results = new ArrayList<Document>( pageSize );
		
		for(int index=startRange; index<(startRange+pageSize);index++)
		{
			if( index == topDocs.totalHits ){
				break;
			}
			
			try {
				results.add(this.index.doc(scoreDocs[index].doc));
				mergeIndexResponse.getTest().add(String.valueOf(index));
				
			} catch (IOException e) {
				throw new MergeIndexException(e);
			}
		}
		
		mergeIndexResponse.setDocuments(results);
		
		return mergeIndexResponse;
	}
	
	public List<Document> search(Query query,int maxResults) throws MergeIndexException {
		
		try {
			
			TopDocs topDocs = this.index.search(query,maxResults);
			
			List<Document> documents = new ArrayList<Document>( maxResults );
		
			for(ScoreDoc scoreDoc : topDocs.scoreDocs)
			{
				this.explain(query,scoreDoc.doc);
				
				documents.add( this.index.doc( scoreDoc.doc ) );		
			}
			
			return documents;
		} 
		catch (IOException e){
			throw new MergeIndexException(e);
		}
	}
	
	
	
	
	
	
	
	
	private void explain(Query query, int docId) throws MergeIndexException{
		try {
			for(Explanation explanation :  index.explain(query,docId).getDetails()){
				this.logger.debug(explanation);
			}
		} catch (IOException e) {
			throw new MergeIndexException(e);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	//----------------------geocoding
	
	/*User runs a query and counts facets. */
	public FacetResult search(double latitude, double longitude,Query query) throws IOException {
	
	    FacetsCollector fc = new FacetsCollector();
	
	    this.index.search(query/*new MatchAllDocsQuery()*/, fc);
	
	    Facets facets = new DoubleRangeFacetCounts(
	    		"field",
	    		getDistanceValueSource(latitude,longitude), 
	    		fc,
	            ONE_KM,TWO_KM,FIVE_KM,DRIL_DOWN_KM);
	
	    FacetResult facetResult = facets.getTopChildren(10, "field");
	    
	    System.out.println(facetResult);
	    
	    return facetResult;
	  }
	
	private final DoubleRange ONE_KM = new DoubleRange("< 1 km", 0.0, true, 1.0, false);
	final DoubleRange TWO_KM = new DoubleRange("< 2 km", 0.0, true, 2.0, false);
	final DoubleRange FIVE_KM = new DoubleRange("< 5 km", 0.0, true, 5.0, false);
	
	private final FacetsConfig config = new FacetsConfig();

	/** Radius of the Earth in KM
	* NOTE: this is approximate, because the earth is a bit
	* wider at the equator than the poles.  See
	* http://en.wikipedia.org/wiki/Earth_radius */
	public final static double EARTH_RADIUS_KM = 6371.01;

	public ValueSource getDistanceValueSource(double latitude, double longitude) 
	{
		Expression distance = null;
		
		try 
		{
			distance = 
					JavascriptCompiler.compile("haversin(" + latitude + "," + longitude + ",latitude,longitude)");
		} 
		catch (ParseException pe) 
		{
			// Should not happen
		    throw new RuntimeException(pe);
		}
		
		SimpleBindings bindings = new SimpleBindings();
		bindings.add(new SortField(Constants.LATITUDE, SortField.Type.DOUBLE));
		bindings.add(new SortField(Constants.LONGITUDE, SortField.Type.DOUBLE));
		
		return distance.getValueSource(bindings);
	}
	
	
	
	
	
	
	
	
	
	

	//+geo
	final DoubleRange DRIL_DOWN_KM = new DoubleRange("< 400 km", 0.0, true, 50.0, false);

	public MergeIndexResponse pagingSearch(int pageSize ,int startRange ,double latitude, double longitude,Query query) throws MergeIndexException {	
    

		TopDocs topDocs = null;
			try {
				topDocs = this.drillDown(DRIL_DOWN_KM,(pageSize+startRange), latitude, longitude, query);
			} catch (IOException e) {
				throw new MergeIndexException(e);
			}
		
			
			MergeIndexResponse response = new MergeIndexResponse();

			response.setTotalHits( topDocs.totalHits );
			
			this.logger.debug("---Paging search on merge index---");
			this.logger.debug("page size-------------"+pageSize);
			this.logger.debug("start range-----------"+startRange);
			this.logger.debug("query-----------------"+query);
			this.logger.debug("top docs total hits---"+topDocs.totalHits);
			
			ScoreDoc[] scoreDocs = topDocs.scoreDocs;
			
			List<Document> results = new ArrayList<Document>( pageSize );
			
			for(int index=startRange; index<(startRange+pageSize);index++)
			{
				if( index == topDocs.totalHits ){
					break;
				}
				
				try {
					results.add(this.index.doc(scoreDocs[index].doc));
					response.getTest().add(String.valueOf(index));
					
				} catch (IOException e) {
					throw new MergeIndexException(e);
				}
			}
			
			response.setDocuments(results);
			
			return response;
	}
	
	
	
	
	 /* User drills down on the specified range. */
	  private TopDocs drillDown(final DoubleRange range,int size,double latitude, double longitude,Query query) throws IOException 
	  {
	    // Passing no baseQuery means we drill down on all
	    // documents ("browse only"):
	    DrillDownQuery q = new DrillDownQuery(null);
	    q.add("field", query );
	    
	    final ValueSource vs = getDistanceValueSource(latitude,longitude);
	    
	    DrillSideways ds = new DrillSideways(this.index , config, (TaxonomyReader) null) 
	    {
	        @Override
	        protected Facets buildFacetsResult(
	        		FacetsCollector drillDowns, FacetsCollector[] drillSideways, String[] drillSidewaysDims) throws IOException 
	        {        
	          assert drillSideways.length == 1;
	          
	          return new DoubleRangeFacetCounts("field", vs, drillSideways[0], range );
	        }  
	     };
	     
	     
	     /**
	      * Search, sorting by {@link Sort}, and computing
	      * drill down and sideways counts.
	      */
//	     public DrillSidewaysResult search(DrillDownQuery query,
//	                                       Filter filter, FieldDoc after, int topN, Sort sort, boolean doDocScores,
//	                                       boolean doMaxScore) throws IOException {
	     
	     
	     
	     
	     
	     
	    // TopDocs td = ds.search(q,size).hits;
	     DrillSidewaysResult td = ds.search(q,null,null,size,sort,true,true);
	     return td.hits;
	  }
}