package com.ctnf.client.activities.compare.filter;
import java.util.List;

public class CompareFilterValues {

	private double quantityMin = 0d;
	private double quantityMax = 0d;
	private double quantityCurrentValue = 0d;
	
	private double priceMin = 0d;
	private double priceMax = 0d;
	private double priceMinCurrentValue = 0d;
	private double priceMaxCurrentValue = 0d;
		
	private List<String> selectedSectionsFromMap = null;
	
	private boolean showParkingTickets = false;
	
	private boolean showAllStared = false;
	
	private List<String> selectedSectionsFromSelect = null;
	private boolean escapeMultipleSelectSections = false;

	
	public boolean isShowAllStared() {
		return showAllStared;
	}
	public void setShowAllStared(boolean showAllStared) {
		this.showAllStared = showAllStared;
	}
	
	public boolean isEscapeMultipleSelectSections() {
		return escapeMultipleSelectSections;
	}
	public void setEscapeMultipleSelectSections(boolean escapeMultipleSelectSections) {
		this.escapeMultipleSelectSections = escapeMultipleSelectSections;
	}
	public List<String> getSelectedSectionsFromSelect() {
		return selectedSectionsFromSelect;
	}
	public void setSelectedSectionsFromSelect(List<String> selectedSectionsFromSelect) {
		this.selectedSectionsFromSelect = selectedSectionsFromSelect;
	}
	public boolean isShowParkingTickets() {
		return showParkingTickets;
	}
	public void setShowParkingTickets(boolean showParkingTickets) {
		this.showParkingTickets = showParkingTickets;
	}
	public List<String> getSelectedSectionsFromMap() {
		return selectedSectionsFromMap;
	}
	public void setSelectedSectionsFromMap(List<String> selectedSectionsFromMap) {
		this.selectedSectionsFromMap = selectedSectionsFromMap;
	}
	public double getQuantityMin() {
		return quantityMin;
	}
	public void setQuantityMin(double quantityMin) {
		this.quantityMin = quantityMin;
	}
	public double getQuantityMax() {
		return quantityMax;
	}
	public void setQuantityMax(double quantityMax) {
		this.quantityMax = quantityMax;
	}
	
	public double getPriceMin() {
		return priceMin;
	}
	public void setPriceMin(double priceMin) {
		this.priceMin = priceMin;
	}
	public double getPriceMax() {
		return priceMax;
	}
	public void setPriceMax(double priceMax) {
		this.priceMax = priceMax;
	}
	public double getQuantityCurrentValue() {
		return quantityCurrentValue;
	}
	public void setQuantityCurrentValue(double quantityCurrentValue) {
		this.quantityCurrentValue = quantityCurrentValue;
	}
	public double getPriceMinCurrentValue() {
		return priceMinCurrentValue;
	}
	public void setPriceMinCurrentValue(double priceMinCurrentValue) {
		this.priceMinCurrentValue = priceMinCurrentValue;
	}
	public double getPriceMaxCurrentValue() {
		return priceMaxCurrentValue;
	}
	public void setPriceMaxCurrentValue(double priceMaxCurrentValue) {
		this.priceMaxCurrentValue = priceMaxCurrentValue;
	}
	@Override
	public String toString() {
		return "CompareFilterValues [quantityMin=" + quantityMin + ", quantityMax=" + quantityMax
				+ ", quantityCurrentValue=" + quantityCurrentValue + ", priceMin=" + priceMin + ", priceMax=" + priceMax
				+ ", priceMinCurrentValue=" + priceMinCurrentValue + ", priceMaxCurrentValue=" + priceMaxCurrentValue
				+ ", selectedSectionsFromMap=" + selectedSectionsFromMap + "]";
	}
	
	
	
}
