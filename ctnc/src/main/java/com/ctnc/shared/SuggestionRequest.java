package com.ctnc.shared;

import com.ctnc.shared.IndexKey;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.user.client.ui.SuggestOracle.Request;

public class SuggestionRequest extends Request implements IsSerializable{

	private IndexKey runOnSuggesterIndex = null;
	
	public SuggestionRequest(){}
	
	public SuggestionRequest(Request request,IndexKey runOnSuggesterIndex) {
		super(request.getQuery(),request.getLimit());
		this.runOnSuggesterIndex = runOnSuggesterIndex;
	}

	public IndexKey getRunOnSuggesterIndex() {
		return runOnSuggesterIndex;
	}
	public void setRunOnSuggesterIndex(IndexKey runOnSuggesterIndex) {
		this.runOnSuggesterIndex = runOnSuggesterIndex;
	}

}
