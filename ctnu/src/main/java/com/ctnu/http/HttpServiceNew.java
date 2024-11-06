package com.ctnu.http;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import com.ctnu.UtilsException;

//http://hc.apache.org/httpcomponents-client-4.3.x/examples.html
public class HttpServiceNew 
{
	private Logger logger = Logger.getLogger(HttpServiceNew.class);
	
	// Create an HttpClient with the ThreadSafeClientConnManager.
    // This connection manager must be used if more than one thread will
    // be using the HttpClient.
	private PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = null;
 	
	private CloseableHttpClient closeableHttpClient = null;
 
	public void init()
	{
		this.poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager();
    	this.poolingHttpClientConnectionManager.setMaxTotal(100);

    	this.closeableHttpClient = 
    			HttpClients.custom().setConnectionManager(this.poolingHttpClientConnectionManager).build();
	}
	
    public void destroy(){
    	try {
    		this.closeableHttpClient.close();
		} 
    	catch (IOException e) {
    		this.logger.error(e);
    	}
    }

    
    
    
	public CtnHttpResponse execute(CtnHttpRequest httpRequest) throws UtilsException
	{ 
		//debug
		logger.debug("----------------------------------------------");
		logger.debug("Execute http request");
		logger.debug("charset: "+httpRequest.getCharset());
		logger.debug("soap action: "+httpRequest.getSoapAction());
		logger.debug("soap header: "+httpRequest.getSoapHeader());
		logger.debug("uri: "+httpRequest.getUri());
		logger.debug("soap body: "+httpRequest.getSoapBody());

		HttpPost httppost = new HttpPost( httpRequest.getSoapAction() );
		try
		{
			httppost.setURI(new URI( httpRequest.getUri() ));
		}
		catch (URISyntaxException e) 
		{
			logger.error(e);
			throw new UtilsException(e);
		}
		
		StringEntity stringEntity = null;	
		try 
		{
			stringEntity = new StringEntity( httpRequest.getSoapBody() );
		} 
		catch (UnsupportedEncodingException e) 
		{
			logger.error(e);
			throw new UtilsException(e);
		}
		
		stringEntity.setContentType( httpRequest.getSoapHeader() );

		httppost.setEntity( stringEntity );
			
		CloseableHttpResponse closeableHttpResponse = null;
			
		long start = System.currentTimeMillis();
		
		byte[] data = null;
		
		try 
		{
			closeableHttpResponse = this.closeableHttpClient.execute(httppost);
					        
			HttpEntity httpEntity = closeableHttpResponse.getEntity();
	        
			if (httpEntity != null)
			{
				try 
				{
					data = EntityUtils.toByteArray(httpEntity);
		            logger.info("Read byte length : "+data.length);
				} 
				catch (IOException e) 
				{
					logger.error(e);
					throw new UtilsException(e);
				}
	          } 
			  else
			  {
				logger.error("httpEntity is null");
				throw new UtilsException("httpEntity is null");
			  }				
		}
		catch (ClientProtocolException e) 
		{
			logger.error(e);
			throw new UtilsException(e);
		}
		catch (IOException e) 
		{
			logger.error(e);
			throw new UtilsException(e);
		}
		finally
		{
			try {
				closeableHttpResponse.close();
			} catch (IOException e) {
				logger.error(e);
			}
		}
		
		CtnHttpResponse httpResponse = new CtnHttpResponse();
		httpResponse.setData(data);
		httpResponse.setStatusLine(closeableHttpResponse.getStatusLine().toString());
		httpResponse.setStatusCode(closeableHttpResponse.getStatusLine().getStatusCode());
		httpResponse.setHttpRequest(httpRequest);
		
		logger.debug(httpResponse);
		
		long end = System.currentTimeMillis();
		
		logger.info("HTTP request execute take : "+(end-start)+" ms.Status "+closeableHttpResponse.getStatusLine()+".Request: "+httpRequest.getSoapBody());

	    return httpResponse;
	  }
}
