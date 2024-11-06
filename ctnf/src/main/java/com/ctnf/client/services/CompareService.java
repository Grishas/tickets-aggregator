package com.ctnf.client.services;
import com.ctnc.shared.CompareRequest;
import com.ctnc.shared.CompareResponse;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("compare")
public interface CompareService extends RemoteService {
	public CompareResponse compare(CompareRequest compareRequest);
}
