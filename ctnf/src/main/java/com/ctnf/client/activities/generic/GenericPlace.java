package com.ctnf.client.activities.generic;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class GenericPlace extends Place {
	
	private static final String PREFIX  = "!page";
	
	private Page page = null; 
	private Throwable throwable = null;
	
	public GenericPlace(Page page){
		this.page = page;
	}
	
	public GenericPlace(Page page,Throwable throwable){
		this(page);
		this.throwable = throwable;
	}

	public Throwable getThrowable() {
		return throwable;
	}
	public void setThrowable(Throwable throwable) {
		this.throwable = throwable;
	}
	public Page getPage() {
		return page;
	}
	public void setPage(Page page) {
		this.page = page;
	}
	
	public String getUrl()
	{
		Tokenizer tokenizer = new Tokenizer();
		StringBuilder url = new StringBuilder(); 
		//url.append("compareticketsnow.com");
		url.append(com.google.gwt.core.client.GWT.getHostPageBaseURL());

		url.append("#"+PREFIX+":");
		url.append(tokenizer.getToken(this));		
		return url.toString();
	}

	@Prefix(PREFIX)
	public static class Tokenizer implements PlaceTokenizer<GenericPlace>
	{
		@Override
		public GenericPlace getPlace(String token) {
			
			Page page = null;
			try{
				page = Page.valueOf(token);
			}
			catch(java.lang.IllegalArgumentException error){
				page = Page.error;
			}
			
			return new GenericPlace(page);
		}

		@Override
		public String getToken(GenericPlace place) {
			return place.getPage().name();
		}
	}
}