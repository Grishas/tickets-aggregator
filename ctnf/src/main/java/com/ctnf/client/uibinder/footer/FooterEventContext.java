package com.ctnf.client.uibinder.footer;
import java.util.Map;
import com.ctnf.client.EventContext;

public class FooterEventContext extends EventContext{

	public static final String COMPARE_VIEW="COMPARE_VIEW";
	public static final String SEARCH_VIEW="SEARCH_VIEW";
	public static final String HIDE="HIDE";
	public static final String SHOW="SHOW";
	
	public static final String COMPARE_FILTER_VIEW="COMPARE_FILTER_VIEW";
	public static final String MINIMIZE="MINIMIZE";
	public static final String MAXIMIZE="MAXIMIZE";
	
	public FooterEventContext(Map<String, Object> contextData) {
		super(contextData);
	}
}
