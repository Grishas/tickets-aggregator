package com.ctnf.server;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.ctnb.BackendException;
import com.ctnb.BackendServices;
import com.ctnc.shared.SearchRequest;
import com.ctnc.shared.SearchResponse;
import com.ctnc.shared.UserLocation;
import com.ctnf.client.services.SearchService;
import com.ctnu.Spring;
import com.ctnu.environment.Environment;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class SearchServiceImpl extends RemoteServiceServlet implements SearchService {

	private Logger logger = Logger.getLogger(SearchServiceImpl.class);

	private static final long serialVersionUID = -3093977091304656718L;
	
//	public void init(ServletConfig config) throws ServletException {
//	       super.init(config);
//	      
//	       SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this,config.getServletContext());
//
//          //SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
//	       
//	       
//
//    }
	
	//@Autowired BackendServices backendServices;
	
	private BackendServices backendServices = Spring.context().getBean(BackendServices.class);

	
	@Override
	public SearchResponse search(SearchRequest request) 
	{	
		this.logger.info("---------------------------------------"+Environment.getLineSeparator());
		
		this.logger.info(request);
			
		//resolve in first request of after timeout in client 
		if(request.getCurrentUserLocation()==null)
		{
			UserLocation userLocation = new UserLocation();
			userLocation.setIp(this.getUserIp());
			
			//moke ip if enable for testing.enable simulate user experience
			boolean forceResolveIfNewIp = this.backendServices.mokeIp(userLocation);
			
			//Set user location of not provided .In case of failure set default: ny
			this.backendServices.prepareUserLocation(userLocation,forceResolveIfNewIp);
			
			request.setCurrentUserLocation(userLocation);	
		}
		
		if(request.getSearchLocation()==null)
		{
			request.setSearchLocation(request.getCurrentUserLocation());				
		}
				
		
		
		
		
		
		
		SearchResponse response = null;
		try 
		{
			response = this.backendServices.search(request);
		} 
		catch (BackendException e) 
		{
			logger.error(e);
		}
		
		logger.info("Search request: "+request+", response with: "+response.getTotalResults()+" results");
		logger.trace("Search response : "+response);

		return response;
	}
	
	
	private String getUserIp()
	{
		return getThreadLocalRequest().getRemoteAddr();
	}
}
