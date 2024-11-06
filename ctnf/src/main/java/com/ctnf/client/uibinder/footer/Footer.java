package com.ctnf.client.uibinder.footer;
import java.util.Map;

import org.gwtbootstrap3.client.ui.Modal;

import com.ctnf.client.DeviceType;
import com.ctnf.client.EventContext;
import com.ctnf.client.Factory;
import com.ctnf.client.utils.Utils;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Window;
import com.google.web.bindery.event.shared.EventBus;

public class Footer implements FooterPresenter{

	private final EventBus eventBus;
	private final Factory factory;

	public Footer(Factory factory) 
	{	
		this.factory = factory;	
		this.eventBus = this.factory.getEventBus();		
	}

//	private void bind() 
//	{
//		eventBus.addHandler(FooterEvent.TYPE,new FooterEventHandler() 
//		{			
//			@Override
//			public void process(FooterEvent event) {
//
//				DeviceType deviceType = Utils.getDeviceType(Window.getClientWidth());
//				
//				Map<String,Object> context = event.getContext().getContext();
//				String type = (String)context.get(EventContext.EVENT_TYPE);
//				
//				if(FooterEventContext.COMPARE_FILTER_VIEW.equals(type)&&context.containsKey(FooterEventContext.MINIMIZE))
//				{
////					if(deviceType.equals(DeviceType.Phone))
////					{
//						factory.getContainerPanlel().setWidgetSize(factory.getFooterPanel(), 21);
////					}
////					else
////					{
////						factory.getContainerPanlel().setWidgetSize(factory.getFooterPanel(), 21);
////					}
//				}
//				
//				//on maximize
//				else if(FooterEventContext.COMPARE_FILTER_VIEW.equals(type)&&context.containsKey(FooterEventContext.MAXIMIZE))
//				{
//					initCompareFooterView();
//				}
//				
//				//init
//				else if(FooterEventContext.COMPARE_VIEW.equals(type))
//				{
//					factory.getFooterPanel().setWidget(factory.getCompareFilterView(deviceType));
//					
////					Modal m = new Modal();
////					m.add(factory.getCompareFilterView());
////					m.show();
//
//					initCompareFooterView();									
//				}	
//				else if(FooterEventContext.HIDE.equals(type))
//				{
//					factory.getFooterPanel().clear();
//					factory.getContainerPanlel().setWidgetSize(factory.getFooterPanel(), 0);
//				}
//				else if(FooterEventContext.SHOW.equals(type))
//				{
//					initCompareFooterView();
//				}
//			}
//		});
//	}
//	
//	private void initCompareFooterView()
//	{
//		DeviceType deviceType = com.ctnf.client.utils.Utils.getDeviceType(Window.getClientWidth());
//
//		switch (deviceType) 
//		{
//			case Phone:
//				factory.getContainerPanlel().setWidgetSize(factory.getFooterPanel(), 110);
//				break;
//			case Tablet:
//				factory.getContainerPanlel().setWidgetSize(factory.getFooterPanel(), 110);
//				break;
//			case MediumDesktop:
//				factory.getContainerPanlel().setWidgetSize(factory.getFooterPanel(), 70);
//				break;
//			case LargeDesktop:
//				factory.getContainerPanlel().setWidgetSize(factory.getFooterPanel(), 70);
//				break;
//		}
//	}
	
	@Override
	public void goTo(Place place) {
		this.factory.getPlaceController().goTo(place);
	}
}
