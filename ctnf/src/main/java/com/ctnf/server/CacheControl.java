package com.ctnf.server;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

//http://seewah.blogspot.co.il/2009/02/gwt-tips-2-nocachejs-getting-cached-in.html
public class CacheControl implements Filter{

	private Logger logger = Logger.getLogger(CacheControl.class);

    @Override public void init(FilterConfig arg0) throws ServletException {}
	@Override public void destroy() {}
	
	
	public static final int YEAR_IN_MINUTES = 365 * 24 * 60 * 60;

	
	  @Override
	  public void doFilter( final ServletRequest request,
	                        final ServletResponse response,
	                        final FilterChain filterChain )
	    throws IOException, ServletException
	  {

	    final HttpServletRequest httpRequest = (HttpServletRequest) request;
	    final HttpServletResponse httpResponse = (HttpServletResponse) response;
	    final String requestURI = httpRequest.getRequestURI();

	    if ( requestURI.contains( ".nocache." ) )
	    {
	      final Date now = new Date();
	      // set create date to current timestamp
	      httpResponse.setDateHeader( "Date", now.getTime() );
	      // set modify date to current timestamp
	      httpResponse.setDateHeader( "Last-Modified", now.getTime() );
	      // set expiry to back in the past (makes us a bad candidate for caching)
	      httpResponse.setDateHeader( "Expires", 0 );
	      // HTTP 1.0 (disable caching)
	      httpResponse.setHeader( "Pragma", "no-cache" );
	      // HTTP 1.1 (disable caching of any kind)
	      // HTTP 1.1 'pre-check=0, post-check=0' => (Internet Explorer should always check)
	      //Note: no-store is not included here as it will disable offline application storage on Firefox
	      httpResponse.setHeader( "Cache-control", "no-cache, must-revalidate, pre-check=0, post-check=0" );
	    }
	    else if ( requestURI.contains( ".cache." ) )
	    {
	      // set expiry to back in the past (makes us a bad candidate for caching)
	      final Calendar calendar = Calendar.getInstance();
	      calendar.setTime( new Date() );
	      calendar.add( Calendar.YEAR, 1 );
	      httpResponse.setDateHeader( "Expires", calendar.getTime().getTime() );
	      httpResponse.setHeader( "Cache-control", "max-age=" + YEAR_IN_MINUTES + ", public" );
	      httpResponse.setHeader( "Pragma", "" );
	    }

	    filterChain.doFilter( request, response );
	  }
	
	}
	
	
	
//	@Override
//	public void doFilter(ServletRequest request, ServletResponse response,FilterChain chain) throws IOException, ServletException  
//	{
//		HttpServletRequest httpRequest = (HttpServletRequest) request;
//		
//		String requestURI = httpRequest.getRequestURI();
//
//		if( requestURI.contains(".nocache.") ) 
//		{
//		   this.logger.info("requestURI.contains(\".nocache.\")");
//		   
//		   Date now = new Date();
//		   HttpServletResponse httpResponse = (HttpServletResponse) response;
//		   httpResponse.setDateHeader("Date", now.getTime());
//		   
//		   // one day old
//		   httpResponse.setDateHeader("Expires", now.getTime() - 86400000L);
//		   
//		   httpResponse.setHeader("Pragma", "no-cache");
//		   
//		   httpResponse.setHeader("Cache-control", "no-cache, no-store, must-revalidate");
//		 }
//
//		 chain.doFilter(request, response);
//	}
