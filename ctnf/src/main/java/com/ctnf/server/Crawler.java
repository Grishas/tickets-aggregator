package com.ctnf.server;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.regex.Pattern;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import com.ctnb.BackendServices;
import com.ctnu.Spring;
import com.ctnu.environment.Environment;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.SilentCssErrorHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class Crawler implements Filter{

	private Logger logger = Logger.getLogger(Crawler.class);

    private static final String CHAR_ENCODING = "UTF-8";

	private BackendServices backendServices = Spring.context().getBean(BackendServices.class);
	
    @Override public void init(FilterConfig config) throws ServletException 
    {
        //SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this,config.getServletContext());
    }
	
    @Override public void destroy() {}
	
    private static class SyncAllAjaxController extends NicelyResynchronizingAjaxController {
    	
        private static final long serialVersionUID = 1L;

        @Override
        public boolean processSynchron(HtmlPage page, WebRequest request, boolean async) {
            return true;
        }
    }
    
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,FilterChain chain)  
	{		
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse)response;
		
        String userAgent = httpRequest.getHeader("User-Agent");


		String queryString = httpRequest.getQueryString();
		
		if ( queryString!=null && queryString.contains("_escaped_fragment_=") ) 
	    {
			String data = null;
			
	    	long start = System.currentTimeMillis();
			
			PrintWriter out = null;
			
			// Encoding needs to be set BEFORE calling response.getWriter()
			httpResponse.setCharacterEncoding(CHAR_ENCODING);
			httpResponse.setHeader("Content-Type", "text/plain; charset=" + CHAR_ENCODING);
			try {
				out = httpResponse.getWriter();
			} catch (IOException e) {
				httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				logger.error(e.getMessage());
				
				this.backendServices.report("Crawler visit with error", 
						"Query string: "+queryString+
						 Environment.getLineSeparator()+ 
						 "User Agent: "+userAgent+
						 Environment.getLineSeparator()+ 
						 "Message: "+e.getMessage());
				
				return;
			}
			
			//Remove from the URL all tokens beginning with _escaped_fragment_= 
			//(Note especially that the = must be removed as well).
			
			queryString = queryString.replace("_escaped_fragment_=","");
			
			String redirectUrl = httpRequest.getRequestURL().toString()+"#!"+queryString;
			
			this.logger.info("Crawler request: "+redirectUrl);
			
			try {
				redirectUrl = URLDecoder.decode(redirectUrl,CHAR_ENCODING);
			} catch (UnsupportedEncodingException e) {
				httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				logger.error(e.getMessage());
				this.backendServices.report("Crawler visit with error", 
						"Query string: "+queryString+
						 Environment.getLineSeparator()+ 
						 "User Agent: "+userAgent+
						 Environment.getLineSeparator()+ 
						 "Message: "+e.getMessage());
				
				return;
			}
			
			//https://tomcat.apache.org/tomcat-5.5-doc/servletapi/javax/servlet/http/HttpServletResponse.html
			
			try {
				
				String key = queryString.replaceAll("[^A-Za-z0-9 ]","-");
				
				if(key.equals(""))
				{
					key = "home";
				}
				
				String pageCacheData = this.backendServices.getPageCache(key);
				
				if(pageCacheData==null)
				{
					data = this.renderPage( redirectUrl );
					out.println( data );
					this.backendServices.addPageToCache(key, data);
				}
				else
				{
					out.println( pageCacheData );
				}
			} 
			catch (IOException | InterruptedException e) {
				logger.error(e.getMessage());
				httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				this.backendServices.report("Crawler visit with error", 
						"Query string: "+queryString+
						 Environment.getLineSeparator()+ 
						 "User Agent: "+userAgent+
						 Environment.getLineSeparator()+ 
						 "Message: "+e.getMessage());
							}
			finally{
				out.close();
			}		
			
			long end = System.currentTimeMillis();
			
			this.logger.info("Crawler request take: "+(end-start));
			
			this.backendServices.report("Crawler visit", 
					"Redirect url: "+redirectUrl+
					 Environment.getLineSeparator()+ 
					 "User Agent: "+userAgent+
					 Environment.getLineSeparator()+ 
					 "Take time: "+(end-start)+					
					 Environment.getLineSeparator()+ 
					 "Data: "+"---");

			return;
		}
		
		try {
			chain.doFilter(request, response);
		} catch (IOException | ServletException e) {
			logger.error(e.getMessage());
			httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
	}

	/**
     * Fetches the page at {@code url} and renders the page in a {@link StringBuilder}. The rendered page is prefixed
     * with a message indicating this is a non-interactive version.
     *
     * @param url The URL of the page to render.
     * @return The rendered page, in a {@link StringBuilder}.
	 * @throws InterruptedException 
     */	
	
    private final long timeoutMillis = 5000;
    private final long jsTimeoutMillis = 2000;
    private final long pageWaitMillis = 100;
    private final long maxLoopChecks = 2;

    private String renderPage(String url) throws IOException, InterruptedException {
    	
    	WebClient webClient = new WebClient(BrowserVersion.FIREFOX_24);

       // webClient.getCache().clear();
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setRedirectEnabled(true);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.setAjaxController(new SyncAllAjaxController());
        webClient.setCssErrorHandler(new SilentCssErrorHandler());

        WebRequest webRequest = new WebRequest(new URL(url), "text/html");
        
        HtmlPage page = webClient.getPage( webRequest );
        
        webClient.getJavaScriptEngine().pumpEventLoop(timeoutMillis);

        int waitForBackgroundJavaScript = webClient.waitForBackgroundJavaScript(jsTimeoutMillis);
        
        int loopCount = 0;

        while (waitForBackgroundJavaScript > 0 && loopCount < maxLoopChecks) 
        {
            ++loopCount;
            waitForBackgroundJavaScript = webClient.waitForBackgroundJavaScript(jsTimeoutMillis);

            if (waitForBackgroundJavaScript == 0) 
            {
    			logger.info("HtmlUnit exits background javascript at loop counter " + loopCount);
                break;
            }

            synchronized (page) 
            {
				logger.info("HtmlUnit waits for background javascript at loop counter " + loopCount);
                page.wait(pageWaitMillis);
            }
        }

        webClient.closeAllWindows();

        String result =  Pattern.compile("<style>.*?</style>", Pattern.DOTALL).matcher(page.asXml().replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "")).replaceAll("");
        
        return result;
   
    }
 
  //https://github.com/ArcBees/GWTP/blob/master/gwtp-crawler-service/src/main/java/com/gwtplatform/crawlerservice/server/CrawlServiceServlet.java

  //bug

  //http://127.0.0.1:8888/Ctn.html#!search:event=disney-live-three-classic-fairy-tales&from=9-oct-15&to=16-oct-15

  //user : 
  //http://127.0.0.1:8888/Ctn.html#!compare:event-name=Laugh%20Factory&date=2015-10-11%2020:30&venue=Laugh%20Factory%20-%20Las%20Vegas&location=Las%20Vegas,NV_US%20Laughlin,NV_US%20&market=ticketcity_1342081

  //robot : 
  //http://127.0.0.1:8888/Ctn.html?_escaped_fragment_=compare:event-name=Laugh%20Factory&date=2015-10-11%2020:30&venue=Laugh%20Factory%20-%20Las%20Vegas&location=Las%20Vegas,NV_US%20Laughlin,NV_US%20&market=ticketcity_1342081


  	
	
//	user : 
//	http://127.0.0.1:8888/Ctn.html#!compare:event-name=Laugh%20Factory&date=2015-10-11%2020:30&venue=Laugh%20Factory%20-%20Las%20Vegas&location=Las%20Vegas,NV_US%20Laughlin,NV_US%20&market=ticketcity_1342081
//
//	robot : 
//	http://127.0.0.1:8888/Ctn.html?_escaped_fragment_=compare:event-name=Laugh%20Factory&date=2015-10-11%2020:30&venue=Laugh%20Factory%20-%20Las%20Vegas&location=Las%20Vegas,NV_US%20Laughlin,NV_US%20&market=ticketcity_1342081

	
	
//	private String contentBuilderHelper(String url_with_hash_fragment)throws InterruptedException, FailingHttpStatusCodeException, MalformedURLException, IOException
//	{
//		final WebClient webClient = new WebClient();
//		
//		//is it good fo seo remove css ???
//        webClient.getOptions().setCssEnabled(false);     
//
//        //http://stackoverflow.com/questions/16754752/java-htmlunit-failing-to-load-javascript
//        java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.OFF);
//        java.util.logging.Logger.getLogger("org.apache.http").setLevel(java.util.logging.Level.OFF);
//
//	    HtmlPage page = webClient.getPage(url_with_hash_fragment);
////	    HtmlPage page = webClient.getPage("http://127.0.0.1:8888/Ctn.html#!compare:event-name=Laugh%20Factory&date=2015-10-11%2020:30&venue=Laugh%20Factory%20-%20Las%20Vegas&location=Las%20Vegas,NV_US%20Laughlin,NV_US%20&market=ticketcity_1342081");
//
//	    // important!  Give the headless browser enough time to execute JavaScript
//	    // The exact time to wait may depend on your application.
//	    webClient.waitForBackgroundJavaScript(20000);
//
//	    System.out.println("@@@ title: "+page.getTitleText());
//	    System.out.println("@@@ as xml: "+page.asXml());	
//		System.out.println("@@@ as text: "+page.asText());
//		
//	    return page.asXml();
//	}

}

















//respond to Googlebot with a 304 ("not modified") code.
		//http://example.com/ set for home in site map
		//home page case  here 
		//set on home html page : <meta name="fragment" content="!">
		//comming request from crawler will be something like this : 
		//http://127.0.0.1:8888/Gwtajaxcrawler.html?_escaped_fragment_=
		//http://compareticketsnow.com?_escaped_fragment_=
		//check it with boot
//		if(query.equals("?_escaped_fragment_="))
//		{
//			
//		}
		
		//http://compareticketsnow.com?_escaped_fragment_=
		//www.example.com/ajax.html?_escaped_fragment_=key=value 
		//www.example.com/ajax.html#!key=value.
		
		//404 case
		//...
		//Warning: Should the content for www.example.com?_escaped_fragment_= 
		//return a 404 code, no content will be indexed for www.example.com! 
		//So, be careful if you add this meta tag to your page and make sure an HTML snapshot is returned.
		
		/*An HTML snapshot is returned, ideally along with a prominent link at the top of the page, 
		  letting end users know that they have reached the _escaped_fragment_ URL in error. 
		  (Remember that _escaped_fragment_ URLs are meant to be used only by crawlers.) 
		  For all requests that do not have an _escaped_fragment_, the server will return content as before.*/
		
		//http://www.sitemaps.org/
		















