package com.ctnf.client.activities.compare.filter;
import java.util.Map;
import com.ctnf.client.EventContext;

public class CompareFilterEventContext extends EventContext{

	public static final String EVENT_TYPE="EVENT_TYPE";
	public static final String INIT="INIT";
	public static final String RESET="RESET";
	public static final String MAP = "MAP";
	public static final String SET="SET";
	public static final String UPDATE_SECTION_SELECT="UPDATE_SECTION_SELECT";

	public static final String TicketsBlocks="TicketsBlock";
	public static final String AboutTickets="AboutTickets";
	public static final String ChangeSectionsFromMap="ChangeSectionsFromMap";
	public static final String HaveParkingTickets="HaveParkingTickets";
	
	public CompareFilterEventContext(Map<String, Object> contextData) {
		super(contextData);
	}
}
