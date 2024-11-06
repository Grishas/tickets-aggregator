package com.ctnf.client.activities.compare.filter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.Toggle;

import com.ctnc.shared.TicketsBlock;
import com.ctnf.client.activities.compare.AboutTickets;
import com.google.gwt.core.client.GWT;



public class CompareFilterViewCommonImpl {
	public Map<String, PriceRange> pricesRange = new LinkedHashMap<String, PriceRange>();
	public final String ANY_PRICE = "Any Price";
	
	public int minQty=0;
	public int maxQty=0;	
	public final String ANY_QTY = "Any Qty";

	public void wa1(Button button,ButtonType buttonType)
	{
		button.setDataToggle(Toggle.DROPDOWN);
		button.setType(buttonType);
	}
	
	// depend on sorting of ticketBlocks !!!! in compare activity :-)
	public Map<String, PriceRange> calculatePriceRange(List<TicketsBlock> ticketBlocks, AboutTickets aboutTickets,
			int partitionsNumber) {

		List<Double> ranges = new ArrayList<Double>();

		// StringBuffer testBuffer = new StringBuffer();
		for (TicketsBlock ticketsBlock : ticketBlocks) {
			if (!ranges.contains(ticketsBlock.getPrice().doubleValue())) {
				ranges.add(ticketsBlock.getPrice().doubleValue());
			}
		}

		// wa for android default browser
		// Collections.sort(ranges, new Comparator<Double>() {
		// @Override
		// public int compare(Double v1, Double v2) {
		// return v1.compareTo(v2);
		// }
		// });

		// for (Double range : ranges) {
		// GWT.log(String.valueOf(range));
		// }

		GWT.log("Total ranges: " + ranges.size());

		int rangesNumber = ranges.size() / partitionsNumber;

		GWT.log("Ranges number: " + rangesNumber);

		for (int index = 0; index < ranges.size(); index++) {
			PriceRange range = new PriceRange();

			range.min = ranges.get(index);

			if ((index + rangesNumber) < ranges.size()) {
				range.max = ranges.get(index + rangesNumber);
			} else {
				range.max = ranges.get(ranges.size() - 1);
			}

			if (rangesNumber == 0) {
				// range.text =
				// aboutTickets.getCurrency()+String.valueOf(Math.floor(range.min));
				range.text = aboutTickets.getCurrency() + range.min;
			} else {
				// range.text =
				// aboutTickets.getCurrency()+String.valueOf(Math.floor(range.min)
				// +"-"+aboutTickets.getCurrency()+String.valueOf(Math.ceil(range.max)));
				range.text = aboutTickets.getCurrency() + range.min + " - " + aboutTickets.getCurrency() + range.max;

			}

			index = index + rangesNumber;

			GWT.log(range.toString());

			this.pricesRange.put(range.text, range);

		}

		// all prices range
		PriceRange range = new PriceRange();
		range.min = aboutTickets.getAboutAllTickets().getMinPrice();
		range.max = aboutTickets.getAboutAllTickets().getMaxPrice();
		range.text = ANY_PRICE;
		this.pricesRange.put(range.text, range);

		return this.pricesRange;
	}

}