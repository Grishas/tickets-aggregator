package com.ctns.collect.index;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.Query;
import org.springframework.beans.factory.annotation.Autowired;

public class CollectIndexService {

	private Logger logger = Logger.getLogger(CollectIndexService.class);

	@Autowired 
	private CollectIndexProperties collectIndexProperties;
	
	@Autowired
	private CollectIndex collectIndex;
	
    public CollectIndexService(){}

	public List<Document> search(Query query) throws CollectIndexException {	
		return this.collectIndex.search(query,1000);
    }
}
