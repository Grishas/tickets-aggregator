package com.ctnf.server;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.ctnb.BackendServices;
import com.ctnf.client.services.MaintenanceService;
import com.ctnu.Spring;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class MaintenanceServiceImpl extends RemoteServiceServlet implements MaintenanceService {

	private static final long serialVersionUID = -1391924896562275108L;

	private Logger logger = Logger.getLogger(MaintenanceServiceImpl.class);

//	public void init(ServletConfig config) throws ServletException {
//        super.init(config);
//		SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this,config.getServletContext());
//
//        //SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
//
//      }
	
	//@Autowired
	private BackendServices service = Spring.context().getBean(BackendServices.class);
	
	@Override
	public void report(String title, String message) {
		this.service.report(title,message);
	}
}
