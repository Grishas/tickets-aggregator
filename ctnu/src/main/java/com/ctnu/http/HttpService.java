package com.ctnu.http;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import com.ctnu.UtilsException;

//http://hc.apache.org/httpcomponents-client-4.3.x/examples.html
//http://hc.apache.org/httpcomponents-client-4.3.x/httpclient/examples/org/apache/http/examples/client/ClientChunkEncodedPost.java
	
public class HttpService 
{
	private Logger logger = Logger.getLogger(HttpService.class);

	public CtnHttpResponse execute(final CtnHttpRequest httpRequest) throws UtilsException
	{
		long start = System.currentTimeMillis();
		
		logger.debug(httpRequest);
		
		CloseableHttpClient httpclient = null;
        CtnHttpResponse httpResponse = null;

        try {
        	
    		httpclient = HttpClients.createDefault();
    		
	    	HttpPost httppost = new HttpPost( httpRequest.getSoapAction() );
			try{
				httppost.setURI(new URI( httpRequest.getUri() ));
			}catch (URISyntaxException e) {
				logger.error(e);
				throw new UtilsException(e);
			}
			
			StringEntity stringEntity = null;
			try {
				stringEntity = new StringEntity( httpRequest.getSoapBody() );
			} catch (UnsupportedEncodingException e) {
				logger.error(e);
				throw new UtilsException(e);
			}
			stringEntity.setContentType( httpRequest.getSoapHeader() );

			httppost.setEntity( stringEntity );
        	
            ResponseHandler<CtnHttpResponse> responseHandler = new ResponseHandler<CtnHttpResponse>() 
            {
				@Override
				public CtnHttpResponse handleResponse(org.apache.http.HttpResponse response) throws ClientProtocolException, IOException {
					
					int status = response.getStatusLine().getStatusCode();

					byte[] data = null;
				
					if (status >= 200 && status < 300) 
                    {
						
						long start = System.currentTimeMillis();
						
						HttpEntity httpEntity = response.getEntity();
			            						
						if (httpEntity != null)
						{
							data = EntityUtils.toByteArray(httpEntity);
			                logger.debug("read "+data.length+" bytes of data");			                
			            } 
						else
						{
							throw new ClientProtocolException("Unexpected http entity is null");
						}
						
						long end = System.currentTimeMillis();
						
						logger.debug("http consume take: "+(end-start));
                    } 
					else 
					{
						throw new ClientProtocolException("Unexpected response status: " + status);
					}
					
					CtnHttpResponse httpResponse = new CtnHttpResponse();
					httpResponse.setData(data);
					httpResponse.setStatusLine(response.getStatusLine().toString());
					httpResponse.setStatusCode(response.getStatusLine().getStatusCode());
					httpResponse.setHttpRequest(httpRequest);
			        return httpResponse;
					
				}
            };
            
			try {
				
				long start_ = System.currentTimeMillis();
				httpResponse = httpclient.execute(httppost,responseHandler);
				long end_ = System.currentTimeMillis();
				logger.debug("http execute take: "+(end_-start_));

				long end = System.currentTimeMillis();
				
				httpResponse.setTakeTime((end-start));
				
				logger.debug(httpResponse);

				logger.debug("http take: "+(end-start));
				
				return httpResponse;
				
			}catch (ClientProtocolException e) {
				logger.error(e);
				throw new UtilsException(e);
			}catch (IOException e) {
				logger.error(e);
				throw new UtilsException(e);
			}	
        } 
        finally 
        {
            try {
				httpclient.close();
			} catch (IOException e) {
				logger.error("occures on httpclient.close()",e);
			}
        }        
	}


	public String get(String url) throws UtilsException
	{	
		CloseableHttpClient httpclient = HttpClients.createDefault();
       
		RequestConfig requestConfig = RequestConfig.custom()
		        .setSocketTimeout(1000)
		        .setConnectTimeout(1000)
		        .setConnectionRequestTimeout(1000)
		        .build();
		
		HttpGet httpget = new HttpGet(url);
		httpget.setConfig(requestConfig);

		System.out.println("Executing request " + httpget.getRequestLine());

		// Create a custom response handler
		ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

			@Override
		    public String handleResponse(final HttpResponse response) throws ClientProtocolException, IOException 
		    {
		        int status = response.getStatusLine().getStatusCode();
		        
		        if (status >= 200 && status < 300) 
		        {
		            HttpEntity entity = response.getEntity();
		            return entity != null ? EntityUtils.toString(entity) : null;
		        } 
		        else 
		        {
		            throw new ClientProtocolException("Unexpected response status: " + status);
		        }
		    }
		};
		
		String responseBody = null;
		try {
			responseBody = httpclient.execute(httpget, responseHandler);
		} catch (IOException e) {
			logger.error(e);
			throw new UtilsException(e);
		}
		
		System.out.println(responseBody);
		
		return responseBody;
	}
}
