package com.ctnf.client.uibinder.menu;
import java.util.Map;
import com.ctnf.client.EventContext;

public class MenuEventContext extends EventContext{

	public static final String Message="Message";
	public static final String DeviceType = "DeviceType";
	public static final String MessageSourceType = "MessageSourceType";
	public static final String HideMenu = "HideMenu";
	public static final String ShowMenu = "ShowMenu";
	
	
	public static final String MessageSourceCompareView = "MessageSourceCompareView";
	
	public static final String EventName = "EventName";
	public static final String EventDate = "EventDate";
	public static final String EventVenue = "EventVenue";
	public static final String EventLocation = "EventLocation";
	public static final String SearchPlace = "SearchPlace";
	
	public static final String MessageSourceSearchView = "MessageSourceSearchView";
	
	public MenuEventContext(Map<String, Object> contextData) {
		super(contextData);
	}
}
