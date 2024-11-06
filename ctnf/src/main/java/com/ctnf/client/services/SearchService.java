package com.ctnf.client.services;
import com.ctnc.shared.SearchRequest;
import com.ctnc.shared.SearchResponse;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("search")
public interface SearchService extends RemoteService {
	SearchResponse search(SearchRequest searchRequest);
}