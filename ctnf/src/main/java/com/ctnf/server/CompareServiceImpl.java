package com.ctnf.server;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.ctnb.BackendException;
import com.ctnb.BackendServices;
import com.ctnc.shared.CompareRequest;
import com.ctnc.shared.CompareResponse;
import com.ctnf.client.services.CompareService;
import com.ctnu.Spring;
import com.ctnu.environment.Environment;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class CompareServiceImpl extends RemoteServiceServlet implements CompareService {

	private static final long serialVersionUID = 6088898732000958565L;
	
	private Logger logger = Logger.getLogger(CompareServiceImpl.class);

	public void init(ServletConfig config) throws ServletException {
        super.init(config);
        //SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this,config.getServletContext());

       // SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);

    }

	private BackendServices backendServices = Spring.context().getBean(BackendServices.class);
	
	@Override
	public CompareResponse compare(CompareRequest request)
	{	
		this.logger.info("---------------------------------------"+Environment.getLineSeparator());
		this.logger.info("User IP: "+getThreadLocalRequest().getRemoteAddr());
		this.logger.info(">>> "+request);

		CompareResponse response = null;
		try {
			response = this.backendServices.getTickets(request);
		} catch (BackendException e) {
			this.logger.error(request,e);
		}
		
		this.logger.trace(response);
		return response;
	}	
}
