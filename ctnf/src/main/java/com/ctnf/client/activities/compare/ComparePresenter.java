package com.ctnf.client.activities.compare;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import com.ctnc.shared.TicketsBlock;
import com.ctnf.client.Factory;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.place.shared.Place;

public interface ComparePresenter {
	void goTo(Place place);
	List<TicketsBlock> getTicketsBlocks();
	AboutTickets getAboutTickets();
	Set<Integer> calculateQuantityOptions(List<TicketsBlock> ticketsBlocks);
	Set<BigDecimal> calculateMinMaxPrice(List<TicketsBlock> ticketsBlocks);
	ComparePlace getPlace();
	void updateAddThis(String title);
	ComparePlace getComparePlace();
	Factory getFactory();
	JavaScriptObject[] getTicketsBlockAsJavaScriptObjectArray();
	JavaScriptObject[] ticketsBlockToJavaScriptObjectArray(List<TicketsBlock> ticketsBlock);
	void initFilters();
	void updateFilters(List<TicketsBlock> ticketsBlocks, AboutTickets aboutTickets,boolean isParkingExist);
	void onChangeSectionsFromMap(List<String> selectedSectionsFromMap);
	void resetFilters();
	void showMenu(boolean showMenu);
	void noSeatingChartReport(String title,String message);
}
