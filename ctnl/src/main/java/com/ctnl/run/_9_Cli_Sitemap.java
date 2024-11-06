package com.ctnl.run;
import java.io.File;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import com.ctnc.SuggesterDto;
import com.ctnc.shared.IndexKey;
import com.ctnl.load.fs.LoadConfiguration;
import com.ctnl.load.fs.LoadException;
import com.ctnl.load.fs.LoadService;
import com.ctns.suggester.index.build.SuggesterIndexBuildConfiguration;
import com.ctns.suggester.index.build.SuggesterIndexBuildException;
import com.ctns.suggester.index.build.SuggesterIndexBuildService;
import com.redfin.sitemapgenerator.ChangeFreq;
import com.redfin.sitemapgenerator.SitemapIndexGenerator;
import com.redfin.sitemapgenerator.SitemapIndexGenerator.Options;
import com.redfin.sitemapgenerator.W3CDateFormat;
import com.redfin.sitemapgenerator.W3CDateFormat.Pattern;
import com.redfin.sitemapgenerator.WebSitemapGenerator;
import com.redfin.sitemapgenerator.WebSitemapUrl;

public class _9_Cli_Sitemap {

	private Logger logger = Logger.getLogger(_9_Cli_Sitemap.class);
	
	private AnnotationConfigApplicationContext context = null;
	private W3CDateFormat dateFormat = null;
	private org.springframework.core.env.Environment environment = null;
	private File rootDir = null;
	private File mainSiteMapXml = null;
	private final static String rootDomain = "https://www.compareticketsnow.com";
	private final static String siteMapXml = "sitemap.xml";
	
	private void init()
	{
		this.context = new AnnotationConfigApplicationContext(LoadConfiguration.class);
		
		//date format
		this.dateFormat = new W3CDateFormat(Pattern.DAY); 
		this.dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		
		//path
		this.environment = context.getBean(org.springframework.core.env.Environment.class);
		this.rootDir = new File(environment.getProperty("dataRootPath"));
		
		this.mainSiteMapXml = new File(environment.getProperty("dataRootPath")+File.separator+siteMapXml);
	}
	
	private void buildSiteMap(IndexKey indexKey)
	{
		//Load events from FS - Suggester
		LoadService loadService = context.getBean(LoadService.class);

		List<SuggesterDto> suggesters = null;
		try {
			suggesters = loadService.getSuggesters(indexKey);
		} catch (LoadException e) {
			logger.fatal(e);
			return;
		}
		
		WebSitemapGenerator wsg = null;
		try {
			wsg = WebSitemapGenerator.builder(rootDomain, rootDir)
					.dateFormat(dateFormat)
					.autoValidate(true)
					.fileNamePrefix(indexKey.name())
					.build();
		} catch (MalformedURLException e) {
			logger.fatal(e);
			return;
		}
		
		for(SuggesterDto suggesterDto : suggesters)
		{
			try {
				
				WebSitemapUrl url = 
						new WebSitemapUrl.Options(rootDomain+"/#!search:"+indexKey.name()+"="+
						suggesterDto.getKeyword().replace(" ","-").replace("&","-").toLowerCase())
					    .lastMod(new Date())
					    .priority(1.0)
					    .changeFreq(ChangeFreq.WEEKLY)
					    .build();
				
				wsg.addUrl(url);
				
			} catch (MalformedURLException e) {
				logger.fatal(e);
				return;
			} 
		}
		
		wsg.write();
		
		return;
	}
	
	private void mergeSiteMap() {
		
		
		SitemapIndexGenerator sig = null;
	
		try {
			sig =  new SitemapIndexGenerator(rootDomain, mainSiteMapXml);
			sig.addUrl(rootDomain+"/"+IndexKey.performer.name()+".xml");
			sig.addUrl(rootDomain+"/"+IndexKey.venue.name()+".xml");

		} catch (MalformedURLException e) {
			logger.fatal(e);
			return;
		}
		
		sig.write();
	}
	
	private void run()
	{
		this.init();
		
		buildSiteMap(IndexKey.performer);
		buildSiteMap(IndexKey.venue);
		mergeSiteMap();
		
		context.close();					
	}
	
	

	public static void main(String[] args) {
		new _9_Cli_Sitemap().run();
	}
}
