package com.ctnf.client.activities.search;
import java.util.Date;
import java.util.List;
import org.gwtbootstrap3.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Collapse;
import org.gwtbootstrap3.client.ui.Container;
import org.gwtbootstrap3.client.ui.Icon;
import org.gwtbootstrap3.client.ui.Image;
import org.gwtbootstrap3.client.ui.Pagination;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.SuggestBox;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconPosition;
import org.gwtbootstrap3.client.ui.constants.IconSize;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.Toggle;
import org.gwtbootstrap3.client.ui.gwt.CellTable;
import org.gwtbootstrap3.client.ui.html.Div;
import org.gwtbootstrap3.client.ui.html.Paragraph;
import org.gwtbootstrap3.client.ui.html.Text;
import org.gwtbootstrap3.extras.datepicker.client.ui.DatePicker;
import org.gwtbootstrap3.extras.datepicker.client.ui.base.events.ClearDateEvent;
import org.gwtbootstrap3.extras.datepicker.client.ui.base.events.ClearDateHandler;
import org.gwtbootstrap3.extras.notify.client.constants.NotifyPlacement;
import org.gwtbootstrap3.extras.notify.client.constants.NotifyPosition;
import org.gwtbootstrap3.extras.notify.client.ui.Notify;
import org.gwtbootstrap3.extras.notify.client.ui.NotifySettings;

import com.ctnc.shared.IndexKey;
import com.ctnc.shared.QueryLocationIndex;
import com.ctnc.shared.SearchResponse;
import com.ctnc.shared.SearchResult;
import com.ctnc.shared.UserLocation;
import com.ctnf.client.DeviceType;
import com.ctnf.client.Factory;
import com.ctnf.client.PaginationCustom;
import com.ctnf.client.Resources;
import com.ctnf.client.activities.compare.ComparePlace;
import com.ctnf.client.activities.generic.GenericPlace;
import com.ctnf.client.activities.generic.Page;
import com.ctnf.client.activities.search.suggestion.SuggestionOracleSearch;
import com.ctnf.client.utils.Utils;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.google.gwt.view.client.Range;

public class SearchViewImpl extends Composite implements SearchView{

	private static SearchViewImplUiBinder uiBinder = GWT.create(SearchViewImplUiBinder.class);

	interface SearchViewImplUiBinder extends UiBinder<Widget, SearchViewImpl> {}

	private final int rangeLength = 20;
	
	@UiField Image image1;//,image2;
	  
	@UiField Container container;

	@UiField Div divMenu;

	//@UiField(provided = true) Image imageLogoCompareTicketsNow;
	
	@UiField Collapse collapseLocation,collapseDates;
		
	@UiField org.gwtbootstrap3.client.ui.Column 
	columnSearchBy,
	columnWaiting,
	columnAboutResults1,
	columnAboutResults2,
	columnSetLocationAndDate1,columnSetLocationAndDate2,
	columnDates,
	columnLocation,
	columnResults;	
	
	@UiField(provided = true) CellTable<SearchResult> cellTable;
	
	@UiField(provided = true) Pagination pagination;
	
		
	@UiField Text textPagination;
	@UiField Div divPagination;
	
	@UiField Row rowWelcome,rowTop,row2,row22,row3,row4,row5,row6,row7;
	
    private SimplePager simplePager = new SimplePager();
    
	private SearchPresenter presenter;
	private SearchAsyncDataProvider asyncDataProvider;
	
	private Column<SearchResult,SafeHtml> columnVerticalHr ; 
	private Column<SearchResult,SafeHtml> columnDate ; 
	private Column<SearchResult,SafeHtml> columnEventPerformerVenueLocation;
	private Column<SearchResult,ImageResource> columnEventImage;
	private Column<SearchResult, String> columnButtonTickets;

	@UiField(provided = true) SuggestBox suggestBoxEventPerformerVenue,suggestBoxLocation;

	@UiField  Anchor anchorResults;
	
	@UiField AnchorListItem 
	
	anchorSearchByA,anchorSearchByB,
	
	anchorListItemNextADays , anchorListItemNextBDays , anchorListItemNextCDays , anchorListItemNextDDays,
	
	anchorListItemLocationSearchAround , anchorListItemLocationKeepOriginalLocation;

	@UiField Button 
	buttonResetSearchBy  ,  buttonSearchBy , 	buttonSearch,
	
	buttonResetDates     ,  buttonCustomizeDates , 		buttonCollapseCustomizeDate,
	
	buttonResetLocation  ,  buttonCustomizeLocation , 	buttonCollapseNewLocation;
	
	@UiField Paragraph paragraphResults;
	
	@UiField DatePicker dateTimePickerFrom;

	private static final String EVENT 		= "Event";
	private static final String PERFORMER 	= "Performer";
	private static final String VENUE 		= "Venue";
	
	private static final String SET_DATE 		= "Dates";
	private static final String TODAY 			= "Today";
	private static final String TOMORROW 		= "Tomorrow";
	private static final String NEXT_7_DAYS 	= "Next 7 days";
	private static final String NEXT_30_DAYS	= "Next 30 days";

	private final static String SET_LOCATION = "Set location";
	private final static String SEARCH_AROUND = "Around";
	
	private String from = null;
	private String to = null;
	
	private String query = null;

	private DeviceType deviceType = null;
	
	private final Factory factory;
	
	private SearchPlace place;
	
	private QueryLocationIndex howToSearchLocation = QueryLocationIndex.city;

	private SearchResponse searchResponse = null;
	
	public SearchViewImpl(final Factory factory) 
	{
		this.factory = factory;
		
		this.deviceType = Utils.getDeviceType( Window.getClientWidth() );

		//this.imageLogoCompareTicketsNow = new Image(Resources.instance.compareticketsnow());
		

		SuggestionOracleSearch suggestionOracleSearch = new SuggestionOracleSearch(factory);
		this.suggestBoxEventPerformerVenue = new SuggestBox(suggestionOracleSearch);
		this.suggestBoxEventPerformerVenue.getValueBox().addFocusHandler(new FocusHandler() {
			@Override
			public void onFocus(FocusEvent event) {
				factory.setRunOnSuggesterIndex( getIndexKey( buttonSearchBy.getText() ));
				
				//moveContainerLeft();
				//rowWelcome.setVisible(false);				
			}
		});

		this.suggestBoxLocation = new SuggestBox(new SuggestionOracleSearch(factory));
		this.suggestBoxLocation.getValueBox().addFocusHandler(new FocusHandler() {
			@Override
			public void onFocus(FocusEvent event) {		
			
				factory.setRunOnSuggesterIndex( IndexKey.location );

				//moveContainerLeft();
			//	rowWelcome.setVisible(false);
				
				suggestBoxLocation.setValue("");
			}
		});
		
		this.suggestBoxLocation.addSelectionHandler(new SelectionHandler<SuggestOracle.Suggestion>() {
			@Override
			public void onSelection(SelectionEvent<Suggestion> event) {
				
				String newLocation = event.getSelectedItem().getReplacementString();
				String newCity = getCityFromLocation(newLocation);
				String newSegmentKey = getSegmentKeyFromLocation(newLocation);

				//user set original location
				if( factory.getUserLocation()!=null&&
					factory.getUserLocation().getCity()!=null&&
					factory.getUserLocation().getSegmentedKey()!=null&&
					factory.getUserLocation().getCity().equals(newCity)&&
					factory.getUserLocation().getSegmentedKey().equals(newSegmentKey))
				{
					factory.setSearchLocation(factory.getUserLocation());
				}
				else//new location select
				{
					UserLocation newUserLocation = new UserLocation();
					newUserLocation.setCity(newCity.toLowerCase());
					newUserLocation.setSegmentedKey(newSegmentKey.toLowerCase());
					
					factory.setSearchLocation(newUserLocation);
				}
			
				anchorListItemLocationKeepOriginalLocation.setText( newLocation );
				
				anchorListItemLocationSearchAround.setText( SEARCH_AROUND +" "+ newLocation );

				setLocation(newLocation, false, IconType.MAP_MARKER );
				
				collapseLocation.hide();
								
				howToSearchLocation = QueryLocationIndex.city;				
				goToSearchResultsPlace();
			}
		});

		this.suggestBoxEventPerformerVenue.addSelectionHandler(new SelectionHandler<SuggestOracle.Suggestion>() {
			@Override
			public void onSelection(SelectionEvent<Suggestion> event) {								
				goToSearchResultsPlace();
			}
		});

		this.cellTable = new CellTable<SearchResult>();
		
		this.pagination = new PaginationCustom();
						
		this.cellTable.setEmptyTableWidget(new Text("Sorry! We couldn't find anything for your request"));
		
		Icon waitingForEvents = new Icon();
		waitingForEvents.setType(IconType.SPINNER);
		waitingForEvents.setSize(IconSize.TIMES2);
		waitingForEvents.setSpin(true);
		waitingForEvents.setColor("#337ab7");
		this.cellTable.setLoadingIndicator(waitingForEvents);
		
		initWidget(uiBinder.createAndBindUi(this));
				
		switch (deviceType) 
		{
			case LargeDesktop:
				break;
			case MediumDesktop:
				break;
			case Phone:
				this.dateTimePickerFrom.setReadOnly(true);
				//this.rowTop.setMarginTop(5);
				this.rowWelcome.setVisible(false);
		
				//this.buttonResetSearchBy.setVisible(false);
				this.buttonSearch.setWidth("40px");	
				break;
		
			case Tablet:
				
				this.dateTimePickerFrom.setReadOnly(true);
				//this.rowTop.setMarginTop(5);
				this.rowWelcome.setVisible(false);
				break;
			default:
				break;	
		}
		
		
		
		this.iconWaitingForEvents.setMarginLeft(20);
		this.iconWaitingForEvents.setMarginTop(20);
	
		this.buildGrid( this.deviceType  );	
		
		//move to code
		this.dateTimePickerFrom.addClearDateHandler(new ClearDateHandler() {
			@Override
			public void onClearDate(ClearDateEvent evt) {
				setDate(SET_DATE,true);
			}
		});
		
		this.dateTimePickerFrom.addValueChangeHandler(new ValueChangeHandler<Date>() {

			@Override
			public void onValueChange(ValueChangeEvent<Date> event) 
			{		
				Date fromTmp  = event.getValue();
				CalendarUtil.resetTime(fromTmp);
				
				Date toTmp = CalendarUtil.copyDate(fromTmp);	
				CalendarUtil.addMonthsToDate(toTmp, 12);
				
				from = formatDateRange(fromTmp);
				to = formatDateRange(toTmp);

				setDate("From "+from, false);
				
				collapseDates.hide();
				
				buttonCustomizeDates.setType(ButtonType.WARNING);
				
				goToSearchResultsPlace();
			}
		});
	
		
//		String image = "url(squairy_light.png)";
//		
//		this.rowWelcome.getElement().getStyle().setBackgroundImage(image);
//		this.rowTop.getElement().getStyle().setBackgroundImage(image);
//		this.row2.getElement().getStyle().setBackgroundImage(image);
//		this.row3.getElement().getStyle().setBackgroundImage(image);
//		this.row4.getElement().getStyle().setBackgroundImage(image);
//		this.row5.getElement().getStyle().setBackgroundImage(image);
//		this.row6.getElement().getStyle().setBackgroundImage(image);
//		this.row7.getElement().getStyle().setBackgroundImage(image);
		
		//this.addStyleName("search");
		//this.rowWelcome.addStyleName("search");
		
		image1.setUrl(Resources.instance.compareticketsnow().getSafeUri());
		//image2.setUrl(Resources.instance.compareticketsnow().getSafeUri());
	}
	
	@Override
	public void addMenu()
	{
		Scheduler.get().scheduleDeferred(new Command() {
			@Override
			public void execute() 
			{	
				 Widget menu = factory.getMenuView().asWidget();		
				 divMenu.add(menu);
			}
		});	
	}
	
	
	private String formatDateRange(Date date)
	{		
		return 	DateTimeFormat.getFormat("dd/MM/yyyy").format(date);	
	}
	
	@UiField Icon iconWaitingForEvents;
	@Override
	public void waiting(boolean show) 
	{
		this.iconWaitingForEvents.setVisible(show);
	}
	
	private String getSegmentKeyFromLocation(String location) {

		String[] tmp = location.split(",");
		
		if(tmp.length==3)
		{
			return tmp[2]+"_"+tmp[1];
		}
		else if(tmp.length==2)
		{
			return tmp[1];
		}
		
		return null;
	}

	private String getCityFromLocation(String location) {

		String[] tmp = location.split(",");
		
		return tmp[0];
	}
	
	private String getLocation(String city,String segmentKey)
	{
		if(city==null||segmentKey==null)
		{
			this.presenter.goTo(new GenericPlace(Page.error));
			return "";
		}
		
		String[] segmentKey_ = segmentKey.split("_");
		String location = null;
		
		String delimiter = null;
		
		if(city.contains("-"))
		{
			delimiter = "-";
		}
		else
		{
			delimiter = " ";
		}
		
		city = Utils.capitalize( city , delimiter );

		if(segmentKey_.length==2)
		{
			location = city+","+(segmentKey_[1]+","+segmentKey_[0]).toUpperCase();
		}
		else if(segmentKey_.length==1)
		{
			location = city+","+segmentKey_[0].toUpperCase(); 
		}
		
		return location;
	}
	
	private String getLocation(SearchResult searchResult,String delimeter,boolean giveMeOnlyExactCity)
	{
		String[] countryStateAbbr = searchResult.getSegmentKey().split("_");
		
		String countryAbbr = delimeter + countryStateAbbr[0].toUpperCase();
		
		String stateAbbr = null;
		if(countryStateAbbr.length==2)
		{
			stateAbbr = ","+countryStateAbbr[1].toUpperCase();
		}
		else
		{
			stateAbbr="";
		}
	
		StringBuffer results = new StringBuffer();
		
		if(giveMeOnlyExactCity==true)
		{
			for(String relatedCityName : searchResult.getRelatedCityNames())
			{
				if(relatedCityName.toLowerCase().trim().equals(this.searchResponse.getSearchLocation().getCity().toLowerCase().trim()))
				{
					return results.append( relatedCityName + stateAbbr + countryAbbr +" ").toString() ;
				}
			}
		}
	
		//return first city
		for(String relatedCityName : searchResult.getRelatedCityNames())
		{
			return results.append( relatedCityName + stateAbbr + countryAbbr +" ").toString() ;
		}
		
		return results.toString();
	}
	
	@UiHandler({"anchorListItemLocationKeepOriginalLocation"})
	public void handleAnchorListItemLocationKeepOriginalLocationOnClick(ClickEvent event)
	{
		this.howToSearchLocation = QueryLocationIndex.city;
		
		this.goToSearchResultsPlace();
	}
	
	@UiHandler({"anchorListItemLocationSearchAround"})
	public void handleAnchorListItemLocationSearchAroundOnClick(ClickEvent event)
	{
		this.howToSearchLocation = QueryLocationIndex.geo;
		
		this.setLocation( this.anchorListItemLocationSearchAround.getText() , false , IconType.DOT_CIRCLE_O);

		this.goToSearchResultsPlace();
	}
	
	
	
	private void moveContainerLeft()
	{
//		this.columnSearchBy.setOffset(ColumnOffset.MD_1,ColumnOffset.LG_1);
//		this.columnWaiting.setOffset(ColumnOffset.MD_1,ColumnOffset.LG_1);
//		this.columnAboutResults1.setOffset(ColumnOffset.MD_1,ColumnOffset.LG_1);
//		this.columnAboutResults2.setOffset(ColumnOffset.MD_1,ColumnOffset.LG_1);
//		this.columnSetLocationAndDate.setOffset(ColumnOffset.MD_1,ColumnOffset.LG_1);
//		this.columnDates.setOffset(ColumnOffset.MD_1,ColumnOffset.LG_1);
//		this.columnLocation.setOffset(ColumnOffset.MD_1,ColumnOffset.LG_1);
//		this.columnResults.setOffset(ColumnOffset.MD_1,ColumnOffset.LG_1);
		
		/*
		this.columnSearchBy.setOffset(ColumnOffset.MD_0,ColumnOffset.LG_0);
		this.columnWaiting.setOffset(ColumnOffset.MD_0,ColumnOffset.LG_0);
		this.columnAboutResults1.setOffset(ColumnOffset.MD_0,ColumnOffset.LG_0);
		this.columnAboutResults2.setOffset(ColumnOffset.MD_0,ColumnOffset.LG_0);
		this.columnSetLocationAndDate1.setOffset(ColumnOffset.MD_0,ColumnOffset.LG_0);
		this.columnSetLocationAndDate2.setOffset(ColumnOffset.MD_0,ColumnOffset.LG_0);

		this.columnDates.setOffset(ColumnOffset.MD_0,ColumnOffset.LG_0);
		this.columnLocation.setOffset(ColumnOffset.MD_0,ColumnOffset.LG_0);
		this.columnResults.setOffset(ColumnOffset.MD_0,ColumnOffset.LG_0);
		
		//set only for large devices 
		if( this.deviceType.equals(DeviceType.LargeDesktop)|| this.deviceType.equals(DeviceType.MediumDesktop) )
		{
			double margin = 20;
			this.columnSearchBy.setMarginLeft(margin);
			this.columnWaiting.setMarginLeft(margin);
			this.columnAboutResults1.setMarginLeft(margin);
			this.columnAboutResults2.setMarginLeft(margin);
			
			this.columnSetLocationAndDate1.setMarginLeft(margin);
			this.columnSetLocationAndDate2.setMarginLeft(margin);
			
			this.columnDates.setMarginLeft(margin);
			this.columnLocation.setMarginLeft(margin);
			this.columnResults.setMarginLeft(margin);
			
			this.addStyleName("searchAdjust");
			
			
			
		}*/
		
	}
	
	@UiHandler({"buttonResetLocation"})
	public void handleButtonResetLocationOnClick(ClickEvent event)
	{
		this.howToSearchLocation = QueryLocationIndex.ignore;

		this.moveContainerLeft();
		
		//this.rowWelcome.setVisible(false);
		
		this.collapseLocation.hide();
		
		if( ! this.buttonCustomizeLocation.getText().equals(SET_LOCATION)  ) 
		{
			this.suggestBoxLocation.setValue("");
			
			this.setLocation(SET_LOCATION,true,IconType.MAP_MARKER);
			
			this.factory.setSearchLocation(null);
			
			this.goToSearchResultsPlace();
		}
	}
	
	@UiHandler({"buttonResetSearchBy"})
	public void handleButtonResetSearchByOnClick(ClickEvent event)
	{
		this.moveContainerLeft();
		
		//this.rowWelcome.setVisible(false);
		
		this.buttonSearchBy.setType(ButtonType.PRIMARY);
		
		if(this.suggestBoxEventPerformerVenue.getValue()!=null&&this.suggestBoxEventPerformerVenue.getValue().length()>0)
		{
			this.suggestBoxEventPerformerVenue.setValue("");
			
			this.goToSearchResultsPlace();
		}
	}
	
	@UiHandler({"buttonResetDates"})
	public void handleResetDatesOnClick(ClickEvent event)
	{
		this.moveContainerLeft();
		
		//this.rowWelcome.setVisible(false);
		
		this.collapseDates.hide();

		if(this.from!=null)
		{
			this.setDate(SET_DATE,true);			
			this.goToSearchResultsPlace();
		}
		
	}
	
	@UiHandler({"buttonCollapseCustomizeDate"})
	public void handleCustomizeDatesOnClick(ClickEvent event)
	{
		//this.rowWelcome.setVisible(false);
		this.moveContainerLeft();
		
		this.collapseLocation.hide();
	}
	
	@UiHandler({"buttonCollapseNewLocation"})
	public void handleCollapseLocationOnClick(ClickEvent event)
	{
		moveContainerLeft();
		
		//rowWelcome.setVisible(false);
		
		this.collapseDates.hide();		
		this.collapseLocation.hide();
	}
	
	//start customize dates section

	@UiHandler({"anchorListItemNextADays","anchorListItemNextBDays","anchorListItemNextCDays","anchorListItemNextDDays"})
	public void handleCustomeDatesOnClick(ClickEvent event)
	{
		moveContainerLeft();
		//rowWelcome.setVisible(false);
		
		Anchor anchor = (Anchor)event.getSource();
		
		String next  = anchor.getText();
		
		Date fromTmp = new Date();				
		CalendarUtil.resetTime(fromTmp);
		
		Date toTmp = null;
		
		String message = null;

		if ( next.equals( TODAY ) )
		{
			this.from = this.formatDateRange(fromTmp);
			toTmp = new Date( fromTmp.getTime()  );
			this.to = this.formatDateRange(toTmp);
			message = TODAY;
		}
		else if( next.equals( TOMORROW ) )
		{
			CalendarUtil.addDaysToDate(fromTmp,1);	
			this.from = this.formatDateRange(fromTmp);
			toTmp = new Date( fromTmp.getTime()  );	
			this.to = this.formatDateRange(toTmp);
			message = TOMORROW;
		}
		else if( next.equals( NEXT_7_DAYS ) )
		{			
			this.from = this.formatDateRange(fromTmp);
			toTmp = new Date( fromTmp.getTime() );			
			CalendarUtil.addDaysToDate(toTmp,6);
			this.to = this.formatDateRange(toTmp);
			message = NEXT_7_DAYS;
		}
		else if(next.equals(NEXT_30_DAYS))
		{
			this.from = this.formatDateRange(fromTmp);
			toTmp = new Date( fromTmp.getTime() );
			CalendarUtil.addDaysToDate( toTmp , 29 );
			this.to = this.formatDateRange(toTmp);
			message = NEXT_30_DAYS;
		}
				
//		if(DeviceType.Phone.equals(deviceType))
//		{
//			this.setDate("", false);
//		}
//		else
//		{
			this.setDate(message, false);
		//}

		this.goToSearchResultsPlace();
	}
	
	
	
	
	@UiHandler({"anchorSearchByA","anchorSearchByB"})
	public void handleSearchByOnClick(ClickEvent event)
	{
		moveContainerLeft();
		//rowWelcome.setVisible(false);
		
		Anchor anchor = (Anchor)event.getSource();
		
		String linkText   = anchor.getText();
		
		IndexKey indexKey = this.getIndexKey( linkText );
		
		this.setSearchBy(indexKey,anchor);
	}
	
	private void setSearchBy(IndexKey indexKey,Anchor anchor)
	{
		String buttonText = this.buttonSearchBy.getText();

		String key = this.getName(indexKey) ;

		//icon text bug
		if( ! key.equals(this.buttonSearchBy.getText()))
		{
			//this.buttonSearchBy.clear();
			this.buttonSearchBy.setText( key );
			this.buttonSearchBy.setIconPosition(IconPosition.LEFT);
			this.buttonSearchBy.setIcon(this.getIcon(key));
			this.buttonSearchBy.setDataToggle(Toggle.DROPDOWN);
		}
		//icon text bug

		if(anchor!=null)
		{
			anchor.setText(buttonText);
		}
		else
		{	
			if(this.anchorSearchByA.getText().equals(key))
			{
				this.anchorSearchByA.setText(buttonText);
			}
			else if(this.anchorSearchByB.getText().equals(key))
			{
				this.anchorSearchByB.setText(buttonText);
			}
		}
		
		//this.factory.setIndexKey( indexKey );
		
		this.anchorSearchByA.setIcon(this.getIcon(this.anchorSearchByA.getText()));
		this.anchorSearchByB.setIcon(this.getIcon(this.anchorSearchByB.getText()));

		this.focus();
	}
	
	@UiHandler({"buttonSearch"})
	public void handleSearchOnClick(ClickEvent event)
	{
		moveContainerLeft();
		
		//rowWelcome.setVisible(false);
		
		this.collapseLocation.hide();
		this.collapseDates.hide();
		
		if(	(this.suggestBoxEventPerformerVenue.getValue()!=null
			&&
			this.suggestBoxEventPerformerVenue.getValue().length()>0)
			||
			(!this.buttonCustomizeLocation.getText().equals(SET_LOCATION))
			||
			(!this.buttonCustomizeDates.getText().equals(SET_DATE)))
		{
			this.goToSearchResultsPlace();
		}
	}

	@Override
	public IndexKey getIndexKey(String searchBy){
		
		if(searchBy==null){
			searchBy = this.buttonSearchBy.getText();
		}
		
		if(EVENT.equals(searchBy)){
			return IndexKey.event;
		}
		if(PERFORMER.equals(searchBy)){
			return IndexKey.performer;
		}
		if(VENUE.equals(searchBy)){
			return IndexKey.venue;
		}
		return null;
	}
	
	private IconType getIcon(String key)
	{
		if(EVENT.equals( key ) ){
			return IconType.FLAG;
		}
		
		if(PERFORMER.equals( key ) ){
			return IconType.STAR;
		}
		
		if(VENUE.equals( key ) ){
			return IconType.BUILDING;
		}
		
		return null;
	}

	@SuppressWarnings("incomplete-switch")
	private String getName(IndexKey indexKey)
	{
		switch (indexKey) {
			case event:
				return EVENT;
			case performer:
				return PERFORMER;
			case venue:
				return VENUE;
		}
		return null;
	}
	
	@Override
	public void goToSearchResultsPlace(){
		
		IndexKey indexKey = this.getIndexKey( buttonSearchBy.getText() );
			
		SearchPlace searchPlace = this.generateRequest( indexKey );
		
		this.presenter.goTo(searchPlace);
		
		this.collapseLocation.hide();
		this.collapseDates.hide();
		this.focus();
	}
	
	private SearchPlace generateRequest(IndexKey queryFieldName)
	{		
		if( this.suggestBoxEventPerformerVenue.getValueBox().getValue()!=null
		&&  this.suggestBoxEventPerformerVenue.getValueBox().getValue().length()>0)
		{
			this.query = this.suggestBoxEventPerformerVenue.getValueBox().getValue() .replace("'","");
		}
		else
		{
			this.query = null;
			queryFieldName = null;
		}
		
		SearchPlace place = new SearchPlace(
				
				queryFieldName,
				this.query,	
				
				this.from,
				this.to,
									
				this.howToSearchLocation,
				this.factory.getSearchLocation()
				);
				
		return place;
	}

	
	
	
	
	@UiHandler({"suggestBoxEventPerformerVenue","suggestBoxLocation"})
	public void onKeyUp(KeyUpEvent event) 
	{
		int nativeKeyCode = event.getNativeKeyCode();
		
		if ( nativeKeyCode == KeyCodes.KEY_ENTER) 
		{
			this.goToSearchResultsPlace();
		}
		
		//this.rowWelcome.setVisible(false);
	}

	public void focus() 
	{
		Scheduler.get().scheduleDeferred(new Command() {
			@Override
			public void execute() {
				
				if( (!deviceType.equals(DeviceType.Phone)) && (!deviceType.equals(DeviceType.Tablet)) )
				{
					//suggestBoxEventPerformerVenue.getValueBox().setFocus(true);	
				}
				
				switch (getIndexKey(null)) {
				case dates:
					
					break;
				case event:
					suggestBoxEventPerformerVenue.setPlaceholder("Search by Event name");
					break;
				case location:
					break;
				case performer:
					suggestBoxEventPerformerVenue.setPlaceholder("Search by Performer/Artist name");
					break;
				case venue:
					suggestBoxEventPerformerVenue.setPlaceholder("Search by Venue name");
					break;		
				}
			}
		});
	}

	@Override
	public void init(SearchPlace place)
	{
		
		//gs
		buttonSearchBy.setType(ButtonType.PRIMARY);
		//gs
		
		
		
		
		this.waiting(true);
		this.columnAboutResults1.setVisible(false);
		this.columnAboutResults2.setVisible(false);
		this.anchorResults.setVisible(false);
		
		collapseLocation.hide();
		collapseDates.hide();


		
		
		this.place = place;
		
		
		
		this.divPagination.setVisible(false);
		
		
		
		
		IndexKey queyFieldName = this.place.getQueryFieldName();
		if(queyFieldName==null)
		{
			queyFieldName = IndexKey.event;
		}
		this.justifySearchBy(queyFieldName);
		
		
		
		if(this.place.isClearSearchView()==true)
		{
			this.suggestBoxEventPerformerVenue.setValue("");
			this.cellTable.setVisible(false);
			this.setDate(SET_DATE, true);
		}
				
		this.cellTable.setVisible(false);
		
		this.createAsyncDataProvider(this.place);
		
	}
	
	private void justifySearchBy(IndexKey indexKey) 
	{
		switch (indexKey) 
		{
			case dates:
				
				this.setSearchBy( IndexKey.event , null );		
				
				Scheduler.get().scheduleDeferred(new Command() {
					@Override
					public void execute() {
						if(		dateTimePickerFrom.getValue()==null && 
								(!deviceType.equals(DeviceType.Phone)) && 
								(!deviceType.equals(DeviceType.Tablet))){
							//dateTimePickerFrom.getTextBox().setFocus(true);
						}}
				});
				
				break;
			case location:
				
				this.setSearchBy( IndexKey.event , null );	
				
				Scheduler.get().scheduleDeferred(new Command() {
					@Override
					public void execute() {
						if(		(!deviceType.equals(DeviceType.Phone)) && 
								(!deviceType.equals(DeviceType.Tablet)) ){
							//suggestBoxLocation.getValueBox().setFocus(true);			
						}
					}
				});
				
				break;
			case event:
			case performer:
			case venue:
				this.setSearchBy( indexKey , null );
				break;
			
			default:
				this.setSearchBy( IndexKey.event , null );
				break;

			 
		}
	}
	
	private void adjustPagination(int totalResultsNumber,int rangeStart)
	{
		//pagination block 
				if( totalResultsNumber > rangeLength )
				{
					this.divPagination.setVisible(true);

					rangeStart++;
					
					int end = 0;
					
					if( ( rangeStart+rangeLength - 1 ) > totalResultsNumber )
					{
						end = totalResultsNumber;
					}
					else
					{
						end = ( rangeStart+rangeLength - 1 );
					}
					
					this.textPagination.setText(
							rangeStart+"-"+end+" of "+totalResultsNumber);
				}
				else
				{
					this.divPagination.setVisible(false);
				}

	}

	private void setDate(String date,boolean reset)
	{
		//icon text bug
		if(reset==true && (!SET_DATE.equals(this.buttonCustomizeDates.getText())))
		{
			this.updateDate(SET_DATE, ButtonType.PRIMARY);
			
			this.dateTimePickerFrom.getTextBox().setText("");
			
			this.from = null;
			this.to=null;
		}
		else
		{
			if( ! date.equals(this.buttonCustomizeDates.getText()) )
			{
				this.updateDate(date, ButtonType.WARNING);
			}
		}
	}
	
	
	private void updateDate(final String text,final ButtonType buttonType)
	{
		//icon text bug

		Scheduler.get().scheduleDeferred(new Command() {
			@Override
			public void execute() {
				//buttonCustomizeDates.clear();
				buttonCustomizeDates.setText( text );
				buttonCustomizeDates.setIconPosition(IconPosition.LEFT);
				buttonCustomizeDates.setIcon(IconType.CALENDAR);
				buttonCustomizeDates.setDataToggle(Toggle.DROPDOWN);
				buttonCustomizeDates.setType(buttonType);
			}
		});
		
		
		
		//icon text bug

	}
	
	
	
	
	
	
	
	
	private void setLocation(String location,boolean reset, IconType iconType)
	{
		//icon text bug
		if(reset==true && (!SET_LOCATION.equals(this.buttonCustomizeLocation.getText())))
		{
			this.updateLocation(SET_LOCATION, ButtonType.PRIMARY , iconType);
		}
		else
		{
			if( ( ! location.equals(this.buttonCustomizeLocation.getText()) ))
			{
				this.updateLocation(location, ButtonType.WARNING , iconType);
			}
		}
	}
	
	private void updateLocation(final String text,final ButtonType buttonType, final IconType iconType)
	{
		//icon text bug

		Scheduler.get().scheduleDeferred(new Command() {
			@Override
			public void execute() 
			{
				//buttonCustomizeLocation.clear();
				buttonCustomizeLocation.setText( text );
				buttonCustomizeLocation.setIconPosition(IconPosition.LEFT);
				buttonCustomizeLocation.setIcon(iconType);
				buttonCustomizeLocation.setDataToggle(Toggle.DROPDOWN);
				buttonCustomizeLocation.setType(buttonType);
			}
		});
				
		//icon text bug

	}
	
	
	@Override
	public void update(SearchResponse searchResponse, String takeTime, int rangeStart, String url) {

		this.searchResponse  = searchResponse;
		
		if(this.searchResponse.getResults().size()==0)
		{
			this.cellTable.setVisible(false);
		}
		else
		{
			this.cellTable.setVisible(true);
		}
		
		
		
		
		
		
		IndexKey queyFieldName = this.searchResponse.getQueryFieldName();
		if(queyFieldName==null)
		{
			queyFieldName = IndexKey.event;
		}
		this.justifySearchBy(queyFieldName);	

		
		
		
		
		
		this.waiting(false);

		
		this.updateAboutResults(takeTime);
		
		
		
		
		
		
		this.adjustPagination(this.searchResponse.getTotalResults(),rangeStart);
		
		StringBuilder title = new StringBuilder();
		
		this.query = this.searchResponse.getQuery();
		this.updateQuery(title,queyFieldName);
		
		
		
		this.updateLocation(title);
		
		this.updateDate(title);
		
		this.updateAddThis(title, url);
		
		//Document.get().setTitle((title.length()==0)?"No results for:"+title.toString():title.toString());	
	
		Utils.setTitle("Compare Tickets Now save up to 30% | "+title.toString()+" tickets");
		Utils.setDescription("Get web site that help you Find and Compare ticket prices");
		
		//---------
		
//		NotifySettings settings = NotifySettings.newSettings();
//		  settings.setTemplate("<div><iframe width=\"350\" height=\"207\" src=\"https://www.youtube.com/embed/8D2JFfvIwS0\" frameborder=\"10\"></iframe></div>");
//		  //settings.makeDefault();
//		  settings.setAllowDismiss(true);
//		  settings.setPauseOnMouseOver(true);
//		  settings.setTimer(60000);
//		  
//		  
//		  settings.setPlacement(NotifyPlacement.BOTTOM_LEFT);
//
//		Notify.notify("",settings);
//		
//
//		  settings.setPlacement(NotifyPlacement.BOTTOM_LEFT);
//
//		Notify.notify("",settings);
		
		
		//---------------
		
		
		
		
		
		
	}
	
	private void updateAboutResults(String takeTime) 
	{	
		String message = null;

		String city = new String(this.searchResponse.getSearchLocation().getCity());
		String segmentKey = new String(this.searchResponse.getSearchLocation().getSegmentedKey());
		String location = this.getLocation(city,segmentKey);
		
		switch ( this.searchResponse.getHowToSearchLocationResolved() ) 
		{
			case city:
				//this.anchorResults.setIcon(IconType.PLUS_SQUARE);
				message = this.getAboutResultsMessage(takeTime,location);
				this.anchorResults.setText("Search around "+location+"?");
				this.columnAboutResults1.setVisible(true);
				this.columnAboutResults2.setVisible(true);
				this.anchorResults.setVisible(true);
				break;
			case geo:
				//this.anchorResults.setIcon(IconType.PLUS_SQUARE);
				message = this.getAboutResultsMessage(takeTime,location);
				this.anchorResults.setText("Search in all?");
				this.columnAboutResults1.setVisible(true);
				this.columnAboutResults2.setVisible(true);
				this.anchorResults.setVisible(true);
				break;	
			case ignore:
				message = this.getAboutResultsMessage(takeTime,null);
				this.columnAboutResults1.setVisible(true);
				this.columnAboutResults2.setVisible(false);	
				this.anchorResults.setVisible(false);
				break;
		}
		
		this.paragraphResults.setHTML(message);

		
		if(this.searchResponse.getTotalResults()==0 && this.searchResponse.getSuggested()!=null)
		{
			String suggested = "Did you mean? "+Utils.capitalize(this.searchResponse.getSuggested(), " ");			
			this.anchorResults.setIcon(null);
			this.anchorResults.setText(suggested);
			this.columnAboutResults1.setVisible(true);
			this.columnAboutResults2.setVisible(true);
			this.anchorResults.setVisible(true);
		}
		
		//Ignore location and date filters and show me all results ???
		
		
	}
	

	@UiHandler({"anchorResults"})
	public void handleAnchorResultsOnClick(ClickEvent event)
	{
		this.moveContainerLeft();
	//	this.rowWelcome.setVisible(false);
		
		Anchor anchor = (Anchor)event.getSource(); 
		
		if(anchor.getText().contains("Did you mean?"))
		{
			this.suggestBoxEventPerformerVenue.setValue(this.searchResponse.getSuggested());
			this.goToSearchResultsPlace();
			return;
		}
		
		if(this.howToSearchLocation.equals(QueryLocationIndex.city))
		{
			this.howToSearchLocation = QueryLocationIndex.geo;

			this.goToSearchResultsPlace();
		}
		else if(this.howToSearchLocation.equals(QueryLocationIndex.geo))
		{
			this.howToSearchLocation = QueryLocationIndex.ignore;
			
			this.factory.setSearchLocation(null);

			this.goToSearchResultsPlace();
		}
	}
	
	
	private String getAboutResultsMessage(String takeTime,String location)
	{
		String message = null;
		
		String connector = null;
		
		switch (this.searchResponse.getHowToSearchLocationResolved()) {
		case city:
			connector = " in ";
			message = "Found <strong>"+this.searchResponse.getTotalResults()+"</strong> results<strong>"+connector+location+"</strong> ("+takeTime+" seconds)";
			return message;
		case geo:
			connector = " around ";
			message = "Found <strong>"+this.searchResponse.getTotalResults()+"</strong> results<strong>"+connector+location+"</strong> ("+takeTime+" seconds)";
			return message;
		case ignore:
			message = "Found <strong> "+this.searchResponse.getTotalResults()+"</strong> results ("+takeTime+" seconds)";
			return message;
		}
		
		return null;
	}

	private void updateQuery(StringBuilder title,IndexKey queyFieldName)
	{
		if(this.query!=null && this.query.length()>0)
		{
			this.query = Utils.capitalize( this.query, "-");
			this.query = Utils.capitalize( this.query, " ");
			
			title.append( this.query );
		
			this.suggestBoxEventPerformerVenue.setValue( this.query );		
			
			this.suggestBoxEventPerformerVenue.setTitle(query);
			
			this.buttonSearchBy.setType(ButtonType.WARNING);
		}
	}
	
	private void updateLocation(StringBuilder title)
	{
		if(this.searchResponse.getHowToSearchLocationResolved().equals(QueryLocationIndex.city))
		{
			String city = new String(this.searchResponse.getSearchLocation().getCity());
			String segmentKey = new String(this.searchResponse.getSearchLocation().getSegmentedKey());
			
			String location = this.getLocation(city,segmentKey);
			
			this.setLocation(location,false,IconType.MAP_MARKER);

			this.suggestBoxLocation.setValue( location );
			
			this.anchorListItemLocationKeepOriginalLocation.setText( location );
			
			this.anchorListItemLocationSearchAround.setText( SEARCH_AROUND+" "+location );
			
			this.howToSearchLocation = QueryLocationIndex.city;
			
			title.append( " "+location );	
		}
		else if(this.searchResponse.getHowToSearchLocationResolved().equals(QueryLocationIndex.geo))
		{
			this.suggestBoxLocation.setValue("");

			String city = new String(this.searchResponse.getSearchLocation().getCity());
			String segmentKey = new String(this.searchResponse.getSearchLocation().getSegmentedKey());
		
			String location = this.getLocation(city,segmentKey);

			this.suggestBoxLocation.setValue( location );

			this.anchorListItemLocationKeepOriginalLocation.setText( location );
			
			this.anchorListItemLocationSearchAround.setText( SEARCH_AROUND +" "+ location );
			
			this.howToSearchLocation = QueryLocationIndex.geo;
			
			this.setLocation(SEARCH_AROUND +" "+ location, false, IconType.DOT_CIRCLE_O);
			
			title.append( " "+location );	

		}
		else if(this.searchResponse.getHowToSearchLocationResolved().equals(QueryLocationIndex.ignore))
		{
			this.howToSearchLocation = QueryLocationIndex.ignore;
			
			//init user current location
			String city = new String(this.searchResponse.getCurrentUserLocation().getCity());
			String segmentKey = new String(this.searchResponse.getCurrentUserLocation().getSegmentedKey());
		
			String location = this.getLocation(city,segmentKey);

			this.setLocation(SET_LOCATION,true,IconType.MAP_MARKER);

			this.anchorListItemLocationKeepOriginalLocation.setText( location );
			
			this.anchorListItemLocationSearchAround.setText( SEARCH_AROUND +" "+ location );

			this.suggestBoxLocation.setValue( location );
			
			title.append( " "+location );	
		}
	}
	
	
	
	private void updateAddThis(StringBuilder title,String url)
	{
		if(title.length()==0)
		{
			
			this.factory.setAddThis(url,"Search tickets by event, performer, venue... - compareticketsnow.com" );
		}
		else
		{
			this.factory.setAddThis(url, title.toString() );
		}
	}
	
	private void updateDate(StringBuilder title)
	{
		String datesTitle = null;
		if( this.searchResponse.getFrom()!=null && this.searchResponse.getTo()!=null && this.from==null && this.to==null)
		{
			this.from = this.searchResponse.getFrom();
			this.to = this.searchResponse.getTo();
			
			datesTitle = this.getDatesTitle(this.from,this.to);
			
//			if(DeviceType.Phone.equals(deviceType))
//			{
//				this.setDate("", false);
//			}
//			else
//			{
				this.setDate(datesTitle, false);
			//}
			
			title.append( " "+datesTitle );		
		}
		else
		{
			datesTitle = this.getDatesTitle(this.from,this.to);

			title.append( " "+datesTitle );		
		}
	}
	
	
	
	private String getDatesTitle(String from,String to)
	{
		String dates = "";
		
		if(from!=null&&to!=null)
		{
			if(from.equals(to))
			{
				dates = from;
			}
			else
			{
				dates = from+" - "+ to;
			}
		}
		
		return dates;
	}
	
	
	
	
	
	
	
	
	private void buildGrid(DeviceType deviceType) 
	{
		switch (deviceType) 
		{
			case Phone:

				columnVerticalHr = buildColumnVerticalHr(120);
				cellTable.setColumnWidth( 0 , "1%");
				cellTable.addColumn(columnVerticalHr);
				
				columnEventPerformerVenueLocation = buildColumnEventPerformerVenueLocation();
				cellTable.setColumnWidth( 1 , "95%");
				cellTable.addColumn(columnEventPerformerVenueLocation);
				
				columnButtonTickets = buildColumnButtonTickets();
				cellTable.setColumnWidth(2, "4%");//tickets
				cellTable.addColumn(columnButtonTickets);
	
				break;
			
			case LargeDesktop:
			case MediumDesktop:
			case Tablet:
				
				columnDate = buildColumnDate();
				cellTable.setColumnWidth( 0 , "20%");
				cellTable.addColumn(columnDate);

				columnVerticalHr = buildColumnVerticalHr(100);
				cellTable.setColumnWidth( 1 , "2%");
				cellTable.addColumn(columnVerticalHr);
				
//				this.columnEventImage = this.buildColumnEventImage();
//				this.cellTable.setColumnWidth(0, "10%");
//				this.cellTable.addColumn(this.columnEventImage);
				
				columnEventPerformerVenueLocation = buildColumnEventPerformerVenueLocation();
				cellTable.setColumnWidth( 2 , "68%");
				cellTable.addColumn(columnEventPerformerVenueLocation);

				columnButtonTickets = buildColumnButtonTickets();
				cellTable.setColumnWidth(3, "10%");//tickets
				cellTable.addColumn(columnButtonTickets);

				//this.buttonSearchBy.setWidth("165px");
				//this.buttonSearch.setWidth("70px");
				//this.inputGroupAddonLocation.setWidth("164px");
				//this.buttonCustomizeDates.setWidth("165px");

				break;
		}

		
		
		pagination.clear();
				 
	}

	private Column<SearchResult, ImageResource> buildColumnEventImage() {
		
		this.columnEventImage = new Column<SearchResult,ImageResource>(new ImageResourceCell())
		{
			@Override
			public ImageResource getValue(SearchResult ticketsBlock) {
				
				return null;//Resources.instance.background1();
				
				
//				switch (ticketsBlock.getTickets().getSource()) 
//				{
//					case ticketcity:
//						return Resources.instance.ticketcity();
//						
//					case ticketnetwork:
//						return Resources.instance.tndirect();
//				}
						
			}
		};	
	
		columnEventImage.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		columnEventImage.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
		return columnEventImage;
	}

	
	private static String emphasisTag = "strong";//mark
	private Column<SearchResult, SafeHtml> buildColumnEventPerformerVenueLocation() 
	{
		this.columnEventPerformerVenueLocation = new Column<SearchResult,SafeHtml>(new SafeHtmlCell())
		{
			@Override
			public SafeHtml getValue(SearchResult object) 
			{
				String event = object.getEventName();
				String performer = getPerformers( object.getPerformersName() );
				
				
				IndexKey indexKey = getIndexKey(null);
				
				SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
				
				if(deviceType.equals(DeviceType.Phone))
				{
					safeHtmlBuilder.appendHtmlConstant("<p class=\"text-primary\">");
					safeHtmlBuilder.appendHtmlConstant("<span style=\"margin-right:3px\" class=\"fa fa-calendar\" aria-hidden=\"true\"></span>");						
					SafeHtml safeHtml = getDate(object.getDate(),false);
					safeHtmlBuilder.append(safeHtml);
					safeHtmlBuilder.appendHtmlConstant("</p>");		
				}
				
				switch (indexKey) 
				{
					case event:
					case dates:
					case location:
					
						safeHtmlBuilder.appendHtmlConstant("<p class=\"text-primary\">");
						safeHtmlBuilder.appendHtmlConstant("<span style=\"margin-right:3px\" class=\"fa fa-flag\" aria-hidden=\"true\"></span>");						
						safeHtmlBuilder.append(Utils.newBold(event, query,emphasisTag));
						safeHtmlBuilder.appendHtmlConstant("</p>");

						safeHtmlBuilder.appendHtmlConstant("<p class=\"text-muted\">");
						safeHtmlBuilder.appendHtmlConstant("<span style=\"margin-right:3px\" class=\"fa fa-building\" aria-hidden=\"true\"></span>");
						safeHtmlBuilder.appendEscaped(object.getVenueName()+" ");
						safeHtmlBuilder.appendHtmlConstant("</p>");	
							
						safeHtmlBuilder.append(getLocation(object));
						
						break;
						
					case performer:
						
						safeHtmlBuilder.appendHtmlConstant("<p class=\"text-primary\">");
						safeHtmlBuilder.appendHtmlConstant("<span style=\"margin-right:3px\" class=\"fa fa-star\" aria-hidden=\"true\"></span>");
						safeHtmlBuilder.append(Utils.newBold(performer, query,emphasisTag));
						safeHtmlBuilder.appendHtmlConstant("</p>");
						
						safeHtmlBuilder.appendHtmlConstant("<p class=\"text-muted\">");
						safeHtmlBuilder.appendHtmlConstant("<span style=\"margin-right:3px\" class=\"fa fa-building\" aria-hidden=\"true\"></span>");
						safeHtmlBuilder.appendEscaped(object.getVenueName()+" ");
						safeHtmlBuilder.appendHtmlConstant("</p>");					
						
						safeHtmlBuilder.append(getLocation(object));

						break;
						
					case venue:
						
						safeHtmlBuilder.appendHtmlConstant("<p class=\"text-primary\">");
						safeHtmlBuilder.appendHtmlConstant("<span style=\"margin-right:3px\" class=\"fa fa-building\" aria-hidden=\"true\"></span>");
						safeHtmlBuilder.append(Utils.newBold(object.getVenueName(), query,emphasisTag));
						safeHtmlBuilder.appendHtmlConstant("</p>");					

						safeHtmlBuilder.appendHtmlConstant("<p class=\"text-muted\">");
						safeHtmlBuilder.appendHtmlConstant("<span style=\"margin-right:3px\" class=\"fa fa-flag\" aria-hidden=\"true\"></span>");
						safeHtmlBuilder.appendEscaped(event);
						safeHtmlBuilder.appendHtmlConstant("</p>");
						
						safeHtmlBuilder.append(getLocation(object));
						
						break;
				}

				return safeHtmlBuilder.toSafeHtml();
			}
		};
		
		return columnEventPerformerVenueLocation;
	}
	
	private SafeHtml getLocation(SearchResult object)
	{
		// if user in la city level - show city as la even if have hollywood and so on
		// if user in la geo level - show la if have
		// if user in ignore location - show randomaly ?
				
		String location = null;
		
		switch (this.howToSearchLocation) 
		{	
			case city:
				location = getLocation(object,",",true);
				break;
				
			case geo:
				location = getLocation(object,",",true);
				break;
				
			case ignore:
				location = getLocation(object,",",true);
				break;
		}
		
		SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
		safeHtmlBuilder.appendHtmlConstant("<p class=\"text-muted\">");
		safeHtmlBuilder.appendHtmlConstant("<span style=\"margin-right:3px\" class=\"fa fa-map-marker\" aria-hidden=\"true\"></span>");
		safeHtmlBuilder.appendEscaped(location);
		safeHtmlBuilder.appendHtmlConstant("</p>");	
		return safeHtmlBuilder.toSafeHtml();
	}
	
	
	private String getPerformers(List<String> performers)
	{
		StringBuilder result = new StringBuilder();
		
		for( String performer : performers )
		{
			result.append(performer+",");
		}
		
		return result.deleteCharAt(result.length()-1).toString();
	}

	private Column<SearchResult, String> buildColumnButtonTickets() {
		
		org.gwtbootstrap3.client.ui.gwt.ButtonCell buttonCell = null;

		if(this.deviceType.equals(DeviceType.Phone))
		{
			//buttonCell = new org.gwtbootstrap3.client.ui.gwt.ButtonCell(IconType.CHEVRON_RIGHT,ButtonType.LINK,ButtonSize.LARGE);
			buttonCell = new org.gwtbootstrap3.client.ui.gwt.ButtonCell(IconType.TICKET,ButtonType.SUCCESS,ButtonSize.DEFAULT);
		}
		else
		{
//			buttonCell = new org.gwtbootstrap3.client.ui.gwt.ButtonCell(IconType.CHEVRON_RIGHT,ButtonType.SUCCESS,ButtonSize.DEFAULT);
			buttonCell = new org.gwtbootstrap3.client.ui.gwt.ButtonCell(IconType.TICKET,ButtonType.SUCCESS,ButtonSize.DEFAULT);

		}
	
		columnButtonTickets = new Column<SearchResult, String>(buttonCell){
		
		@Override
		public String getValue(SearchResult object){
				
			if(deviceType.equals(DeviceType.Phone))
			{
				return "Tickets";
			}
			else
			{
				return "View Tickets";
			}
		}};
		
		columnButtonTickets.setFieldUpdater(new FieldUpdater<SearchResult, String>(){
				@Override
				public void update(int index, SearchResult object, String value){
									
					toCompare(object);
					
				}
		});
		
		
		
		columnButtonTickets.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_JUSTIFY);
		columnButtonTickets.setVerticalAlignment(HasVerticalAlignment.ALIGN_BOTTOM);		
		
		return columnButtonTickets;
	}

	private void toCompare(SearchResult object)
	{
		ComparePlace comparePlace = new ComparePlace(
				object.getEventName(),
				object.getDate(),
				object.getRelatedCityNames(),
				object.getSegmentKey(),
				object.getVenueName(),
				object.getSources(),
				this.place);
		
		presenter.goTo(comparePlace);
	}
	
	private Column<SearchResult, SafeHtml> buildColumnDate()
	{
		this.columnDate = new Column<SearchResult, SafeHtml>(new SafeHtmlCell()){
			@Override
			public SafeHtml getValue(SearchResult object) 
			{
				return getDate(object.getDate(),true);
			}
		};
		
		this.columnDate.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		this.columnDate.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);		
		return columnDate;
	}
	
	
	private Column<SearchResult, SafeHtml> buildColumnVerticalHr(final int height)
	{
		this.columnVerticalHr = new Column<SearchResult, SafeHtml>(new SafeHtmlCell()){
			@Override
			public SafeHtml getValue(SearchResult object) 
			{
				SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();	
				
				int sources = object.getSources().size();
				String color = null;
				String markets = "";
				if(sources==1)
				{
					color = "#ddd";
					//markets = "Box";
				}
				else
				{
					color = "#75AF1D";
					//markets = "M="+sources;

				}
				
				
				//height:80px
			
//				safeHtmlBuilder.appendHtmlConstant(
//						"<div style=\"border-left:1px solid "+color+";height:80px;\">"+markets+"</div>");
				
				safeHtmlBuilder.appendHtmlConstant(
						"<div style=\"border-left:1px solid "+color+";min-height:"+height+"px;overflow: hidden;\">"+markets+"</div>");
				
				return safeHtmlBuilder.toSafeHtml();		
			}
		};
		
		this.columnVerticalHr.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		this.columnVerticalHr.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);		
		return columnVerticalHr;
	}

	private SafeHtml getDate(String date,boolean br)
	{
		SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
		
		String date_[] = date.split(",");
		
		safeHtmlBuilder.appendHtmlConstant("<b>");
		safeHtmlBuilder.appendHtmlConstant(date_[0]);
		safeHtmlBuilder.appendHtmlConstant("</b>");
		if(br==true)
		{
			safeHtmlBuilder.appendHtmlConstant("</br>");
		}
		safeHtmlBuilder.appendHtmlConstant(" "+date_[1]);
		return safeHtmlBuilder.toSafeHtml();
	}
	
	private void createAsyncDataProvider(SearchPlace place) {
		
		if(this.asyncDataProvider!=null)
		{
			asyncDataProvider.removeDataDisplay(this.cellTable);
		}

		this.asyncDataProvider = 
				new SearchAsyncDataProvider(
						this,
						this.simplePager,
						this.pagination,
						this.presenter,
						place,
						this.factory
						);
		
		this.cellTable.setVisibleRangeAndClearData( new Range(0,rangeLength) , false );
		
		this.simplePager.setDisplay(this.cellTable);
				
		this.asyncDataProvider.addDataDisplay(this.cellTable);
	}
	
	@Override
	public void setSearchPresenter(SearchPresenter presenter) 
	{
		this.presenter = presenter;
	}











	

//	

//	@Override
//	public void waiting(boolean show) 
//	{
//		if(show==true)
//		{
//			waitingView = new WaitingView("Searching events...");
//			this.divWaiting.add(waitingView);
//		}
//		else
//		{
//			Scheduler.get().scheduleDeferred(new Command() {
//				@Override
//				public void execute() {
//					divWaiting.remove(waitingView);
//				}
//			});
//		}
//	}
}