package com.ctnf.server;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import com.ctnb.BackendConfiguration;
import com.ctnu.Spring;

public class ApplicationListener  implements ServletContextListener{

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		Spring.context().close();
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {		
		Spring.set(new AnnotationConfigApplicationContext(BackendConfiguration.class));
	}

}
