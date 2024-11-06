
package com.ctnf.client.activities.compare;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.gwtbootstrap3.extras.notify.client.ui.Notify;

import com.ctnc.shared.CompareRequest;
import com.ctnc.shared.CompareResponse;
import com.ctnc.shared.CompareResponseStatus;
import com.ctnc.shared.IndexKey;
import com.ctnc.shared.QueryLocationIndex;
import com.ctnc.shared.Source;
import com.ctnc.shared.Tickets;
import com.ctnc.shared.TicketsBlock;
import com.ctnf.client.DeviceType;
import com.ctnf.client.DeviceTypeEvent;
import com.ctnf.client.DeviceTypeEventHandler;
import com.ctnf.client.EventContext;
import com.ctnf.client.Factory;
import com.ctnf.client.activities.compare.AboutTickets.About;
import com.ctnf.client.activities.compare.filter.CompareFilterEvent;
import com.ctnf.client.activities.compare.filter.CompareFilterEventContext;
import com.ctnf.client.activities.compare.filter.CompareFilterValues;
import com.ctnf.client.activities.generic.GenericPlace;
import com.ctnf.client.activities.generic.Page;
import com.ctnf.client.activities.search.SearchPlace;
import com.ctnf.client.uibinder.menu.MenuEvent;
import com.ctnf.client.uibinder.menu.MenuEventContext;
import com.ctnf.client.utils.Utils;
import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class CompareActivity extends AbstractActivity implements ComparePresenter{

	private final Factory 	factory;
	private CompareView 	view = null;
	private ComparePlace 	place = null;
	private DeviceType deviceType = Utils.getDeviceType( Window.getClientWidth() );
	
	//regular tickets results
	private List<TicketsBlock> regularTicketBlocks = null;
	//source_block_id,JavaScriptObject
	private Map<String,JavaScriptObject> regularTicketBlocksAsJavaScriptObject;
	private AboutTickets regularAboutTickets = new AboutTickets();
	
	//parking tickets results
	private List<TicketsBlock> parkingTicketBlocks = null;
	private AboutTickets parkingAboutTickets = new AboutTickets();

	//indicate about filter state.for example if user press on parking option
	//and try to manipulate parking filters
	//if after parking mng user press reset or uncheck parking we need init and update regular tickets
	private boolean flagAfterParkingView = false;
	
	private HandlerRegistration  handlerRegistration = null;

	@Override
	public void onCancel() {
		handlerRegistration.removeHandler();
		super.onCancel();
	}

	@Override
	public void onStop() {
		handlerRegistration.removeHandler();
		super.onStop();
	}

	public CompareActivity(ComparePlace place, final Factory factory){
		this.factory = factory;
		this.place = place;
	}
	
	@Override
	public void start(final AcceptsOneWidget panel, EventBus eventBus) {
		
		//this.showMenu(false);
		
		this.bind();//events listener and process
		
		//main view
		this.view = this.factory.getCompareView(this.deviceType);
		this.view.setComparePresenter(this);	
		this.view.setFilters(this.factory);
		
		this.view.reset();
		
		this.view.preInit();
				
		this.initFilters();
		
		panel.setWidget(view.asWidget());
		
		this.getSetCompareResults( this.place);			
	}
	
	private void getSetCompareResults(final ComparePlace place) {
		
		final CompareRequest request = new CompareRequest();
		request.setRequests(place.getSources());
		request.setEventName(place.getEventName());
		request.setVenueName(place.getVenue());
		request.setCitiesClose(place.getCitiesClose());
		request.setDate(place.getDate());
		request.setSegmentKey(place.getSegmentKey());
		
		final float start = System.currentTimeMillis();
		
		this.factory.getCompareService().compare(request,new AsyncCallback<CompareResponse>() {
			@Override
			public void onSuccess(CompareResponse response) 
			{				
				//in case that request and respnse have kind of mismatch
				if( ! response.getStatus().equals(CompareResponseStatus.confirmed) )
				{
					goTo(new SearchPlace(
							IndexKey.event,request.getEventName(),QueryLocationIndex.ignore,null));
				}
				
				view.setHeader(
						response.getEventDate(),
						response.getEventVenue(),
						response.getEventSegmentKey(),
						response.getEventCity(),
						response.getEventName());

				regularAboutTickets.setCurrency(response.getCurrency());
				parkingAboutTickets.setCurrency(response.getCurrency());

				processResults(response.getTickets(),regularAboutTickets,parkingAboutTickets);
			
				view.init( regularTicketBlocks , regularAboutTickets ,false);	
				
				//found tickets
				if( regularTicketBlocks.size() > 0 )
				{					
					boolean isParkingExist = parkingAboutTickets.getAboutPerSectionTickets().size()==0?false:true;
					
					updateFilters(regularTicketBlocks,regularAboutTickets,isParkingExist);
					
					//gs
					String directUrl = regularAboutTickets.getSourceDirectLinkToPage().get(Source.ticketcity);
					if(directUrl!=null){
						GWT.log("ticketcity: "+directUrl);
					}
					//gs
					
				
					String mapId = null;
					
					view.initMap(
							ticketsBlockToJavaScriptObjectArray( regularTicketBlocks ),
							mapId,
							response.getDateForMap(),
							response.getEventSourceName(),
							response.getEventSourceVenue(),
							place.getUrl());
					
					view.notifications();
					
				}
				else
				{
					view.noTicketsAvailable();
				}
				
				regularAboutTickets.setTakeTime((System.currentTimeMillis()-start));
				//view.setAboutTickets(regularAboutTickets);		
			}
			
			@Override
			public void onFailure(Throwable caught) {
				goTo(new GenericPlace(Page.error,caught));
			}
		});
	}
	
	private boolean isParkingBlock(TicketsBlock ticketsBlock) {

		String section = ticketsBlock.getSection().toLowerCase();

		if (section != null && (!section.equals("")) && section.contains("parking")) {
			return true;
		}

		String row = ticketsBlock.getRow();
		if (row != null) {
			row = row.toLowerCase();
			if ((!row.equals("")) && row.contains("parking")) {
				return true;
			}
		}
		
		String note = ticketsBlock.getComments();
		if (note != null) {
			note = note.toLowerCase();
			if (
				(!note.equals("")) 
				&& 
				( note.contains("parking pass only") || note.contains("no admission to event") ) ) {
				return true;
			}
		}
		
		return false;
	}

	private void bind() {
		
		handlerRegistration = this.factory.getEventBus().addHandler(CompareEvent.TYPE,new CompareEventHandler() {			
			@Override
			public void process(CompareEvent event) {
				
				Map<String,Object> context = event.getContext().getContext();
				
				String type = (String)context.get(EventContext.EVENT_TYPE);
				
				if(CompareEventContext.VALUE_UPDATED.equals(type))
				{
					
					GWT.log("$$$ CompareActivite receive VALUE_UPDATED");

					CompareFilterValues compareFilterValues =(CompareFilterValues)context.get(CompareEventContext.CompareFilterValues);
					applyFilters(compareFilterValues);
				}
				else if(CompareEventContext.RESET.equals(type))
				{
					GWT.log("$$$ CompareActivite receive RESET");


					CompareFilterValues compareFilterValues =(CompareFilterValues)context.get(CompareEventContext.CompareFilterValues);
					reset(compareFilterValues);
				}
			}
		});
		
		this.factory.getEventBus().addHandler(DeviceTypeEvent.TYPE,new DeviceTypeEventHandler() {
			
			@Override
			public void setDeviceTypeEvent(DeviceTypeEvent event) {
				
				switch (event.getType()) {
				case Phone:
				case Tablet:
					if( deviceType.equals(DeviceType.MediumDesktop) || deviceType.equals(DeviceType.LargeDesktop)  ){
						Window.Location.reload();
					}
					break;
				case MediumDesktop:
				case LargeDesktop:
					if( deviceType.equals(DeviceType.Phone) || deviceType.equals(DeviceType.Tablet)  ){
						Window.Location.reload();
					}				
					break;
				}
			}
		});	
	}
	
	private void reset(CompareFilterValues compareFilterValues)
	{
		if(this.flagAfterParkingView==true)
		{
			this.initFilters();
			
			boolean isParkingExist = parkingAboutTickets.getAboutPerSectionTickets().size()==0?false:true;

			this.updateFilters(regularTicketBlocks, regularAboutTickets, isParkingExist);
			
			this.view.adjust();
		}
		
		this.flagAfterParkingView = false;
		
		if(compareFilterValues.getSelectedSectionsFromMap()!=null&&
		   compareFilterValues.getSelectedSectionsFromMap().size()>0)
		{
			view.sectionsBlinking(compareFilterValues.getSelectedSectionsFromMap(), false);
			compareFilterValues.setSelectedSectionsFromMap(null);
		}	
		
		//                                        reset always to regular tickets 
		this.view.setData(this.getTicketsBlocks(),false);
		this.view.updateMap(this.getTicketsBlockAsJavaScriptObjectArray());
		this.updateSectionSelect(this.regularTicketBlocks, this.regularAboutTickets);
	}
	
	private void applyFilters(CompareFilterValues compareFilterValues) 
	{
		
		if(compareFilterValues.isShowAllStared()==true)
		{
			this.view.adjustStarred();
			compareFilterValues.setShowAllStared(false);
			return;
		}
		
		List<TicketsBlock> newFilter = new ArrayList<TicketsBlock>();

		boolean showParkingTickets = compareFilterValues.isShowParkingTickets();
		
		if ( showParkingTickets == true ) 
		{
			if(flagAfterParkingView==false)//first parking init
			{
				this.view.disableMap();
								
				this.updateFilters(parkingTicketBlocks,parkingAboutTickets,true);

				for (TicketsBlock ticketsBlock : this.parkingTicketBlocks) 
				{
					newFilter.add(ticketsBlock);
				}
			}
			else // filters on parking 
			{
				for (TicketsBlock ticketsBlock : this.parkingTicketBlocks) 
				{
					if (ticketsBlock.getPrice().doubleValue() >= compareFilterValues.getPriceMinCurrentValue()
					 && ticketsBlock.getPrice().doubleValue() <= compareFilterValues.getPriceMaxCurrentValue()
					 && Utils.getMaxNumber(ticketsBlock.getSaleSize()) >= compareFilterValues.getQuantityCurrentValue()
					 && this.isAddSectionFromSelect(ticketsBlock,compareFilterValues.getSelectedSectionsFromSelect()) == true) 
					{
						newFilter.add(ticketsBlock);
					}
				}
			}
			
			view.setData(newFilter,showParkingTickets);
			
			if(compareFilterValues.isEscapeMultipleSelectSections()==false&&
		(compareFilterValues.getSelectedSectionsFromSelect()==null||compareFilterValues.getSelectedSectionsFromSelect().size()==0))
			{
				this.updateSectionSelect(newFilter,parkingAboutTickets);
			}
			
			flagAfterParkingView = true;
		} 
		else if ( showParkingTickets == false) 
		{
			if(flagAfterParkingView==true)
			{
				this.view.enableMap();

				this.initFilters();
				
				updateFilters(regularTicketBlocks,regularAboutTickets,true);
				
				this.reset(compareFilterValues);
				
				flagAfterParkingView = false;

				return;
			}
			
			for (TicketsBlock ticketsBlock : this.getTicketsBlocks()) 
			{
				if (ticketsBlock.getPrice().doubleValue() >= compareFilterValues.getPriceMinCurrentValue()
				&&  ticketsBlock.getPrice().doubleValue() <= compareFilterValues.getPriceMaxCurrentValue()
				&&  Utils.getMaxNumber(ticketsBlock.getSaleSize()) >= compareFilterValues.getQuantityCurrentValue()
				&&  this.isAddSectionFromMap(ticketsBlock,compareFilterValues.getSelectedSectionsFromMap()) == true
				&&  this.isAddSectionFromSelect(ticketsBlock,compareFilterValues.getSelectedSectionsFromSelect()) == true
				) 
				{
					newFilter.add(ticketsBlock);
				}
			}

			view.setData(newFilter,showParkingTickets);

			// update map only when no filters on map level
			if (compareFilterValues.getSelectedSectionsFromMap() == null || compareFilterValues.getSelectedSectionsFromMap().size() == 0) 
			{
				view.updateMap(newFilter);
				
				if(compareFilterValues.isEscapeMultipleSelectSections()==false&&
				   (compareFilterValues.getSelectedSectionsFromSelect()==null||compareFilterValues.getSelectedSectionsFromSelect().size()==0))
				{
					this.updateSectionSelect(newFilter,regularAboutTickets);
				}
			}		
		}		
	}

	//throw event to filters about selected/deselected sections from map 
	@Override
	public void onChangeSectionsFromMap(List<String> selectedSectionsFromMap) {
		
		
		GWT.log("$$$ CompareActivite fire ChangeSectionsFromMap");


		Map<String,Object> data = new HashMap<String,Object>();
		data.put(CompareFilterEventContext.EVENT_TYPE, CompareFilterEventContext.MAP);
		data.put(CompareFilterEventContext.ChangeSectionsFromMap,selectedSectionsFromMap);
		CompareFilterEventContext context = new CompareFilterEventContext(data);
		this.factory.getEventBus().fireEvent(new CompareFilterEvent(context));
	}
	
	
	
	
	private boolean isAddSectionFromMap(TicketsBlock ticketsBlock, List<String> selectedSectionsFromMap)
	{
		if(selectedSectionsFromMap==null||selectedSectionsFromMap.size()==0)
		{
			return true;
		}
		else
		{
			for(String selectedSectionFromMap : selectedSectionsFromMap)
			{
				if(selectedSectionFromMap.equals(ticketsBlock.getSection()))
				{					
					return true;
				}
			}
		}
		
		return false;
	}
	
	private boolean isAddSectionFromSelect(TicketsBlock ticketsBlock, List<String> selectedSectionsFromSelect)
	{
		if(selectedSectionsFromSelect==null||selectedSectionsFromSelect.size()==0)
		{
			return true;
		}
		else
		{
			for(String selectedSectionFromSelect : selectedSectionsFromSelect)
			{
				if(selectedSectionFromSelect.equals(ticketsBlock.getSection()))
				{					
					return true;
				}
			}
		}
		
		return false;
	}
	
	private void processResults(
			List<Tickets> allTickets,
			AboutTickets aboutRegularTickets,
			AboutTickets aboutParkingTickets){
		
		Map<String,List<TicketsBlock>> regularTickets = new HashMap<String,List<TicketsBlock>>();
		Map<String,List<TicketsBlock>> parkingTickets = new HashMap<String,List<TicketsBlock>>();
				
		//merge all sections from diff sources 
		
		for(Tickets ticket : allTickets)
		{
			regularAboutTickets.getSourceDirectLinkToPage().put(ticket.getSource(),ticket.getPage());
			
			for(Entry<String,List<TicketsBlock>> entry : ticket.getBlocks().entrySet())
			{
				boolean isParkingBlock = this.isParkingBlock(entry.getValue().get(0));
				
				if(isParkingBlock==true)
				{
					if(parkingTickets.containsKey(entry.getKey())==true)
					{
						parkingTickets.get(entry.getKey()).addAll(entry.getValue());
					}
					else
					{					
						parkingTickets.put(entry.getKey(),entry.getValue());
					}	
				}
				else
				{
					if(regularTickets.containsKey(entry.getKey())==true)
					{
						regularTickets.get(entry.getKey()).addAll(entry.getValue());
					}
					else
					{					
						regularTickets.put(entry.getKey(),entry.getValue());
					}	
				}
			}
		}
		
		//process about and adapt to blocks list
		
		List<TicketsBlock> finalResultRegularTickets = new ArrayList<TicketsBlock>();
		
		for(Map.Entry<String,List<TicketsBlock>> entry : regularTickets.entrySet())
		{
			finalResultRegularTickets.addAll(entry.getValue());
			
			this.processAboutSection( entry.getValue() , regularAboutTickets , entry.getKey() );	
		}
		this.processAboutSummary( regularAboutTickets );
		this.regularTicketBlocks= finalResultRegularTickets;	
		
		Collections.sort(regularTicketBlocks, new Comparator<TicketsBlock>() {
			@Override
			public int compare(TicketsBlock o1, TicketsBlock o2) {				
				return o1.getPrice().compareTo(o2.getPrice());
			}
		});
		
		
		
		//parking 
		List<TicketsBlock> finalResultParkingTickets = new ArrayList<TicketsBlock>();
		
		for(Map.Entry<String,List<TicketsBlock>> entry : parkingTickets.entrySet())
		{
			finalResultParkingTickets.addAll(entry.getValue());
			
			this.processAboutSection( entry.getValue() , parkingAboutTickets , entry.getKey() );	
		}
		this.processAboutSummary( parkingAboutTickets );
		this.parkingTicketBlocks = finalResultParkingTickets;
		Collections.sort(this.parkingTicketBlocks, new Comparator<TicketsBlock>() {
			@Override
			public int compare(TicketsBlock o1, TicketsBlock o2) {
				return o1.getPrice().compareTo(o2.getPrice());
			}
		});
	}
	
	@Override
	public JavaScriptObject[] getTicketsBlockAsJavaScriptObjectArray() {
		
		JavaScriptObject[] result = new JavaScriptObject[this.regularTicketBlocks.size()];

		int index=0;
		for(JavaScriptObject javaScriptObject : this.regularTicketBlocksAsJavaScriptObject.values())	{
			result[index] = javaScriptObject;
			index++;
		}
		
		return result;
	}
	
	@Override
	public JavaScriptObject[] ticketsBlockToJavaScriptObjectArray(List<TicketsBlock> ticketsBlock) 
	{			
		JavaScriptObject[] result = new JavaScriptObject[ticketsBlock.size()];

		if(this.regularTicketBlocksAsJavaScriptObject==null)
		{
			this.initRegularTicketBlocksAsJavaScriptObject(ticketsBlock);
			
			int index=0;
			for(JavaScriptObject javaScriptObject : this.regularTicketBlocksAsJavaScriptObject.values())	{
				result[index] = javaScriptObject;
				index++;
			}
		}
		else
		{
			int index=0;
			for(TicketsBlock ticketBlock : ticketsBlock)
			{
				JavaScriptObject javaScriptObject = 
						this.regularTicketBlocksAsJavaScriptObject.get(
						ticketBlock.getTickets().getSource().name()+"_"+ticketBlock.getBlockId());
				
				result[index] = javaScriptObject;
				index++;
			}
		}
				
		return result;
	}

	private Map<String,JavaScriptObject> initRegularTicketBlocksAsJavaScriptObject(List<TicketsBlock> ticketsBlock)
	{
		this.regularTicketBlocksAsJavaScriptObject = new HashMap<String,JavaScriptObject>(ticketsBlock.size());
				
		for(TicketsBlock ticketBlock : ticketsBlock)
		{
			StringBuilder builder = new StringBuilder();

			builder.append("{\"id\":\""+ticketBlock.getBlockId()+"#"+ticketBlock.getTickets().getSource().getName()+"\",");
			builder.append("\"section\":\""+ticketBlock.getSection().replace("\"", "")+"\",");
			builder.append("\"row\":\""+ticketBlock.getRow().replace("\"", "")+"\",");
			
			if(ticketBlock.getSaleSize().size()==1)
			{
				builder.append("\"quantity\":"+ticketBlock.getSaleSize().get(0)+",");
			}
			else
			{
				builder.append("\"quantity\":[");
				for(int saleSizeIndex=0; saleSizeIndex < ticketBlock.getSaleSize().size();saleSizeIndex++)
				{
					if( saleSizeIndex < ticketBlock.getSaleSize().size() - 1 )
					{
						builder.append(ticketBlock.getSaleSize().get(saleSizeIndex)+",");
					}
					else
					{
						builder.append(ticketBlock.getSaleSize().get(saleSizeIndex));
					}
				}
				builder.append("],");
			}
			
			String price = Utils.usdNumberFormat.format(ticketBlock.getPrice());
			builder.append("\"price\":\""+price+"\"}");
			
			JavaScriptObject javaScriptObject = JsonUtils.safeEval(builder.toString());
			
			this.regularTicketBlocksAsJavaScriptObject.put(
					ticketBlock.getTickets().getSource().name()+"_"+ticketBlock.getBlockId(), javaScriptObject);
		}
		
//		for(JavaScriptObject r : result)
//		{
//			GWT.log(new JSONObject(r).toString());
//		}

		return this.regularTicketBlocksAsJavaScriptObject;
	}
	
//	public JavaScriptObject getTicketsBlockIdAsJavaScriptObject(List<TicketsBlock> ticketsBlock) {
//				
//		StringBuilder builder = new StringBuilder();
//
//		builder.append("{\"Ids\":[");
//
//		for(int ticketBlockIndex=0;ticketBlockIndex<ticketsBlock.size();ticketBlockIndex++)
//		{
//			TicketsBlock ticketBlock = ticketsBlock.get(ticketBlockIndex);
//
//			if( ticketBlockIndex < ticketsBlock.size() - 1 )
//			{
//				builder.append("\""+ticketBlock.getBlockId()+"#"+ticketBlock.getTickets().getSource().getName()+"\",");
//			}
//			else
//			{
//				builder.append("\""+ticketBlock.getBlockId()+"#"+ticketBlock.getTickets().getSource().getName()+"\"");
//			}			
//		}
//		
//		builder.append("]}");
//
//		JavaScriptObject javaScriptObject = JsonUtils.safeEval(builder.toString());
//
//		GWT.log(new JSONObject(javaScriptObject).toString());
//		
//		return javaScriptObject;
//	}
	
	
	
	private void processAboutSummary(AboutTickets aboutTickets)
	{
		double 	avgPrice = 0;
		double 	minPrice = 100000d;
		double 	maxPrice = 0;
		int 	totalTicketsNumber = 0;
		double 	totalTicketsPrice = 0;
		Set<Integer> quantityOptions = new HashSet<Integer>(); 
		boolean includeParkingTickets = false;

		for(About aboutSection : aboutTickets.getAboutPerSectionTickets().values())
		{
			//catch only once ...blobal indicator
			if(includeParkingTickets==false){
				includeParkingTickets = aboutSection.isParkingTickets();
			}
			
			quantityOptions.addAll(aboutSection.getQuantityOptions());
			
			//---
			
			totalTicketsPrice+=aboutSection.getTotalTicketsPrice();
			
			//---
			
			if( minPrice > aboutSection.getMinPrice()  )
			{
				minPrice = aboutSection.getMinPrice();
			}	
			
			if( maxPrice < aboutSection.getMaxPrice()  )
			{
				maxPrice = aboutSection.getMaxPrice();
			}	
			
			//---
			
			totalTicketsNumber+=aboutSection.getTotalTicketsNumber();
		}
		
		avgPrice = (totalTicketsPrice/totalTicketsNumber);
		
		aboutTickets.getAboutAllTickets().setAvgPrice( avgPrice );
		aboutTickets.getAboutAllTickets().setMinPrice(minPrice);
		aboutTickets.getAboutAllTickets().setMaxPrice(maxPrice);
		aboutTickets.getAboutAllTickets().setTotalTicketsNumber(totalTicketsNumber);
		aboutTickets.getAboutAllTickets().setTotalTicketsPrice(totalTicketsPrice);
		aboutTickets.getAboutAllTickets().setQuantityOptions(quantityOptions);
		
		//---
		
		if(quantityOptions!=null&&quantityOptions.size()>0)
		{
			int minQuantity = Collections.min(quantityOptions);
			int maxQuantity = Collections.max(quantityOptions);
	
			aboutTickets.setMinQuantity(minQuantity);
			aboutTickets.setMaxQuantity(maxQuantity);
		}
	}
	
	private void processAboutSection(List<TicketsBlock> allSectionTickets,AboutTickets aboutTickets,String sectionName)
	{
		double avgPrice = 0;
		double minPrice = 100000d;
		double maxPrice = 0;
		
		int totalTicketsNumber = 0;
		double totalTicketsPrice = 0;
		Set<Integer> quantityOptions = new LinkedHashSet<Integer>(); 
		
		boolean isParking = false;
		
		for(TicketsBlock block : allSectionTickets)
		{
			isParking  = this.isParkingBlock(block);
			
			double price = block.getPrice().doubleValue();

			int saleSize = Utils.getMaxNumber(block.getSaleSize());
			
			quantityOptions.addAll(block.getSaleSize());
			
			totalTicketsNumber+=saleSize;
			
			totalTicketsPrice+=(price*saleSize);
			
			if( minPrice > price  )
			{
				minPrice = price;
			}	
			
			if( maxPrice < price  )
			{
				maxPrice = price;
			}	
		}
		
		AboutTickets.About about = new About();
		
		avgPrice = (totalTicketsPrice/totalTicketsNumber);
		
		about.setAvgPrice( avgPrice );
		about.setMinPrice(minPrice);
		about.setMaxPrice(maxPrice);
		about.setTotalTicketsNumber(totalTicketsNumber);
		about.setTotalTicketsPrice(totalTicketsPrice);
		about.setQuantityOptions(quantityOptions);
		about.setParkingTickets(isParking);

		String price = null;
//		if(about.getMinPrice()==about.getMaxPrice()){
//			price = Utils.usdNumberFormat.format(about.getMinPrice());
//		}
//		else{
//			price = Utils.usdNumberFormat.format(about.getMinPrice())+"-"+Utils.usdNumberFormat.format(about.getMaxPrice());
//		}
		
		price = Utils.usdNumberFormat.format(about.getMinPrice());
		
//		String summarySectionInfo  = 
//				(about.getTotalTicketsNumber()==1)?
//						price+","+ about.getTotalTicketsNumber()+" ticket":
//						price+","+ about.getTotalTicketsNumber()+" tickets";
		
		String summarySectionInfo  = 
				(about.getTotalTicketsNumber()==1)?
						about.getTotalTicketsNumber()+" ticket "+price:
						about.getTotalTicketsNumber()+" tickets from "+price;
		
		about.setSummarySectionInfo(" ("+summarySectionInfo+")");
		
		aboutTickets.getAboutPerSectionTickets().put( sectionName , about );		
	}
	
	@Override
	public void goTo(Place place) {
		this.factory.getPlaceController().goTo(place);
	}

	@Override
	public  List<TicketsBlock> getTicketsBlocks() {
		return regularTicketBlocks;
	}
	
	@Override
	public AboutTickets getAboutTickets() {
		return this.regularAboutTickets;
	}

	@Override
	public Set<Integer> calculateQuantityOptions(List<TicketsBlock> ticketsBlocks)
	{
		Set<Integer> quantityOptions = new HashSet<Integer>(); 

		for(TicketsBlock ticketsBlock : ticketsBlocks)
		{
			quantityOptions.addAll(ticketsBlock.getSaleSize());
		}
		
		return quantityOptions;
	}
	
	@Override
	public Set<BigDecimal> calculateMinMaxPrice(List<TicketsBlock> ticketsBlocks)
	{
		Set<BigDecimal> minMaxPrice = new HashSet<BigDecimal>(); 

		for(TicketsBlock ticketsBlock : ticketsBlocks)
		{
			minMaxPrice.add(ticketsBlock.getPrice());
		}
		
		return minMaxPrice;
	}
	
	@Override
	public ComparePlace getPlace(){
		return this.place;
	}


	@Override
	public void updateAddThis(String title) {
		this.factory.setAddThis(this.place.getUrl(),title);
	}

	@Override
	public ComparePlace getComparePlace() {
		return this.place;
	}

	@Override
	public Factory getFactory() {
		return this.factory;
	}
	
	@Override
	public void showMenu(boolean showMenu) {
		Map<String,Object> data = new HashMap<String,Object>();
		if(showMenu==true){
			data.put(MenuEventContext.EVENT_TYPE, MenuEventContext.ShowMenu);
		}
		else{
			data.put(MenuEventContext.EVENT_TYPE, MenuEventContext.HideMenu);
		}
		MenuEventContext context = new MenuEventContext(data);
		this.factory.getEventBus().fireEvent(new MenuEvent(context));
	}
	
	@Override
	public void initFilters() {
		

		GWT.log("$$$ CompareActivite fire INIT");

		Map<String,Object> data = new HashMap<String,Object>();
		data.put(CompareFilterEventContext.EVENT_TYPE, CompareFilterEventContext.INIT);
		CompareFilterEventContext context = new CompareFilterEventContext(data);
		this.factory.getEventBus().fireEvent(new CompareFilterEvent(context));
	}
	
	@Override
	public void updateFilters(List<TicketsBlock> ticketsBlocks, AboutTickets aboutTickets,boolean isParkingExist) {
		
		GWT.log("$$$ CompareActivite fire SET");

		Map<String,Object> data = new HashMap<String,Object>();
		data.put(CompareFilterEventContext.EVENT_TYPE, CompareFilterEventContext.SET);
		data.put(CompareFilterEventContext.TicketsBlocks, ticketsBlocks);
		data.put(CompareFilterEventContext.AboutTickets, aboutTickets);
		data.put(CompareFilterEventContext.HaveParkingTickets, isParkingExist);
				
		CompareFilterEventContext context = new CompareFilterEventContext(data);
		this.factory.getEventBus().fireEvent(new CompareFilterEvent(context));
	}

	@Override
	public void resetFilters() {
		
		
		GWT.log("$$$ CompareActivite fire RESET");


		Map<String,Object> data = new HashMap<String,Object>();
		data.put(CompareFilterEventContext.EVENT_TYPE, CompareFilterEventContext.RESET);
		CompareFilterEventContext context = new CompareFilterEventContext(data);
		this.factory.getEventBus().fireEvent(new CompareFilterEvent(context));
	}
	

	public void updateSectionSelect(List<TicketsBlock> ticketsBlocks, AboutTickets aboutTickets) {
		
		
		GWT.log("$$$ CompareActivite fire UPDATE_SECTION_SELECT");


		Map<String,Object> data = new HashMap<String,Object>();
		data.put(CompareFilterEventContext.EVENT_TYPE, CompareFilterEventContext.UPDATE_SECTION_SELECT);
		data.put(CompareFilterEventContext.TicketsBlocks, ticketsBlocks);
		data.put(CompareFilterEventContext.AboutTickets, aboutTickets);
				
		CompareFilterEventContext context = new CompareFilterEventContext(data);
		this.factory.getEventBus().fireEvent(new CompareFilterEvent(context));
	}
	
	@Override
	public void noSeatingChartReport(String title,String message)
	{
		this.factory.getMaintenanceService().report(title, message, new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {}
			@Override
			public void onSuccess(Void result) {}
		});
	}
}





