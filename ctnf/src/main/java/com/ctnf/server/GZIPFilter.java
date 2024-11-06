package com.ctnf.server;
import java.io.File;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

//@WebFilter( filterName = "GZIPFilter", urlPatterns = "/*", asyncSupported = true )
public class GZIPFilter implements Filter{

	private Logger logger = Logger.getLogger(GZIPFilter.class);
    @Override public void init(FilterConfig config) throws ServletException {this.filterConfig = config;};
    @Override public void destroy() {this.filterConfig = null;}

    private FilterConfig filterConfig = null;

    private static final String GZIP_EXTENSION = ".gz";

    @Override
    public void doFilter( final ServletRequest servletRequest,
                          final ServletResponse servletResponse,
                          final FilterChain filterChain )
      throws IOException, ServletException
    {
      final HttpServletRequest request = (HttpServletRequest) servletRequest;
      final HttpServletResponse response = (HttpServletResponse) servletResponse;

    if ( !acceptsGzip( request ) )
    {
      filterChain.doFilter( request, response );
    }
    else
    {
      final String resourcePath = request.getServletPath();
      final String realPath = request.getServletContext().getRealPath( resourcePath );
      final File file = null == realPath ? null : new File( realPath );
      if ( null == file ||
           resourcePath.endsWith( GZIP_EXTENSION ) ||
           !file.isFile() )
      {
        filterChain.doFilter( request, response );
      }
      else
      {
        final String gzippedPath = realPath + GZIP_EXTENSION;
        final File gzippedFile = new File( gzippedPath );

        if ( !gzippedFile.isFile() )
        {
          filterChain.doFilter( request, servletResponse );
        }
        else
        {
          final RequestDispatcher dispatcher =
            request.getServletContext().getRequestDispatcher( resourcePath + GZIP_EXTENSION );
          response.setHeader( "Content-Encoding", "gzip" );
          final String mimeType = servletRequest.getServletContext().getMimeType( resourcePath );
          if ( null != mimeType )
          {
            response.setHeader( "Content-Type", mimeType );
          }
          dispatcher.include( request, response );
        }
      }
    }
  }

  private boolean acceptsGzip( final HttpServletRequest request )
  {
    final String header = request.getHeader( "Accept-Encoding" );
    return null != header && header.contains( "gzip" );
  }
    
    
    
//	@Override
//	public void doFilter(ServletRequest request, ServletResponse response,FilterChain chain)  
//	{		
//		HttpServletRequest httpRequest = (HttpServletRequest) request;
//		HttpServletResponse httpResponse = (HttpServletResponse)response;
//
//		String requestUrl = httpRequest.getRequestURI();
//        
//		logger.info("requestUrl : "+requestUrl);
//		
//		//skip gz files
//		if( ! requestUrl.endsWith(".gz"))
//		{
//			//check gzip support by browser
//			String accesptEncoding = httpRequest.getHeader("accept-encoding");
//			if(accesptEncoding!=null && accesptEncoding.indexOf("gzip")!=-1)
//			{
//				//forwar to gz file 
//				try
//				{
//					RequestDispatcher requestDispatcher = 
//							this.filterConfig.getServletContext().getRequestDispatcher(requestUrl + ".gz");
//
//					httpResponse.setHeader( "Content-Encoding", "gzip" );
//					
//					final String mimeType = httpRequest.getServletContext().getMimeType( requestUrl );
//			        if ( null != mimeType )
//			        {
//			        	httpResponse.setHeader( "Content-Type", mimeType );
//			        }
//
//					requestDispatcher.forward(request, response);
//					
//					return;
//				}
//				catch(ServletException |IOException e){
//					logger.error(e);
//				}
//			}
//		}
//		
//		try {
//			chain.doFilter(request, response);
//		} catch (IOException | ServletException e) {
//			logger.error(e);
//		}
//	}
}
