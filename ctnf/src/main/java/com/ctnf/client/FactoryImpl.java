package com.ctnf.client;
import com.ctnc.shared.IndexKey;
import com.ctnc.shared.Location;
import com.ctnc.shared.UserLocation;
import com.ctnf.client.activities.checkout.CheckoutPresenter;
import com.ctnf.client.activities.checkout.CheckoutView;
import com.ctnf.client.activities.checkout.CheckoutViewImpl;
import com.ctnf.client.activities.compare.CompareView;
import com.ctnf.client.activities.compare.filter.CompareFilterView;
import com.ctnf.client.activities.compare.filter.CompareFilterViewCommonImpl;
import com.ctnf.client.activities.compare.filter.md.lg.CompareFilterViewImpl;
import com.ctnf.client.activities.generic.AboutViewImpl;
import com.ctnf.client.activities.generic.BrokerViewImpl;
import com.ctnf.client.activities.generic.ContactViewImpl;
import com.ctnf.client.activities.generic.ErrorViewImpl;
import com.ctnf.client.activities.generic.GenericPresenter;
import com.ctnf.client.activities.generic.GenericView;
import com.ctnf.client.activities.generic.PolicyViewImpl;
import com.ctnf.client.activities.generic.TermsOfUseViewImpl;
import com.ctnf.client.activities.search.SearchPresenter;
import com.ctnf.client.activities.search.SearchView;
import com.ctnf.client.activities.search.SearchViewImpl;
import com.ctnf.client.activities.search.suggestion.SuggestionServiceAsync;
import com.ctnf.client.services.CheckoutServiceAsync;
import com.ctnf.client.services.CompareServiceAsync;
import com.ctnf.client.services.MaintenanceServiceAsync;
import com.ctnf.client.services.SearchServiceAsync;
import com.ctnf.client.uibinder.footer.FooterPresenter;
import com.ctnf.client.uibinder.footer.FooterView;
import com.ctnf.client.uibinder.footer.FooterViewImpl;
import com.ctnf.client.uibinder.menu.MenuPresenter;
import com.ctnf.client.uibinder.menu.MenuView;
import com.ctnf.client.uibinder.menu.MenuViewImpl;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

public class FactoryImpl implements Factory {
	
	/***
	 * Update current url and title for add this 
	 * @param url
	 * @param title
	 */
	public native void setAddThis(String url,String title) /*-{
//		$wnd.addthis_share.url = url;
//		$wnd.addthis_share.title = title;
	}-*/;
	
	private static EventBus eventBus = null;
	@Override
	public EventBus getEventBus() {
		if (eventBus == null){ 
			eventBus = new SimpleEventBus();
		}
		return eventBus;
	}

	private static PlaceController placeController = null;
	@Override
	public PlaceController getPlaceController() 
	{
		if (placeController == null) 
		{	
			placeController = 
					new PlaceController(getEventBus(),
							new PlaceController.DefaultDelegate());
		}
		return placeController;
	}

	@Override
	public SuggestionServiceAsync getSuggestionService() {
		return SuggestionServiceAsync.Util.getInstance();
	}
	
	@Override
	public MaintenanceServiceAsync getMaintenanceService() {
		return MaintenanceServiceAsync.Util.getInstance();
	}
	
	
	
	
	@Override
	public SearchServiceAsync getSearchService() {
		return SearchServiceAsync.Util.getInstance();
	}
	
	@Override
	public CompareServiceAsync getCompareService() {
		return CompareServiceAsync.Util.getInstance();
	}

	@Override
	public CheckoutServiceAsync getCheckoutService() {
		return CheckoutServiceAsync.Util.getInstance();
	}
	
	private static CompareView compareView = null;
	@Override
	public CompareView getCompareView(final DeviceType deviceType) {
		switch (deviceType){
			case LargeDesktop:
			case MediumDesktop:
				if(compareView==null){
					compareView = new com.ctnf.client.activities.compare.md.lg.CompareViewImpl();
				}
				return compareView;
			case Tablet:				
			case Phone:
				if(compareView==null){
					compareView = new com.ctnf.client.activities.compare.xs.sm.CompareViewImpl();
				}
				return compareView;
		}
		return null;
	}

	private static SearchView searchView = null;
	@Override
	public SearchView getSearchView(SearchPresenter presenter) {
		if(searchView==null){
			searchView = new SearchViewImpl(this);
		}
		searchView.setSearchPresenter(presenter);
		return searchView;
	}
	public boolean isSearchViewInitialized(){
		return (searchView==null)?false:true;
	}
	
	private static MenuView menueView = null;
	@Override
	public MenuView getMenuView() {
		if(menueView==null){
			menueView = new MenuViewImpl();
		}
		return menueView;
	}
	private static FooterView footerView = null;
	@Override
	public FooterView getFooterView() {
		if(footerView==null){
			footerView = new FooterViewImpl();
		}
		return footerView;
	}
	
	private static CheckoutView checkoutView= null;
	@Override
	public CheckoutView getCheckoutView(CheckoutPresenter presenter) {
		if(checkoutView==null){
			checkoutView = new CheckoutViewImpl();
		}
		checkoutView.setPresenter(presenter);
		return checkoutView;	
	}
	
	private static GenericView aboutView= null;
	@Override
	public GenericView getAboutView(GenericPresenter presenter) {
		if(aboutView==null){
			aboutView = new AboutViewImpl(presenter);
		}
		return aboutView;
	}

	private static GenericView brokerView= null;
	@Override
	public GenericView getBrokerView(GenericPresenter presenter) {
		if(brokerView==null){
			brokerView = new BrokerViewImpl(presenter);
		}
		return brokerView;
	}

	private static GenericView contactView= null;
	@Override
	public GenericView getContactView(GenericPresenter presenter) {
		if(contactView==null){
			contactView = new ContactViewImpl(presenter);
		}
		return contactView;
	}	
	
	private static GenericView errorView= null;
	@Override
	public GenericView getErrorView(GenericPresenter presenter) {
		if(errorView==null){
			errorView = new ErrorViewImpl(presenter);
		}
		return errorView;
	}

	private static GenericView policyView= null;
	@Override
	public GenericView getPolicyView(GenericPresenter presenter) {
		if(policyView==null){
			policyView = new PolicyViewImpl(presenter);
		}
		return policyView;
	}

	private static GenericView termsOfUseView= null;
	@Override
	public GenericView getTermsOfUseView(GenericPresenter presenter) {
		if(termsOfUseView==null){
			termsOfUseView = new TermsOfUseViewImpl(presenter);
		}
		return termsOfUseView;
	}
	
	private static CompareFilterView compareFilterView= null;
	@Override
	public CompareFilterView getCompareFilterView(final DeviceType deviceType) {
			
		switch (deviceType){
		case LargeDesktop:
		case MediumDesktop:
			if(compareFilterView==null){
				compareFilterView = new CompareFilterViewImpl();
			}
			return compareFilterView;
		case Tablet:				
		case Phone:
			if(compareFilterView==null){
				compareFilterView = new com.ctnf.client.activities.compare.filter.xs.sm.CompareFilterViewImpl();
			}
			return compareFilterView;
		}
		return null;
	}
	
	private static FooterPresenter footerPresenter = null;
	@Override
	public void setFooterPresenter(FooterPresenter footerPresenter) {
		FactoryImpl.footerPresenter = footerPresenter;
	}
	@Override
	public FooterPresenter getFooterPresenter() {
		return FactoryImpl.footerPresenter;
	}
	
	private static final SimpleLayoutPanel center = new SimpleLayoutPanel();
	@Override
	public SimpleLayoutPanel getCenterPanel(){
		return center;
	}
	
	private static MenuPresenter menuPresenter = null;
	@Override
	public void setMenuPresenter(MenuPresenter menuPresenter) {
		FactoryImpl.menuPresenter = menuPresenter;
	}
	@Override
	public MenuPresenter setMenuPresenter() {
		return FactoryImpl.menuPresenter;
	}
	
	private static CenterPresenter centerPresenter = null;
	@Override
	public void setCenterPresenter(CenterPresenter centerPresenter) {
		FactoryImpl.centerPresenter = centerPresenter;
	}
	@Override
	public CenterPresenter getCenterPresenter() {
		return FactoryImpl.centerPresenter;
	}
	
	private static IndexKey runOnSuggesterIndex = null;
	@Override
	public IndexKey runOnSuggesterIndex() {
		if(runOnSuggesterIndex==null){
			return IndexKey.event;
		}
		return runOnSuggesterIndex;
	}
	@Override
	public void setRunOnSuggesterIndex(final IndexKey runOnSuggesterIndex) {
		
		Scheduler.get().scheduleDeferred(new Command() {
			@Override
			public void execute() {
				FactoryImpl.runOnSuggesterIndex = runOnSuggesterIndex;
			}
		});
	}

	


	
	private static UserLocation userLocation = null;
	@Override
	public void setUserLocation(UserLocation userLocation) {
		FactoryImpl.userLocation = userLocation;
	}
	@Override
	public UserLocation getUserLocation() {		
		return FactoryImpl.userLocation;
	}

	//location for server query
	private static Location searchLocation = null;
	@Override
	public void setSearchLocation(Location searchLocation) {
		FactoryImpl.searchLocation = searchLocation;	
	}
	@Override
	public Location getSearchLocation() {
		return FactoryImpl.searchLocation;
	}

}
