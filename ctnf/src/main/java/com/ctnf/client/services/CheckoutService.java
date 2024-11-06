package com.ctnf.client.services;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("checkout")
public interface CheckoutService extends RemoteService {
	void checkout( String checkout);
}
