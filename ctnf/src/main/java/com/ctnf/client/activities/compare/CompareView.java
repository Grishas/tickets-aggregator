package com.ctnf.client.activities.compare;
import java.util.List;

import com.ctnc.shared.TicketsBlock;
import com.ctnf.client.DeviceTypeEvent;
import com.ctnf.client.Factory;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.IsWidget;

public interface CompareView extends IsWidget{
	void reset();
	void init(List<TicketsBlock> ticketBlocks, AboutTickets aboutTickets,boolean showParkingTickets);	
	void setComparePresenter(ComparePresenter comparePresenter);
	void preInit();
	void setAboutTickets(AboutTickets aboutTickets);
	void initMap(	JavaScriptObject[] ticketsBlockAsJavaScriptObject, 
					String mapId, 
					String dateForMap,String[] eventSourceName, String[] eventSourceVenue,
					String url);
	void setData(List<TicketsBlock> data,boolean showParkingTickets);
	void updateMap(List<TicketsBlock> data);
	void updateMap(JavaScriptObject[] data);
	void sectionsBlinking(List<String> sections,boolean blinking);
	void disableMap();
	void enableMap();
	void adjust();
	void setHeader(String date,String venue,String segmentKey__,String city,String event) ;
	void setFilters(Factory factory);
	void adjustStarred();
	void noTicketsAvailable();
	void notifications();
}
