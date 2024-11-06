package com.ctnf.client.activities.compare.xs.sm;
import java.util.Comparator;
import java.util.List;

import org.gwtbootstrap3.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Column;
import org.gwtbootstrap3.client.ui.Container;
import org.gwtbootstrap3.client.ui.DropDownMenu;
import org.gwtbootstrap3.client.ui.Icon;
import org.gwtbootstrap3.client.ui.Image;
import org.gwtbootstrap3.client.ui.Pagination;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.gwt.CellTable;
import org.gwtbootstrap3.client.ui.html.Div;
import org.gwtbootstrap3.client.ui.html.Paragraph;

import com.ctnc.shared.IndexKey;
import com.ctnc.shared.QueryLocationIndex;
import com.ctnc.shared.TicketsBlock;
import com.ctnf.client.DeviceType;
import com.ctnf.client.Factory;
import com.ctnf.client.PaginationCustom;
import com.ctnf.client.Resources;
import com.ctnf.client.activities.compare.AboutTickets;
import com.ctnf.client.activities.compare.AlphanumComparator;
import com.ctnf.client.activities.compare.AlphanumComparator.SortBy;
import com.ctnf.client.activities.compare.ComparePresenter;
import com.ctnf.client.activities.compare.CompareView;
import com.ctnf.client.activities.compare.CompareViewImplCommon;
import com.ctnf.client.activities.search.SearchPlace;
import com.ctnf.client.utils.Utils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.dom.client.Style.FontStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.RowStyles;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.ColumnSortList.ColumnSortInfo;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.RangeChangeEvent;

public class CompareViewImpl extends Composite implements CompareView {

	private static CompareViewImplUiBinder uiBinder = GWT.create(CompareViewImplUiBinder.class);
	interface CompareViewImplUiBinder extends UiBinder<Widget, CompareViewImpl> {}

	private CompareViewImplCommon common = new CompareViewImplCommon();
	
	private int pageSize = 30;
		
	@UiField Column columnTicketsList;
			
	@UiField Row rowTitle;

	@UiField Anchor anchorHideShowMap;
	
	@UiField Icon iconWaitingForTickets,iconWaitingForMap;
		
	@UiField ScrollPanel scrollPanelTicketsList;
	
	@UiField Div divFilters,divMap,divTicketsListAndPaginationWrapper;
	
	@UiField Button buttonEventInfo,buttonGoHome;
	
	@UiField DropDownMenu dropDownMenuEventInfo;
	
	@UiField AnchorListItem anchorListItemEventLocation,anchorListItemEventDate;
	
	@UiField Button buttonBackToSearch,buttonEventName,buttonEventVenue;
	
	@UiField org.gwtbootstrap3.client.ui.Column columnInfo;
	
	@UiField(provided = true) CellTable<TicketsBlock> cellTable;
	
	@UiField(provided = true) Pagination pagination;
	
	@UiField Paragraph paragraphPagination;
	
	@UiField(provided = true) Image imageLogoCompareTicketsNow;
	
	private int scrollPanelTicketsListMinHeightPercent = 55;
	private int scrollPanelTicketsListMaxHeightPercent = 95;
	
	@UiHandler("anchorHideShowMap")
	public void onHideShowMap(ClickEvent event)
	{
		if(anchorHideShowMap.getText().equals("Hide map"))
		{	
			anchorHideShowMap.setText("Show map");	
			
			this.common.setHeight(this.scrollPanelTicketsList, scrollPanelTicketsListMaxHeightPercent);
			
			this.disableMap();
		}
		else
		{	
			anchorHideShowMap.setText("Hide map");	
			
			this.common.setHeight(this.scrollPanelTicketsList, scrollPanelTicketsListMinHeightPercent);
			
			this.enableMap();
		}
	}
	
	@Override
	public void sectionsBlinking(List<String> sections,boolean blinking){
		
		if(this.common.isUpdateMap()==true)
		{
			common.sectionsBlinking(sections, blinking);
		}
	}
	
	private native void initMapNative(
			CompareViewImpl this_, 
			CompareViewImplCommon common,
			JavaScriptObject[] ticketsArray,
			String mapId,
			String venueName,String eventName,String eventTime,
			boolean /*true/false*/singleSectionSelection,
			String failoverMapUrl,
			String /*250, 500, 1000, 1500*/tooltipSectionViewSize,
			String deviceType,String url
			) /*-{
											
		failoverMapUrl = "https://static.ticketutils.com/Charts/No-Seating-Chart.jpg";
		tooltipSectionViewSize = 250;
		
		if (mapId == null) 
		{
	        MapId = null;
	        eventInfo = 
	        {
	            Venue: venueName,
	            EventName: eventName,
	            EventDate: eventTime
        	};
        	
            console.log("Request map by event info: "+ JSON.stringify(eventInfo));
    	}
    	else 
    	{
    	    console.log("Request map id: "+ mapId);

        	eventInfo = null;
    	}

		$wnd.jQuery(document).ready(function (){
		
			setTimeout(function(){
				
				try{
				
				$wnd.jQuery("#divMap").tuMap({
					  ServiceUrl: "https://imap.ticketutils.net"
					, MapId: mapId
                    , EventInfo: eventInfo
					, ColorScheme: 1
					, FailoverMapUrl: failoverMapUrl
					, Tickets: ticketsArray
					, TooltipSectionViewSize:tooltipSectionViewSize
					, EnableTooltipSectionView: true
					, AnimateTooltip:false
					, SingleSectionSelection:singleSectionSelection 
					, FailoverMapUrl: failoverMapUrl
                    , AutoSwitchToStatic: true
                    , MapSet: "tu"
					, OnInit: function (e, Data) {
			           $wnd.jQuery("#divMap").tuMap("RemoveMapControl", "Reset");
			           $wnd.jQuery("#divMap").tuMap("RemoveMapControl", "Unmapped");
			           $wnd.jQuery("#divMap").tuMap("RemoveMapControl", "Parking");
			           
			  		    common.@com.ctnf.client.activities.compare.CompareViewImplCommon::setMapType(Ljava/lang/String;)(Data.MapType);
			           
			           //updateZoom(deviceType);
            		   //resizeWindow();
			        }
//			        , OnGroupClick:function(e,Group){
//						if(Group.Selected) {
//							alert("Selected Group " + Group.Name);
//						}
//					}
			        , OnError:function(e,Error){
                         console.log("Seating charts error: code: "+ Error.Code + " - " + Error.Message);
                         
                         if(Error.Code==1||Error.Code==0)
                         {
							this_.@com.ctnf.client.activities.compare.xs.sm.CompareViewImpl::noSeatingCharts(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)(eventTime,eventName,venueName,url);
                         }
                 
                      }
					, OnClick:function(e,Section){
						if(Section.Active)
						{
							console.log("------------------");
							
							if(Section.Selected)
							{
								console.log( "Selected Section: "+ Section.Name +" in Group: "+ Section.Group.Name);
								
								
								try {
							        var selectedSections = $wnd.jQuery("#divMap").tuMap("GetSelectedSections");
							        console.log("selectedSections: "+selectedSections.toString());
							        
							    } catch (e) {
							    }
								
								common.@com.ctnf.client.activities.compare.CompareViewImplCommon::setSelectedSectionsFromMap_([Ljava/lang/String;)(selectedSections);
								
								console.log("Section.Mappings: "+Section.Mappings.toString());
								
								if (! typeof Section.Alias === 'undefined') 
								{
									console.log("Section.Alias: "+Alias.Mappings.toString());
								}
								
								console.log("Section.Group: "+JSON.stringify(Section.Group));
								
								console.log("Section.Key: "+Section.Key);
								
							}
							else
							{
								console.log( "Deselected Section "+ Section.Name +" in Group "+ Section.Group.Name);
								
								try {
							        var selectedSections = $wnd.jQuery("#divMap").tuMap("GetSelectedSections");
							        console.log("selectedSections: "+selectedSections.toString());
							        
							    } catch (e) {
							    }
							    
							    common.@com.ctnf.client.activities.compare.CompareViewImplCommon::setSelectedSectionsFromMap_([Ljava/lang/String;)(selectedSections);
							    								
								console.log("Section.Mappings: "+Section.Mappings.toString());
								
								if (! typeof Section.Alias === 'undefined') 
								{
									console.log("Section.Alias: "+Alias.Mappings.toString());
								}
								
								console.log("Section.Group: "+JSON.stringify(Section.Group));
								
								console.log("Section.Key: "+Section.Key);
								
							}
						}
					}
				});
				
				}
				catch(error)
				{
					console.log(error.message);
				}
				finally 
				{
					//remove waiting indicator
					this_.@com.ctnf.client.activities.compare.xs.sm.CompareViewImpl::afterMapInit()();
				}
				
			},2000);//timeout
		});	
		
		
		function updateZoom(deviceType) 
		{    
			switch (deviceType)
			{
				case "LargeDesktop":
		
					$wnd.jQuery("#divMap").tuMap("SetOptions", {ZoomLevel: 1});
					console.log("LargeDesktop --> ZoomLevel: 1");
				
					break;
					
				case "MediumDesktop":
		
					$wnd.jQuery("#divMap").tuMap("SetOptions", {ZoomLevel: 4});
					console.log("MediumDesktop --> ZoomLevel: 4");
			
					break;
		
				case "Tablet":
		
			       	$wnd.jQuery("#divMap").tuMap("SetOptions", {ZoomLevel: 3});
					console.log("Tablet --> ZoomLevel: 3");
			       
					break;
				
				case "Phone":
		
		            $wnd.jQuery("#divMap").tuMap("SetOptions", {ZoomLevel: 1});
		            console.log("Phone --> ZoomLevel: 1");
		            
					break;
			} 
		
//		    var zoomLevel = 3;
//		    var defaultZoomlevel = 3;
//		    //width of map wrapper, to
//		    var maxMapSize = $('#divTicketFilters').width() - $('#ticketList').width();
//		    //round down to nearest 100
//		    maxMapSize = Math.floor(maxMapSize / 100) * 100;
//		    if (maxMapSize >= 400 && maxMapSize <= 3400) {
//		        //add switch that maps each 100 px to a zoom level
//		
//		        switch (maxMapSize) {
//		            case 400:
//		                zoomLevel = 2;
//		                break;
//		            case 500:
//		                zoomLevel = 2;
//		                break;
//		            case 600:
//		                zoomLevel = 2;
//		                break;
//		            case 700:
//		                zoomLevel = 2;
//		                break;
//		            case 800:
//		                zoomLevel = 3;
//		                break;
//		            case 900:
//		                zoomLevel = 3;
//		                break;
//		            case 1000:
//		                zoomLevel = 4;
//		                break;
//		            case 1100:
//		                zoomLevel = 4;
//		                break;
//		            case 1200:
//		                zoomLevel = 5;
//		                break;
//		            case 1300:
//		                zoomLevel = 5;
//		                break;
//		            case 1400:
//		                zoomLevel = 5;
//		                break;
//		            case 1500:
//		            case 1600:
//		            case 1700:
//		            case 1800:
//		            case 1900:
//		            case 2000:
//		                zoomLevel = 5;
//		                break;
//		            default:
//		                zoomLevel = defaultZoomlevel;
//		        }
//	    }
//	    else {
//	        zoomLevel = 1;
//	    }

	}
	
	}-*/;  

	private void noSeatingCharts(String eventTime,String eventName,String venueName,String url)
	{
		common.noSeatingCharts = true;
		
		Scheduler.get().scheduleDeferred(new Command() {
			@Override
			public void execute() {
				iconWaitingForMap.setVisible(false);
				divMap.setVisible(false);
				anchorHideShowMap.setVisible(false);
				common.setHeight( scrollPanelTicketsList, scrollPanelTicketsListMaxHeightPercent);
			}
		});
		
		this.common.presenter.noSeatingChartReport("No seating chart for: "+eventTime+" [event] "+eventName+" [venue] "+venueName,url);
	}

	private void afterMapInit()
	{	
		Scheduler.get().scheduleDeferred(new Command() {
			@Override
			public void execute() {
				iconWaitingForMap.setVisible(false);
				if(common.noSeatingCharts==true){
					iconWaitingForMap.setVisible(false);	
					divMap.setVisible(false);	
				}
				else
				{
				}
			}
		});
	}
	
	@Override
	public void initMap(final JavaScriptObject[] ticketsBlockAsJavaScriptObject, 
						final String mapId, 
						final String dateForMap,final String[] eventSourceName, final String[] eventSourceVenue,final String url){				
		
		final CompareViewImpl this_ = this;
		
		// only if map exist add check here
		Scheduler.get().scheduleDeferred(new Command() {
			@Override
			public void execute() {

				// delay execution for 1 sec
				Timer timer = new Timer() {
					@Override
					public void run() {

						String eventName = common.chooseEntryForMap(eventSourceName);
						String venueName = common.chooseEntryForMap(eventSourceVenue);
						boolean singleSectionSelection = common.deviceType.equals(DeviceType.Phone)?true:false;
					
						//test
//						String mapId = null;
//
//						switch (Random.nextInt(2)) {
//						case 0:
//							mapId = "24d98d09-37e1-437f-87c5-eae845692e6c";// "30c04c8b-6bdc-4e8c-b1a8-5e2772de3fc3";
//
//							break;
//						case 1:
//							mapId = null;//"24d98d09-37e1-437f-87c5-eae845692e6c";
//							break;
//						}
						//test
						
						initMapNative(this_,common,ticketsBlockAsJavaScriptObject,mapId,
								venueName,eventName,dateForMap,singleSectionSelection,null,null,
								common.deviceType.name(),url);
					}
				};
				timer.schedule(10);
			}
		});
	}
	
	@Override
	public void updateMap(List<TicketsBlock> data)
	{
		if(this.common.isUpdateMap()==true)
		{
			common.updateMap(data);	
		}
	}
	
	@Override
	public void updateMap(JavaScriptObject[] data)
	{
		if(this.common.isUpdateMap()==true)
		{
			common.updateMap(data);
		}
	}
	
	public CompareViewImpl() 
	{
		this.imageLogoCompareTicketsNow = new Image(Resources.instance.compareticketsnow());
		this.imageLogoCompareTicketsNow.getElement().getStyle().setMarginTop(20,Unit.PX);
		this.imageLogoCompareTicketsNow.getElement().getStyle().setMarginBottom(20,Unit.PX);
		
		this.cellTable = new CellTable<TicketsBlock>();
		this.pagination = new PaginationCustom();	
		
		initWidget(uiBinder.createAndBindUi(this));
		
		this.buildGrid( common.deviceType );
		
		this.common.listDataProvider = new ListDataProvider<TicketsBlock>();
		
		this.common.listDataProvider.addDataDisplay(this.cellTable);
		
		this.addColumnSortListHandling();
		
		this.cellTable.setPageSize(pageSize);
		
		this.common.simplePager.setDisplay(this.cellTable);
	
		//this.buttonEventInfo.getElement().getStyle().setBorderColor("#337ab7");
		//this.buttonEventInfo.getElement().getStyle().setColor("#337ab7");
		//this.buttonGoHome.getElement().getStyle().setBorderColor("#337ab7");
		
		common.setHeight( scrollPanelTicketsList, scrollPanelTicketsListMinHeightPercent);
		//this.scrollPanelTicketsList.getElement().getStyle().setPaddingBottom(50, Unit.PX);


		this.setOnCellPreview();

		//this.cellTable.getElement().getStyle().setFontStyle(FontStyle.NORMAL);
	//	this.cellTable.getElement().getStyle().setFontSize(15,Unit.PX);
//		this.cellTable.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		this.cellTable.getElement().getStyle().setColor("#337ab7");	
		
//		this.cellTable.getElement().getStyle().setBorderColor("#337ab7");
//		this.cellTable.getElement().getStyle().setBorderStyle(BorderStyle.DOTTED);
//		this.cellTable.getElement().getStyle().setBorderWidth(1,Unit.PX);
		
//		Scheduler.get().scheduleDeferred(new Command() {
//			@Override
//			public void execute() 
//			{	
//				common.filters = common.presenter.getFactory().getCompareFilterView(common.deviceType).asWidget();
//				common.filters.getElement().getStyle().setMarginTop(5,Unit.PX);
//				common.filters.getElement().getStyle().setMarginBottom(10,Unit.PX);				
//				divFilters.add(common.filters);
//			}
//		});
		
		
		this.scrollPanelTicketsList.addScrollHandler(new ScrollHandler() {
			
			@Override
			public void onScroll(ScrollEvent event) 
			{
				if(scrollPanelTicketsList.getVerticalScrollPosition()==0)
				{
					common.filters.setVisible(true);
					rowTitle.setVisible(true);
			
//					if(scrollPanelMap.isVisible())
//					{
//						common.setHeight( scrollPanelTicketsList, scrollPanelTicketsListMinHeightPercent);
//					}
//					else
//					{
//						common.setHeight( scrollPanelTicketsList, scrollPanelTicketsListMaxHeightPercent);
//					}					
				}
				else
				{
					common.filters.setVisible(false);
					rowTitle.setVisible(false);

//					if(scrollPanelMap.isVisible())
//					{
//						common.setHeight( scrollPanelTicketsList, scrollPanelTicketsListMinHeightPercent+5);
//					}
//					else
//					{
//						common.setHeight( scrollPanelTicketsList, scrollPanelTicketsListMaxHeightPercent+5);
//					}	
				}
			}
		});
		
		this.scrollPanelTicketsList.getElement().getStyle().setPadding(0, Unit.PX);
		this.scrollPanelTicketsList.getElement().getStyle().setMargin(0, Unit.PX);
		
		
	}
	
	private void setOnCellPreview()
	{
			//http://www.gwtproject.org/javadoc/latest/constant-values.html#com.google.gwt.user.client.Event.MOUSEEVENTS
			cellTable.addCellPreviewHandler(new CellPreviewEvent.Handler<TicketsBlock>() {
			@Override
			public void onCellPreview(CellPreviewEvent<TicketsBlock> event) {

				TicketsBlock ticketsBlock = event.getValue();
				
				if(ticketsBlock.isStar())
				{
					int start = cellTable.getVisibleRange().getStart();
					
					int index = event.getIndex() - start;

					GWT.log("index: "+index);

					TableRowElement tableRowElement = cellTable.getRowElement(index);

					if ("mouseover".equals(event.getNativeEvent().getType())) 
					{
						GWT.log("mouseover");
						
						tableRowElement.removeClassName("btn-info");

					} 
					else if ("mouseout".equals(event.getNativeEvent().getType())) 
					{
						tableRowElement.setClassName("btn-info");								
					}
				}

				
				if(common.isUpdateMap()==true)
				{					
					if ("mouseover".equals(event.getNativeEvent().getType())) 
					{					
						common.highlightSection(ticketsBlock.getSection());
					}
					else if ("mouseout".equals(event.getNativeEvent().getType())) 
					{					
						common.resetSection(ticketsBlock.getSection());
					}
				}
			}
		});	
	}
	
//	private String getElementValue(Element element)
//	{
//		Element child = element.getFirstChildElement().cast();
//		
//		while (child != null)
//		{
//			element = child;
//		    child = element.getFirstChildElement().cast();
//		}
//		    
//		return element.getFirstChild().getNodeValue();
//	}

//	private void setTicketsListScroll(final int percent)
//	{
//		//init scroll in first time
//		Scheduler.get().scheduleDeferred(new Command() {
//			@Override
//			public void execute() 
//			{	
//				//45%
//				int scrollerHeight = common.calculateHeight(percent) ;
//				
//				if (scrollerHeight < 1) 
//				{
//					scrollerHeight = 1;
//				}
//				scrollPanelTicketsList.setHeight("" + scrollerHeight + "px"); 	
//			}
//		});
//		
//		//update scroll if user resize height 
//		Scheduler.get().scheduleDeferred(new Command() {
//			@Override
//			public void execute() {
//				Window.addResizeHandler(new ResizeHandler() {
//					
//					@Override
//					public void onResize(final ResizeEvent event) {
//						Scheduler.get().scheduleDeferred(new Command() {
//							@Override
//							public void execute() {
//								
//								//45%
//								int scrollerHeight = common.calculateHeight(percent) ;
//								
//								if (scrollerHeight < 1) 
//								{
//									scrollerHeight = 1;
//								}
//								
//								scrollPanelTicketsList.setHeight("" + scrollerHeight + "px"); 				
//							}
//						});						
//					}
//				});
//			}
//		});
//	}
	
	
	
	
	
	
	
	
	
	

	
	
	//common --------------------------------------------------------------------------------------------------------

	
	@Override
	public void setData(final List<TicketsBlock> data,boolean showParkingTickets) {
	
		if(data.size()==0)
		{
			this.cellTable.setVisible(false);
			this.pagination.setVisible(false);
			this.paragraphPagination.setVisible(false);
			return;
		}
		else
		{
			if(data.size() <= pageSize)
			{
				this.pagination.setVisible(false);
				this.paragraphPagination.setVisible(false);
			}
			else
			{
				this.pagination.setVisible(true);
				this.paragraphPagination.setVisible(true);
			}
			
			Scheduler.get().scheduleDeferred(new Command() {
				@Override
				public void execute() 
				{	
					cellTable.setVisible(true);
					common.filters.setVisible(true);
				}
			});
		}
		
		this.common.listDataProvider.getList().clear();
		
		this.common.listDataProvider.getList().addAll( data );
		
		this.common.listDataProvider.refresh();

		this.cellTable.setRowCount(this.common.listDataProvider.getList().size(), true);		

		this.cellTable.setVisibleRangeAndClearData(new Range(0,pageSize),true);	
		
		this.scrollPanelTicketsList.scrollToTop();
		
		if(showParkingTickets==true)
		{
			common.setHeight( scrollPanelTicketsList, scrollPanelTicketsListMaxHeightPercent);

		}
//		else if(this.scrollPanelMap.isVisible())
//		{
//			common.setHeight( scrollPanelTicketsList, scrollPanelTicketsListMinHeightPercent);
//
//		}
//		else
//		{
//			common.setHeight( scrollPanelTicketsList, scrollPanelTicketsListMaxHeightPercent);
//		}
	}

	@UiHandler({"buttonBackToSearch"})
	void onAnchorBackToSearchClick(ClickEvent e) 
	{			
		if(this.common.presenter.getComparePlace().getSearchPlace()!=null)
		{
			this.common.presenter.goTo(this.common.presenter.getComparePlace().getSearchPlace());
		}
		else
		{
			this.common.presenter.goTo(new SearchPlace(
					IndexKey.event,
					this.common.presenter.getPlace().getEventName(),
					null,null,
					QueryLocationIndex.city,
					this.common.presenter.getFactory().getSearchLocation()));
		}
	}
	
	@Override
	public void init( final List<TicketsBlock> ticketBlocks,final AboutTickets aboutTickets,boolean showParkingTickets )
	{
		this.iconWaitingForTickets.setVisible(false);

		if( ticketBlocks.size() > 0 )
		{
			this.setData( ticketBlocks ,showParkingTickets);
		}
		else
		{		
			this.buttonBackToSearch.setVisible(true);
			
			this.iconWaitingForMap.setVisible(false);
			
			this.common.filters.setVisible(false);
		
			return;
		}
	}

	@Override
	public void reset()
	{						
		this.common.noSeatingCharts = false;
		this.common.mapType = null;

		this.anchorHideShowMap.setVisible(true);
		
		this.divMap.getElement().removeAllChildren();	
		this.common.setHeight(this.divMap, 40);
		this.divMap.setVisible(true);
		
		this.common.listDataProvider.getList().clear();	
		this.anchorHideShowMap.setText("Hide map");
		this.paragraphPagination.setText("");
		this.paragraphPagination.setVisible(false);
		this.cellTable.setVisible(false);
		this.pagination.setVisible(false);
		this.buttonBackToSearch.setVisible(false);
		this.buttonEventInfo.setText("");
		this.buttonEventName.setText("");
		this.buttonEventVenue.setText("");
		this.anchorListItemEventLocation.setText("");
		this.anchorListItemEventDate.setText("");
		this.iconWaitingForTickets.setVisible(false);
		this.iconWaitingForMap.setVisible(false);

	}
	
	@UiHandler("buttonEventName")
	public void onEventNameClick(ClickEvent event) {
		
		if(this.common.presenter.getPlace().getSearchPlace()!=null)
		{
			common.presenter.goTo(
					new SearchPlace(
							IndexKey.event,
							this.common.presenter.getPlace().getEventName(),
							this.common.presenter.getPlace().getSearchPlace().getQueryLocationIndex(),
							this.common.presenter.getPlace().getSearchPlace().getLocation()));
		}
		else
		{
			common.presenter.goTo(
					new SearchPlace(
							IndexKey.event,
							this.common.presenter.getPlace().getEventName(),
							QueryLocationIndex.ignore,
							null));			
		}
	}
	
	@UiHandler("buttonGoHome")
	public void onButtonGoHomeClick(ClickEvent event) {
		
		common.presenter.goTo(
					new SearchPlace(
							IndexKey.event,
							this.common.presenter.getPlace().getEventName(),
							QueryLocationIndex.ignore,null));			
	}
	
	@UiHandler("buttonEventVenue")
	public void onEventVenueClick(ClickEvent event) {
	
		if(this.common.presenter.getPlace().getSearchPlace()!=null)
		{
			common.presenter.goTo(
					new SearchPlace(
							IndexKey.venue,
							this.common.presenter.getPlace().getVenue(),
							this.common.presenter.getPlace().getSearchPlace().getQueryLocationIndex(),
							this.common.presenter.getPlace().getSearchPlace().getLocation()));
		}
		else
		{
			common.presenter.goTo(
					new SearchPlace(
							IndexKey.venue,
							this.common.presenter.getPlace().getVenue(),
							QueryLocationIndex.ignore,
							null));
		}
	}

	private void buildGrid(DeviceType deviceType) 
	{		
		this.common.columnButtonStart = this.common.buildColumnButtonStart(cellTable);
		this.cellTable.addColumn(this.common.columnButtonStart);

		this.common.columnSection = this.common.buildColumnSection();
		this.cellTable.addColumn(this.common.columnSection, this.common.buildHeader(common.COLUMN_SECTION), this.common.buildHeader(common.COLUMN_SECTION));
	
		this.common.columnRow = this.common.buildColumnRow();
		this.cellTable.addColumn(this.common.columnRow, this.common.buildHeader(common.COLUMN_ROW), this.common.buildHeader(common.COLUMN_ROW));

		this.common.columnPrice = this.common.buildColumnPrice();
		this.cellTable.addColumn(this.common.columnPrice, this.common.buildHeader(common.COLUMN_PRICE), this.common.buildHeader(common.COLUMN_PRICE));

		this.common.columnQuantity = this.common.buildColumnQuantity();
		this.cellTable.addColumn(this.common.columnQuantity, this.common.buildHeader(common.COLUMN_QUANTITY), this.common.buildHeader(common.COLUMN_QUANTITY));

		this.common.columnButtonCheckout = this.common.buildColumnButtonCheckout();
		this.cellTable.addColumn(this.common.columnButtonCheckout);

		this.common.columnButtonAbout 	= this.common.buildColumnButtonAbout();
		this.cellTable.addColumn(this.common.columnButtonAbout);

		this.cellTable.addRangeChangeHandler(new RangeChangeEvent.Handler() {
            @Override
            public void onRangeChange(final RangeChangeEvent event) {
               pagination.rebuild(common.simplePager);
               
               String text = common.adjustPagination(
            		   common.listDataProvider.getList().size(), event.getNewRange().getStart(), pageSize);
               
               paragraphPagination.setText(text);  
               
               Scheduler.get().scheduleDeferred(new Command() {
          			@Override
          			public void execute() 
          			{	
                       scrollPanelTicketsList.scrollToTop();

          			}
          		});	}
        });
	 
		pagination.clear();	
		
		
		cellTable.setRowStyles(new RowStyles<TicketsBlock>() {

			@Override
			public String getStyleNames(TicketsBlock ticketsBlock, int rowIndex) {
				
				if(ticketsBlock.isStar())
				{
					return "btn-info";
				}
				else
				{
					return "";
				}			
			}
		
		});
	}

	
	private void addColumnSortListHandling() {
		
		ListHandler<TicketsBlock> columnSortHandler = new ListHandler<TicketsBlock>(this.common.listDataProvider.getList());

		this.common.columnRow.setSortable(true);
		this.common.columnPrice.setSortable(true);
		this.common.columnQuantity.setSortable(true);
		this.common.columnButtonStart.setSortable(true);
		this.common.columnSection.setSortable(true);
		
		columnSortHandler.setComparator(common.columnSection, new AlphanumComparator(SortBy.section));
		
		columnSortHandler.setComparator(common.columnRow, new AlphanumComparator(SortBy.row));
		
		columnSortHandler.setComparator(common.columnQuantity, new Comparator<TicketsBlock>() {
			
			@Override
			public int compare(TicketsBlock o1, TicketsBlock o2) 
			{
				Integer number1 = Utils.getMaxNumber(o1.getSaleSize());
				Integer number2 = Utils.getMaxNumber(o2.getSaleSize());
				
				return  number1.compareTo( number2 );
			}
		});
		
		columnSortHandler.setComparator(common.columnPrice, new Comparator<TicketsBlock>() {
			@Override
			public int compare(TicketsBlock o1, TicketsBlock o2) {
				return o1.getPrice().compareTo(o2.getPrice());
			}
		});
		
//		if(this.columnBrokerImage!=null)
//		{
//			this.columnBrokerImage.setSortable(true);
//	
//			columnSortHandler.setComparator(columnBrokerImage, new Comparator<TicketsBlock>() {
//				@Override
//				public int compare(TicketsBlock o1, TicketsBlock o2) {
//					return o1.getTickets().getSource().name().compareTo(o2.getTickets().getSource().name());
//				}
//			});
//		}
		
		columnSortHandler.setComparator(common.columnButtonStart, new Comparator<TicketsBlock>() {
			@Override
			public int compare(TicketsBlock o1, TicketsBlock o2) {
				return Boolean.valueOf(o1.isStar()).compareTo(Boolean.valueOf(o2.isStar()));
			}
		});
		
		this.cellTable.getColumnSortList().push(this.common.columnPrice);
		
		this.cellTable.addColumnSortHandler(columnSortHandler);
	}

	@Override
	public void setComparePresenter(ComparePresenter presenter) {
		this.common.presenter = presenter;
	}
	
		
	
	@Override
	public void preInit() 
	{
	
		//waiting indicators
		iconWaitingForMap.setVisible(true);
		iconWaitingForTickets.setVisible(true);
		
//		//date
//		String date = this.common.presenter.getPlace().getDate();
//		this.anchorListItemEventDate.setText(date);
//
//		//venue
//		String venue = Utils.capitalize(this.common.presenter.getPlace().getVenue(), " ");
//		this.buttonEventVenue.setText(venue);
//
//		//location
//		String[] segmentKey = this.common.presenter.getPlace().getSegmentKey().split("_");
//		String segmentKey_ = null;
//
//		if(segmentKey.length==1)
//		{
//			segmentKey_ = ","+segmentKey[0]; 
//		}
//		else if(segmentKey.length==2)
//		{
//			segmentKey_ = ","+segmentKey[1]+","+segmentKey[0];
//		}
//		
//		String location = 
//				Utils.getCitiesCloseAsString(
//						this.common.presenter.getPlace().getCitiesClose(),false) + segmentKey_.toUpperCase();
//		this.anchorListItemEventLocation.setText(location);
//
//		//event
//		String event = Utils.capitalize(this.common.presenter.getPlace().getEventName(), " ");		
//		this.buttonEventName.setText(event);
//		
//		
//		if( event.length() > 42 )
//		{
//			event = event.substring(0, 39);
//			event = event+"...";
//		}
//	
//		this.common.updateButtonBug(ButtonSize.SMALL,this.buttonEventInfo, event, IconType.INFO_CIRCLE, ButtonType.PRIMARY,true);
//
//		//title for page
//		String title = event+", "+venue+", "+location+", "+date;
//		Document.get().setTitle(title);		
//		
//		//title for addthis
//		this.common.presenter.updateAddThis(title);
	}
	
	@Override
	public void setHeader(String date, String venue, String segmentKey__, String city, String event) {
		
		//date
		this.anchorListItemEventDate.setText(date);

		//venue
		this.buttonEventVenue.setText(venue);

		//location
		String[] segmentKey = segmentKey__.split("_");
		String segmentKey_ = null;

		if(segmentKey.length==1)
		{
			segmentKey_ = ","+segmentKey[0]; 
		}
		else if(segmentKey.length==2)
		{
			segmentKey_ = ","+segmentKey[1]+","+segmentKey[0];
		}
		
		this.anchorListItemEventLocation.setText(city+segmentKey_.toUpperCase());

		//event
		this.buttonEventName.setText(event);
				
		if( event.length() > 36 )
		{
			event = event.substring(0, 32);
			event = event+"...";
		}
	
		this.common.updateButtonBug(ButtonSize.DEFAULT,this.buttonEventInfo, event, IconType.INFO_CIRCLE, ButtonType.PRIMARY,true);

		//title for page
		String title = event+", "+venue+", "+city+segmentKey_+", "+date;		
		
		Utils.setTitle("Compare Tickets Now | "+event);
		Utils.setDescription(venue+", "+city+segmentKey_+", "+date);
		
		
		//title for addthis
		this.common.presenter.updateAddThis(title);
		
	}
	
	
	
	
	//private Modal modalAboutResults = new Modal();
	//private String aboutTickets = "";
	
	@Override
	public void setAboutTickets(AboutTickets aboutTickets)
	{	
//	    final String result;
//		
//		if(aboutTickets.getAboutAllTickets().getTotalTicketsNumber()==0)
//		{
//			result = NO_TICKETS_AVAILABLE;
//		}
//		else
//		{
//			String minPrice = Utils.usdNumberFormat.format(aboutTickets.getAboutAllTickets().getMinPrice());
//			String maxPrice = Utils.usdNumberFormat.format(aboutTickets.getAboutAllTickets().getMaxPrice());
//			String avgPrice = Utils.usdNumberFormat.format(aboutTickets.getAboutAllTickets().getAvgPrice());
//			String totalTicketsNumber = String.valueOf(aboutTickets.getAboutAllTickets().getTotalTicketsNumber());
//			//String totalTicketsPrice = Utils.usdNumberFormat.format(aboutTickets.getAboutAllTickets().getTotalTicketsPrice());
//
////			result  = "<strong>Found "+totalTicketsNumber+" tickets</strong>"
////					+ "("+(aboutTickets.getTakeTime()/1000)+" seconds)</br>"
////					+ "Prices from "+minPrice+" to "+maxPrice+"</br>"+
////					" Average price: "+avgPrice;
////					//", Amount: "+totalTicketsPrice;
//			
//			result  = "<strong>Found "+totalTicketsNumber+" tickets</strong>"
//					+ ".Prices from "+minPrice+" to "+maxPrice+
//					  ".Average price: "+avgPrice
//					+ "</br>("+(aboutTickets.getTakeTime()/1000)+" seconds)";
//
//		}
//
//		this.aboutTickets = result;
		
		//this.notifyAboutTickets();
		
	}

	@Override
	public void disableMap() 
	{
		this.divMap.setVisible(false);
	}
	
	@Override
	public void enableMap() 
	{
		this.divMap.setVisible(true);
	}
	
	@Override
	public void adjust() {
		this.divMap.setVisible(true);		
	}

	@Override
	public void setFilters(final Factory factory) {
		Scheduler.get().scheduleDeferred(new Command() {
			@Override
			public void execute() 
			{	
				common.filters = factory.getCompareFilterView(common.deviceType).asWidget();
				common.filters.getElement().getStyle().setMarginTop(5,Unit.PX);
				common.filters.getElement().getStyle().setMarginBottom(10,Unit.PX);				
				common.filters.getElement().getStyle().setPadding(0,Unit.PX);
				common.filters.getElement().getStyle().setMarginLeft(0,Unit.PX);
				common.filters.getElement().getStyle().setMarginRight(0,Unit.PX);
				
				divFilters.getElement().getStyle().setPadding(0,Unit.PX);

				divFilters.add(common.filters);
				
			
//				Widget menu = factory.getMenuView().asWidget();		
//				divMenu.add(menu);
			}
		});		
	}
	
	
	
	
	

	@Override
	public void adjustStarred() {
		ColumnSortInfo columnSortInfo = new ColumnSortInfo( this.common.columnButtonStart, false );
		cellTable.getColumnSortList().push( columnSortInfo );
		ColumnSortEvent.fire( cellTable, cellTable.getColumnSortList());
		this.scrollPanelTicketsList.setVerticalScrollPosition(0);
		this.cellTable.setVisibleRangeAndClearData(new Range(0,pageSize),true);		
	}

	@Override
	public void noTicketsAvailable() {
		this.divMap.setVisible(false);
		this.anchorHideShowMap.setVisible(false);
		
		
	}

	@Override
	public void notifications() {
		this.common.notifications();		
	}
}
