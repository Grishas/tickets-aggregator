package com.ctnf.client;
import com.ctnc.shared.QueryLocationIndex;
import com.ctnf.client.activities.search.SearchPlace;
import com.ctnf.client.uibinder.menu.Menu;
import com.ctnf.client.uibinder.menu.MenuPresenter;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootLayoutPanel;

public class Ctn implements EntryPoint {

	// one instance per application
	private final Factory factory = GWT.create(Factory.class);

	public void onModuleLoad() {
		
		this.setWindowResizeHandler();

		DOM.getElementById("loading").removeFromParent();

		LoadResources.js();
		LoadResources.css();
		LoadResources.code(factory);

		// moke ip conflict resolve and open later (also in searchasync
		// class)... GeolocationUtils.getSetGeolocation(this.factory);

		this.bindPanels();
		this.bindLogic();
	}

	private void bindLogic() {

		MenuPresenter menuPresenter = new Menu(factory);
		factory.setMenuPresenter(menuPresenter);

		CenterPresenter centerPresenter = new Center(factory);
		factory.setCenterPresenter(centerPresenter);

		LoadResources.compareFilter(factory);

		Place defaultPlace = new SearchPlace(QueryLocationIndex.city, factory.getSearchLocation(), true);

		PlaceController placeController = factory.getPlaceController();

		// Start PlaceHistoryHandler with our PlaceHistoryMapper
		AppicationPlacesHistoryMapper historyMapper = GWT.create(AppicationPlacesHistoryMapper.class);
		PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(historyMapper);
		historyHandler.register(placeController, factory.getEventBus(), defaultPlace);

		// Goes to the place represented on URL else default place
		historyHandler.handleCurrentHistory();
	}

	private void bindPanels() {

		RootLayoutPanel.get().add(factory.getCenterPanel());
	}
	
	private void setWindowResizeHandler() {
		Window.addResizeHandler(new ResizeHandler() {

			@Override
			public void onResize(ResizeEvent event) {

				DeviceType deviceType = com.ctnf.client.utils.Utils.getDeviceType(event.getWidth());
				int width = event.getWidth();
				int height = event.getHeight();
				
				switch (deviceType) {
				case MediumDesktop:
					factory.getEventBus().fireEvent(new DeviceTypeEvent(DeviceType.MediumDesktop,width,height));
					GWT.log("MediumDesktop");
					break;
				case Phone:
					factory.getEventBus().fireEvent(new DeviceTypeEvent(DeviceType.Phone,width,height));
					GWT.log("Phone");
					break;
				case Tablet:
					factory.getEventBus().fireEvent(new DeviceTypeEvent(DeviceType.Tablet,width,height));
					GWT.log("Tablet");
					break;
				case LargeDesktop:
					factory.getEventBus().fireEvent(new DeviceTypeEvent(DeviceType.LargeDesktop,width,height));
					GWT.log("LargeDesktop");
					break;
				}
			}
		});
	}

}
