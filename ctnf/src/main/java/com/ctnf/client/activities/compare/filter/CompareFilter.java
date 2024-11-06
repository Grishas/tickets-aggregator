package com.ctnf.client.activities.compare.filter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ctnc.shared.TicketsBlock;
import com.ctnf.client.DeviceType;
import com.ctnf.client.Factory;
import com.ctnf.client.activities.compare.AboutTickets;
import com.ctnf.client.activities.compare.CompareEvent;
import com.ctnf.client.activities.compare.CompareEventContext;
import com.ctnf.client.utils.Utils;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Window;
import com.google.web.bindery.event.shared.EventBus;

public class CompareFilter implements CompareFilterPresenter{

	private final EventBus eventBus;
	private final Factory factory;
	private CompareFilterView view = null;
	private DeviceType deviceType = Utils.getDeviceType(Window.getClientWidth());

	public CompareFilter(Factory factory) 
	{	
		this.factory = factory;	
		this.eventBus = this.factory.getEventBus();
		this.view = this.factory.getCompareFilterView(this.deviceType);
		this.view.setPresenter(this);	
		this.view.adjust(this.deviceType);
		bind();		
	}
	
	@Override
	public void goTo(Place place) {
		this.factory.getPlaceController().goTo(place);
	}

	private void bind() {
		
		eventBus.addHandler(CompareFilterEvent.TYPE,new CompareFilterEventHandler() {			
			@SuppressWarnings("unchecked")
			@Override
			public void process(CompareFilterEvent event) {
				
				Map<String,Object> context = event.getContext().getContext();
				
				String type = (String)context.get(CompareFilterEventContext.EVENT_TYPE);
				
				if(CompareFilterEventContext.INIT.equals(type))
				{
					GWT.log("@@@ CompareFilter receive INIT");

					view.init();					
				}
				else if(CompareFilterEventContext.SET.equals(type))
				{
					GWT.log("@@@ CompareFilter receive SET");

					AboutTickets aboutTickets = 
							(AboutTickets)context.get(CompareFilterEventContext.AboutTickets);
					
					List<TicketsBlock> ticketsBlocks = 
							(List<TicketsBlock>)context.get(CompareFilterEventContext.TicketsBlocks);
					
					boolean haveParkingTickets = 
							Boolean.valueOf(
									(String.valueOf(context.get(CompareFilterEventContext.HaveParkingTickets))));
					
					view.update(aboutTickets,ticketsBlocks,haveParkingTickets);
					
					view.setSelectSectionState();

				}	
				else if(CompareFilterEventContext.MAP.equals(type))
				{
					
					GWT.log("@@@ CompareFilter receive MAP");


					List<String> selectedSectionsFromMap = 
							(List<String>)context.get(CompareFilterEventContext.ChangeSectionsFromMap);
					
					CompareFilterValues compareFilterValues = view.getCompareFilterValues();
					compareFilterValues.setSelectedSectionsFromMap(selectedSectionsFromMap);
					view.setButtonResetState(compareFilterValues);
					
					view.setSelectSectionState();
					
					valueUpdated(compareFilterValues);
				}
				else if(CompareFilterEventContext.RESET.equals(type))
				{
					
					GWT.log("@@@ CompareFilter receive RESET");

					view.resetFilters();
				}
				else if(CompareFilterEventContext.UPDATE_SECTION_SELECT.equals(type))
				{
					
					GWT.log("@@@ CompareFilter receive UPDATE_SECTION_SELECT");

					AboutTickets aboutTickets = 
							(AboutTickets)context.get(CompareFilterEventContext.AboutTickets);
					
					List<TicketsBlock> ticketsBlocks = 
							(List<TicketsBlock>)context.get(CompareFilterEventContext.TicketsBlocks);
					
					view.setSectionSelectUpdate(ticketsBlocks,aboutTickets);

				}
			}
		});
	}
	
	@Override
	public void valueUpdated(CompareFilterValues compareFilterValues) 	{
		
		
		GWT.log("@@@ CompareFilter fire VALUE_UPDATED");


		
		Map<String,Object> data = new HashMap<String,Object>();
		data.put(CompareEventContext.EVENT_TYPE, CompareEventContext.VALUE_UPDATED);
		data.put(CompareEventContext.CompareFilterValues,compareFilterValues);
		CompareEventContext context = new CompareEventContext(data);
		factory.getEventBus().fireEvent(new CompareEvent(context));	
	}

	@Override
	public void reset(CompareFilterValues compareFilterValues) {
		
		
		GWT.log("@@@ CompareFilter fire RESET");


		Map<String,Object> data = new HashMap<String,Object>();
		data.put(CompareEventContext.EVENT_TYPE, CompareEventContext.RESET);
		data.put(CompareEventContext.CompareFilterValues,compareFilterValues);
		CompareEventContext context = new CompareEventContext(data);
		factory.getEventBus().fireEvent(new CompareEvent(context));	
	}
}
