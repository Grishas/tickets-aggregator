package com.ctnb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import com.ctnl.load.fs.LoadConfiguration;
import com.ctns.geocoding.GeocodingConfiguration;
import com.ctns.merge.index.MergeIndexConfiguration;
import com.ctns.suggester.index.SuggesterIndexConfiguration;
import com.ctntc.TCConfiguration;
import com.ctntn.TNConfiguration;
import com.ctnu.UtilsException;
import com.ctnu.config.UtilsConfiguration;
import com.ctnu.crypto.CipherUtils;

@Import({
	SuggesterIndexConfiguration.class,
	MergeIndexConfiguration.class,
	TCConfiguration.class,
	TNConfiguration.class,
	GeocodingConfiguration.class,
	UtilsConfiguration.class,
	LoadConfiguration.class
})
@PropertySource("classpath:backend.configuration")
@EnableScheduling
@Configuration
//https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/scheduling/annotation/EnableScheduling.html
public class BackendConfiguration {

	@Autowired
	private Environment environment;	
	
	@Autowired
	private CipherUtils cipherUtils;
	
	@Scope("singleton")
	@Bean
	public BackendServices getBackendServices(){
		return new BackendServices();
	}
	
	@Scope("singleton")
	@Bean
	public TicketsService getTicketsService(){
		return new TicketsService();
	}
	
	@Bean
	@Scope("singleton")
	public BackendProperties getBackendProperties() throws BackendException{
		long getTicketsTimeout = this.environment.getProperty("ctnb.get.tickets.timeout",Long.class);
		int getTicketsRetryThreshold = this.environment.getProperty("ctnb.get.tickets.retry.threshold",Integer.class);
		long keepTicketsInCacheMs = this.environment.getProperty("ctnb.keep.tickets.in.cache.ms",Long.class);

		boolean enableMokeIp=this.environment.getProperty("ctnb.enable.moke.ip",Boolean.class);
		String mokeIpTarget=this.environment.getProperty("ctnb.moke.ip.target",String.class);
		String[] mokeIpSourceList=this.environment.getProperty("ctnb.moke.ip.source.list",String[].class);
		
		String maintenanceToEmails=this.environment.getProperty("ctnb.maintenance.to.emails",String.class);
		String gmailPassword=this.environment.getProperty("ctnb.gmail.password",String.class);
		boolean sendEmailsEnable=this.environment.getProperty("ctnb.send.emails.enable",Boolean.class);

		try {
			gmailPassword = cipherUtils.decrypt(gmailPassword);
		} catch (UtilsException e) {
			throw new BackendException(e); 
		}
		
		BackendProperties properties = new BackendProperties();	
		properties.setKeepTicketsInCacheMs(keepTicketsInCacheMs);
		properties.setGetTicketsTimeout(getTicketsTimeout);
		properties.setGetTicketsRetryThreshold(getTicketsRetryThreshold);
		properties.setEnableMokeIp(enableMokeIp);
		if(enableMokeIp==true)
		{
			properties.setMokeIpSourceList(mokeIpSourceList);
			properties.setMokeIpTarget(mokeIpTarget);
		}
		properties.setMaintenanceToEmails(maintenanceToEmails);
		properties.setGmailPassword(gmailPassword);
		properties.setSendEmailsEnable(sendEmailsEnable);
		return properties;
	}
	
	@Scope("singleton")
	@Bean(initMethod="initPageCache")
	public PageCacheService getPageCacheService(){
		return new PageCacheService();
	}
	
	
}

