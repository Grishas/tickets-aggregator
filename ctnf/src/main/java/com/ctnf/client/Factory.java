package com.ctnf.client;
import com.ctnc.shared.IndexKey;
import com.ctnc.shared.Location;
import com.ctnc.shared.UserLocation;
import com.ctnf.client.activities.checkout.CheckoutPresenter;
import com.ctnf.client.activities.checkout.CheckoutView;
import com.ctnf.client.activities.compare.CompareView;
import com.ctnf.client.activities.compare.filter.CompareFilterView;
import com.ctnf.client.activities.generic.GenericPresenter;
import com.ctnf.client.activities.generic.GenericView;
import com.ctnf.client.activities.search.SearchPresenter;
import com.ctnf.client.activities.search.SearchView;
import com.ctnf.client.activities.search.suggestion.SuggestionServiceAsync;
import com.ctnf.client.services.CheckoutServiceAsync;
import com.ctnf.client.services.CompareServiceAsync;
import com.ctnf.client.services.MaintenanceServiceAsync;
import com.ctnf.client.services.SearchServiceAsync;
import com.ctnf.client.uibinder.footer.FooterPresenter;
import com.ctnf.client.uibinder.footer.FooterView;
import com.ctnf.client.uibinder.menu.MenuPresenter;
import com.ctnf.client.uibinder.menu.MenuView;
//import com.ctnf.client.utils.Geolocation;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.web.bindery.event.shared.EventBus;

public interface Factory {

	//http://www.addthis.com/academy/setting-the-url-title-to-share/
	void setAddThis(String url,String title);

	//global
	EventBus getEventBus();
	PlaceController getPlaceController();
	
	//services
	SuggestionServiceAsync getSuggestionService();
	SearchServiceAsync getSearchService();
	CompareServiceAsync getCompareService();
	CheckoutServiceAsync getCheckoutService();
	MaintenanceServiceAsync getMaintenanceService();
	
	//views
	SearchView getSearchView(SearchPresenter searchResultsPresenter);

	CompareView getCompareView(DeviceType deviceType);
	MenuView getMenuView();
	CheckoutView getCheckoutView(CheckoutPresenter presenter);
	
	GenericView getAboutView(GenericPresenter presenter);
	GenericView getBrokerView(GenericPresenter presenter);
	GenericView getContactView(GenericPresenter presenter);
	GenericView getErrorView(GenericPresenter presenter);
	GenericView getPolicyView(GenericPresenter presenter);
	GenericView getTermsOfUseView(GenericPresenter presenter);
	FooterView getFooterView();
	CompareFilterView getCompareFilterView(final DeviceType deviceType);
	
	SimpleLayoutPanel getCenterPanel();
	
	//presenters
	void setMenuPresenter(MenuPresenter menuPresenter);
	MenuPresenter setMenuPresenter();

	void setCenterPresenter(CenterPresenter centerPresenter);
	CenterPresenter getCenterPresenter();

	void setFooterPresenter(FooterPresenter footerPresenter);
	FooterPresenter getFooterPresenter();
	
	IndexKey runOnSuggesterIndex();
	void setRunOnSuggesterIndex(IndexKey runOnSuggesterIndex);

	//current location of user resolved by ip in server
	void setUserLocation(UserLocation userLocation);
	UserLocation getUserLocation();
	
	//search by location for query
	void setSearchLocation(Location searchLocation) ;
	Location getSearchLocation();

	
}
