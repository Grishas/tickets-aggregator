package com.ctnf.client.activities.compare;
import java.util.Map;
import com.ctnf.client.EventContext;

public class CompareEventContext extends EventContext{

	public static final String VALUE_UPDATED="VALUE_UPDATED";
	public static final String RESET="RESET";
	public static final String CompareFilterValues="CompareFilterValues";

	public CompareEventContext(Map<String, Object> contextData) {
		super(contextData);
	}
}
