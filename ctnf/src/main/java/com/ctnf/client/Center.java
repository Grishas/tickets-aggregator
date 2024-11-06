package com.ctnf.client;
import com.ctnf.client.Factory;
import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;

public class Center implements CenterPresenter{

	public Center(Factory factory) 
	{	
        ActivityMapper activityMapper = new ApplicationActivityMapper(factory);
        ActivityManager activityManager = new ActivityManager(activityMapper,factory.getEventBus());        
        activityManager.setDisplay(factory.getCenterPanel());
	}
}
