package com.ctnf.client.activities.compare.filter;
import java.util.List;

import com.ctnc.shared.TicketsBlock;
import com.ctnf.client.DeviceType;
import com.ctnf.client.activities.compare.AboutTickets;
import com.google.gwt.user.client.ui.IsWidget;

public interface CompareFilterView  extends IsWidget{
	void setPresenter(CompareFilterPresenter presenter);
	void init();
	void update(AboutTickets aboutTickets, List<TicketsBlock> ticketsBlocks,boolean haveParkingTickets);
	CompareFilterValues getCompareFilterValues();
	void setButtonResetState(CompareFilterValues compareFilterValues);
	void resetFilters();
	void adjust(DeviceType deviceType);
	void setSelectSectionState();
	void setSectionSelectUpdate(List<TicketsBlock> ticketsBlocks,AboutTickets aboutTickets);
}
