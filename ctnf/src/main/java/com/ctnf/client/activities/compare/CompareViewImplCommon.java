package com.ctnf.client.activities.compare;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Image;
import org.gwtbootstrap3.client.ui.Modal;
import org.gwtbootstrap3.client.ui.ModalBody;
import org.gwtbootstrap3.client.ui.ModalFooter;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconPosition;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.Toggle;
import org.gwtbootstrap3.client.ui.gwt.ButtonCell;
import org.gwtbootstrap3.client.ui.gwt.CellTable;
import org.gwtbootstrap3.client.ui.html.Paragraph;
import org.gwtbootstrap3.extras.notify.client.constants.NotifyPlacement;
import org.gwtbootstrap3.extras.notify.client.constants.NotifyPosition;
import org.gwtbootstrap3.extras.notify.client.constants.NotifyType;
import org.gwtbootstrap3.extras.notify.client.event.NotifyClosedHandler;
import org.gwtbootstrap3.extras.notify.client.ui.Notify;
import org.gwtbootstrap3.extras.notify.client.ui.NotifySettings;

import com.ctnc.shared.Source;
import com.ctnc.shared.TicketsBlock;
import com.ctnf.client.DeviceType;
import com.ctnf.client.Resources;
import com.ctnf.client.activities.checkout.Checkout;
import com.ctnf.client.activities.checkout.CheckoutPlace;
import com.ctnf.client.utils.Utils;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

//common between devices large and small devices 
public class CompareViewImplCommon {

	public DeviceType deviceType = Utils.getDeviceType( Window.getClientWidth() );

	public ComparePresenter presenter;
	
	public NumberFormat format = NumberFormat.getSimpleCurrencyFormat("USD");

	public String[] selectedSectionsFromMap = null;

	public Widget filters = null;

	public SimplePager simplePager = new SimplePager();

	public ListDataProvider<TicketsBlock> listDataProvider;

	public final String COLUMN_BROKER_IMAGE = "Market";
	public Column<TicketsBlock,ImageResource> columnBrokerImage;

	public final String COLUMN_SECTION = "Section";
	public TextColumn<TicketsBlock> columnSection;
		
	public final String COLUMN_ROW = "Row";
	public TextColumn<TicketsBlock> columnRow;
	
	public final String COLUMN_PRICE = "Price";
	public Column<TicketsBlock,Number> columnPrice;
		
	public Column<TicketsBlock, String> columnButtonCheckout;
	
	public Column<TicketsBlock, String> columnButtonStart;
	
	public Column<TicketsBlock, String> columnButtonAbout;

	public final String COLUMN_QUANTITY = "Qty";
	public TextColumn<TicketsBlock> columnQuantity ;

	public boolean noSeatingCharts = false;
	public String mapType = null;
	
	public void setHeight(final Widget widget , final int percent)
	{
		//init scroll in first time
		Scheduler.get().scheduleDeferred(new Command() {
			@Override
			public void execute() 
			{	
				int height = calculateHeight(percent) ;
				
				if (height < 1) 
				{
					height = 1;
				}
				
				widget.setHeight("" + height + "px"); 	
			}
		});
		
		//update scroll if user resize height 
		Scheduler.get().scheduleDeferred(new Command() {
			@Override
			public void execute() {
				Window.addResizeHandler(new ResizeHandler() {
					
					@Override
					public void onResize(final ResizeEvent event) {
						Scheduler.get().scheduleDeferred(new Command() {
							@Override
							public void execute() {
								
								int height = calculateHeight(percent) ;
								
								if (height < 1) 
								{
									height = 1;
								}
								
								widget.setHeight("" + height + "px"); 				
							}
						});						
					}
				});
			}
		});
	}	

	public int calculateHeight(int percent)
	{
		int clientHeight = Window.getClientHeight();
		
		return (percent * clientHeight)/100;		
	}
	
	public void setMapType(String mapType)
	{
		this.mapType = mapType;
	}
	
	public boolean isUpdateMap()
	{
		if(noSeatingCharts==true)
		{
			return false;
		}
		if(mapType==null||mapType.toLowerCase().equals("static"))
		{
			return false;
		}
		
		return true;
	}
	
	
	
	
	
	
	public Header<String> buildHeader(final String text)
	{
		Header<String> head = new Header<String>(new TextCell()){
			@Override
			public String getValue() {
				return text;
			}
		};
		return head;
	}

	
	
	//headers and about ------------------------------------------------------------------------
	
	
	public void updateButtonBug(final ButtonSize buttonSize, final Button button,final String text,final IconType iconType,final ButtonType buttonType,final boolean dropdown)
	{
		Scheduler.get().scheduleDeferred(new Command() {
			@Override
			public void execute() {
				
				button.setText( text );
				button.setTitle(text);
				button.setIconPosition(IconPosition.LEFT);
				button.setIcon(iconType);
				if(dropdown==true)button.setDataToggle(Toggle.DROPDOWN);
				button.setType(buttonType);
				button.setSize(buttonSize);
			}
		});
	}

	
	
	public String adjustPagination(int totalResultsNumber,int rangeStart,int pageSize)
	{
		if( totalResultsNumber > pageSize )
		{
			rangeStart++;
					
			int end = 0;
					
			if( ( rangeStart + pageSize - 1 ) > totalResultsNumber )
			{
				end = totalResultsNumber;
			}
			else
			{
				end = ( rangeStart+pageSize - 1 );
			}
				
			return rangeStart+"-"+end+" of "+totalResultsNumber;
		}
		
		return "";
	}

	private native void updateMapNative(JavaScriptObject[] ticketsArray) /*-{
		//console.log("@@@@"+JSON.stringify(ticketsArray));
		try {
			$wnd.jQuery("#divMap").tuMap("SetOptions", {
				Tickets : ticketsArray
			});

			$wnd.jQuery("#divMap").tuMap("Refresh", "ProcessTickets");
		} catch (ex) {
			//do nothing, is a static map, doesnt need filtering
		}
	}-*/;
	
	public void updateMap(List<TicketsBlock> data)
	{
		updateMapNative(
				presenter.ticketsBlockToJavaScriptObjectArray(data));	
	}
	public void updateMap(JavaScriptObject[] data)
	{
		updateMapNative(data);	
	}
	
	
	public String chooseEntryForMap(String[] source_entry)
	{
		GWT.log("chooseEntryForMap: "+source_entry);
		
		if(source_entry.length==1)
		{
			return source_entry[0].split("_")[1];
		}
		else
		{
			//priority 1
			for(String entry : source_entry)
			{
				String[] tmp = entry.split("_");
				
				if(tmp[0].equals(Source.ticketcity.name()))
				{
					return tmp[1];
				}
			}
			
			//priority 2
			for(String entry : source_entry)
			{
				String[] tmp = entry.split("_");
				
				if(tmp[0].equals(Source.ticketnetwork.name()))
				{
					return tmp[1];
				}
			}
		}
		
		return null;
	}
	// sectionToHighlight
	public native void highlightSection(String sectionToHighlight) /*-{
		$wnd.jQuery("#divMap").tuMap('HighlightSection', sectionToHighlight);
	}-*/;

	public native void resetSection(String sectionToHighlight) /*-{
		$wnd.jQuery("#divMap").tuMap('ResetSection', sectionToHighlight);
	}-*/;

	public native void zoom(String zoomLevel) /*-{
		$wnd.jQuery("#divMap").tuMap("SetOptions", {
			ZoomLevel : zoomLevel
		});
	}-*/;

	public native void zoomIn() /*-{
		$wnd.jQuery("#divMap").tuMap("ZoomIn");
	}-*/;

	public native void zoomOut() /*-{
		$wnd.jQuery("#divMap").tuMap("ZoomOut");
	}-*/;

	private native void resetSelectedSections() /*-{
		$wnd.jQuery("#divMap").tuMap("Refresh", "Reset");
	}-*/;

	private native String[] getSelectedSections()/*-{
		try {
			var selectedSections = $wnd.jQuery("#divMap").tuMap(
					"GetSelectedSections");
		} catch (e) {
		}
		return selectedSections;
	}-*/;
		
	// blinking
	public native void sectionBlinking(String section, boolean blinking) /*-{
		$wnd.jQuery("#divMap").tuMap("FilterTickets", section, blinking);
	}-*/;

	// blinking
	public native void toggleAll() /*-{
		$wnd.jQuery("#divMap").tuMap("ToggleAll");		
	}-*/;

	public void sectionsBlinking(List<String> sections, boolean blinking) {
						
		if (sections == null) {
			return;
		}
		for (String section : sections) {
			this.sectionBlinking(section, blinking);
		}
	}


	public void setSelectedSectionsFromMap_(String[] selectedSectionsFromMap)
	{
		List<String> result = new ArrayList<String>();
		
		if(selectedSectionsFromMap!=null&&selectedSectionsFromMap.length>0)
		{
			for(String selectedSectionFromMap : selectedSectionsFromMap)
			{
				
				//GWT.log(" @@@ "+selectedSectionFromMap);
				result.add(selectedSectionFromMap);
			}
		}
		
		this.presenter.onChangeSectionsFromMap(result);
	}
	

	public Column<TicketsBlock, ImageResource> buildColumnBrokerImage() {
	
		this.columnBrokerImage = new Column<TicketsBlock,ImageResource>(new ImageResourceCell())
		{
			@Override
			public ImageResource getValue(TicketsBlock ticketsBlock) {
				switch (ticketsBlock.getTickets().getSource()) 
				{
					case ticketcity:
						return Resources.instance.ticketcity();
						
					case ticketnetwork:
						return Resources.instance.tndirect();
				}
				return null;			
			}
		};	
	
		columnBrokerImage.setDataStoreName(COLUMN_BROKER_IMAGE);
		columnBrokerImage.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		columnBrokerImage.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
		return columnBrokerImage;
	}

	public Column<TicketsBlock, Number> buildColumnPrice() {
			    		
		columnPrice = new Column<TicketsBlock, Number>(new NumberCell(format)){
	    	@Override
	    	public BigDecimal getValue(TicketsBlock object) {
	    		return object.getPrice();
	    	}
	    };
	    		
	    columnPrice.setDataStoreName(COLUMN_PRICE);
	    columnPrice.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
	    columnPrice.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);		
		return columnPrice;
	}

	public TextColumn<TicketsBlock> buildColumnQuantity() {
		
		columnQuantity = new TextColumn<TicketsBlock>(){
			
	    	@Override
	    	public String getValue(TicketsBlock object) {
	    		
	    		int quantity = Utils.getMaxNumber( object.getSaleSize() );
	    		
	    		return String.valueOf( quantity );
	    	}
	    };
	    
	    columnQuantity.setDataStoreName(COLUMN_QUANTITY);
	    columnQuantity.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
	    columnQuantity.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);		
		return columnQuantity;
	}

	
	public TextColumn<TicketsBlock> buildColumnSection(){
		this.columnSection = new TextColumn<TicketsBlock>(){
			@Override
			public String getValue(TicketsBlock object) {
						return object.getSection();
			}};
			
			this.columnSection.setDataStoreName(COLUMN_SECTION);
			this.columnSection.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_DEFAULT);
			this.columnSection.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
			return columnSection;	
	}
	
	public TextColumn<TicketsBlock> buildColumnRow(){
		this.columnRow = new TextColumn<TicketsBlock>(){
			@Override
			public String getValue(TicketsBlock object) {
						return object.getRow();
			}};
			
			this.columnRow.setDataStoreName(COLUMN_ROW);
			this.columnRow.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
			this.columnRow.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
			return columnRow;	
	}

	public Column<TicketsBlock, String> buildColumnButtonAbout() {
		
		org.gwtbootstrap3.client.ui.gwt.ButtonCell buttonCell = 
				new org.gwtbootstrap3.client.ui.gwt.ButtonCell(ButtonType.LINK,IconType.INFO_CIRCLE);

		columnButtonAbout = new Column<TicketsBlock, String>(buttonCell){
		
		@Override
		public String getValue(TicketsBlock object){
			return "";
		}};
			
		columnButtonAbout.setFieldUpdater(new FieldUpdater<TicketsBlock, String>(){
				@Override
				public void update(int index, final TicketsBlock ticketsBlock, String value){
						
					final Modal modal = new Modal();
					modal.setColor("#337ab7");

					modal.setTitle("Section: "+ticketsBlock.getSection()+", Row: "+ticketsBlock.getRow()+", Price: "+format.format(ticketsBlock.getPrice()));

					ModalBody body = new ModalBody();
					modal.add(body);
					
					Paragraph paragraph = new Paragraph();
					body.add(paragraph);

					if(ticketsBlock.getComments()!=null&&ticketsBlock.getComments().length()>0)
					{
						paragraph.setHTML("Notes: "+ticketsBlock.getComments()+"<br><br>");
					}
					
					if(ticketsBlock.getServiceCharge()!=null)
					{				
						paragraph.setHTML("Expected service charge: "+format.format(ticketsBlock.getServiceCharge())+"<br><br>");
					}
					
					ImageResource imageResource = null;
					switch (ticketsBlock.getTickets().getSource()) 
					{
						case ticketcity:
							imageResource =  Resources.instance.ticketcity();
							break;
						case ticketnetwork:
							imageResource =  Resources.instance.tndirect();
							break;
					}
					
					Image image = new Image(imageResource);
		
					paragraph.add(image);
					
					modal.setDataKeyboard(true);
					
					
					ModalFooter modalFooter = new ModalFooter();
					modal.add(modalFooter);
					
					Button checkout = new Button();
					checkout.setText("Checkout");
					checkout.setIcon(IconType.SHOPPING_CART);
					checkout.setType(ButtonType.SUCCESS);
				
					checkout.addClickHandler(new ClickHandler() {
						
						@Override
						public void onClick(ClickEvent event) {
							
							checkout(ticketsBlock);
							
							modal.hide();
						}
					});
					
					modalFooter.add(checkout);
					
					Button close = new Button();
					close.setColor("#337ab7");
					close.getElement().getStyle().setBorderColor("#337ab7");
					
					close.setText("Close");
					close.setIcon(IconType.CLOSE);
					close.setType(ButtonType.DEFAULT);
				
					close.addClickHandler(new ClickHandler() {
						
						@Override
						public void onClick(ClickEvent event) {
							
							modal.hide();
						}
					});
					
					modalFooter.add(close);

					
					
					
					
					
					
					modal.show();
				}
		});
		
		columnButtonAbout.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		columnButtonAbout.setVerticalAlignment(HasVerticalAlignment.ALIGN_BOTTOM);		
		return columnButtonAbout;
	}


	
	public Column<TicketsBlock, String> buildColumnButtonCheckout() {
		
		ButtonCell buttonCell = new ButtonCell(IconType.SHOPPING_CART,ButtonType.SUCCESS,ButtonSize.DEFAULT);

		columnButtonCheckout = new Column<TicketsBlock, String>(buttonCell){
		
		@Override
		public String getValue(TicketsBlock object){
			
			if(deviceType.equals(DeviceType.Phone))
			{
				return "";
			}
			else
			{
				return "Checkout";
			}
		}};
		
		columnButtonCheckout.setFieldUpdater(new FieldUpdater<TicketsBlock, String>(){
				@Override
				public void update(int index, TicketsBlock ticketsBlock, String value)
				{
					checkout(ticketsBlock);
				}
		});
		columnButtonCheckout.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		columnButtonCheckout.setVerticalAlignment(HasVerticalAlignment.ALIGN_BOTTOM);		
		return columnButtonCheckout;
	}
	
	private void checkout(TicketsBlock ticketsBlock)
	{
		final Checkout checkout = new Checkout(
				new CheckoutPlace(
				ticketsBlock.getTickets().getSource(),
				ticketsBlock.getTickets().getEventId(),
				ticketsBlock.getBlockId(),
				Collections.min(ticketsBlock.getSaleSize())));
		
		redirectingToBrokerSiteNotification();
		
		presenter.getFactory().getCheckoutService().checkout(checkout.getUrl(), new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {

				GWT.log("Can't report checkout", caught);
				
				checkout.checkout();

				return;
			}

			@Override
			public void onSuccess(Void result) {

				GWT.log("Checkout reported");

				checkout.checkout();

				return;
			}
		});

	}
	
	private void redirectingToBrokerSiteNotification() 
	{
		NotifySettings notifySettings = NotifySettings.newSettings();
		notifySettings.setPauseOnMouseOver(true);
		notifySettings.setNewestOnTop(true);
		notifySettings.setType(NotifyType.SUCCESS);
		notifySettings.setDelay(2000);
		notifySettings.setPlacement(NotifyPlacement.TOP_CENTER);
		Notify.notify("","Redirecting to  broker site",IconType.INFO_CIRCLE,notifySettings);
		
	}
	
	
	
	
	public Column<TicketsBlock, String> buildColumnButtonStart(final CellTable<TicketsBlock> cellTable) {
		
		ButtonCell buttonCell = new ButtonCell(IconType.STAR,ButtonType.LINK,ButtonSize.EXTRA_SMALL);			
		
		columnButtonStart = new Column<TicketsBlock, String>(buttonCell){
		
		@Override
		public String getValue(TicketsBlock object){
			return "";
		}};
		
		columnButtonStart.setFieldUpdater(new FieldUpdater<TicketsBlock, String>(){
				@Override
				public void update(int index, TicketsBlock ticketsBlock, String value){
					
					int start = cellTable.getVisibleRange().getStart();
					//int lenght = cellTable.getVisibleRange().getLength();
					
					GWT.log("---");
					GWT.log("start: "+ start);
					//GWT.log("lenght: "+ lenght);
					GWT.log("index: "+ index);
					GWT.log("total: "+ listDataProvider.getList().size());
					
					index = index - start;
					
					GWT.log("go index: "+ index);
					
					TableRowElement tableRowElement = cellTable.getRowElement(index);
					
					if(ticketsBlock.isStar())
					{
						tableRowElement.removeClassName("btn-info");
						ticketsBlock.setStar(false);
					}
					else
					{
						tableRowElement.setClassName("btn-info");
						ticketsBlock.setStar(true);
					}					
				}
		});
		
		columnButtonStart.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		columnButtonStart.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);		
		return columnButtonStart;
	}


	public void notifications() 
	{
//		NotifySettings notifySettings = NotifySettings.newSettings();
//		notifySettings.setPauseOnMouseOver(true);
//		notifySettings.setClosedHandler(new NotifyClosedHandler() {
//			
//			@Override
//			public void onClosed() {
//				
//				GWT.log("@@@@@@@close");
//			}
//		});
//		
//		Notify notify = Notify.notify("","Ticket prices do not include service and delivery fees",IconType.INFO_CIRCLE,notifySettings);
		
	}
	
}
