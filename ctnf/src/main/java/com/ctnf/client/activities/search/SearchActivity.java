package com.ctnf.client.activities.search;
import java.util.HashMap;
import java.util.Map;
import com.ctnf.client.Factory;
import com.ctnf.client.uibinder.menu.MenuEvent;
import com.ctnf.client.uibinder.menu.MenuEventContext;
import com.ctnf.client.utils.Utils;
import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class SearchActivity extends AbstractActivity implements SearchPresenter{

	private final Factory factory;
	private SearchView view = null;
	private SearchPlace place = null;
	
	public SearchActivity(SearchPlace place, final Factory factory){
		this.factory = factory;
		this.place = place;
	}

	@Override
	public void start(final AcceptsOneWidget panel, EventBus eventBus) {

		view = factory.getSearchView(this);
		
		view.addMenu();
		
		view.init(place);
		
		panel.setWidget(view.asWidget());
		
		this.addThis();
		
		this.setHeader();
		
	}
	
	
	
	@Override
	public void setHeader() {
		Map<String,Object> data = new HashMap<String,Object>();
		data.put(MenuEventContext.EVENT_TYPE, MenuEventContext.Message);
		data.put(MenuEventContext.MessageSourceType, MenuEventContext.MessageSourceSearchView);
		data.put(MenuEventContext.DeviceType, Utils.getDeviceType(Window.getClientWidth()));
		
		MenuEventContext context = new MenuEventContext(data);
		this.factory.getEventBus().fireEvent(new MenuEvent(context));
	}
	
	@Override
	public void goTo(Place place) 
	{
		this.factory.getPlaceController().goTo(place);
	}	
	
	private void addThis()
	{
		if(this.place.getQuery()!=null&&this.place.getQuery().length()>0)
		{
			this.factory.setAddThis(this.place.getUrl(), Utils.capitalize(this.place.getQuery()) );
		}
		else
		{
			this.factory.setAddThis(this.place.getUrl(),"Search tickets by event, performer, venue... - compareticketsnow.com" );
		}
	}
}
