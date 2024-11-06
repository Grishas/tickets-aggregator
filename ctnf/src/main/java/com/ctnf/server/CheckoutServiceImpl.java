package com.ctnf.server;
import org.apache.log4j.Logger;
import com.ctnb.BackendServices;
import com.ctnf.client.services.CheckoutService;
import com.ctnu.Spring;
import com.ctnu.environment.Environment;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class CheckoutServiceImpl extends RemoteServiceServlet implements CheckoutService {

	private static final long serialVersionUID = 742393041610982863L;
	private Logger logger = Logger.getLogger(CheckoutServiceImpl.class);

//	public void init(ServletConfig config) throws ServletException {
//        super.init(config);
//        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this,config.getServletContext());
//        //SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
//    }
	
	//@Autowired
	private BackendServices backendServices = Spring.context().getBean(BackendServices.class);
	
	@Override
	public void checkout( String checkout)
	{	
		this.logger.info("---------------------------------------"+Environment.getLineSeparator());
		this.logger.info("User IP: "+getThreadLocalRequest().getRemoteAddr());
		this.backendServices.checkout(checkout);
	}	
}
