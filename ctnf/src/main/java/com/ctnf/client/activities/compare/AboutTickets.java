package com.ctnf.client.activities.compare;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.ctnc.shared.Source;

public class AboutTickets {

	public static class About {
		
		private double minPrice = 100000d;
		private double maxPrice;
		private double avgPrice;
		private int totalTicketsNumber = 0;
		private double totalTicketsPrice = 0;
		private Set<Integer> quantityOptions = null; 
		private String summarySectionInfo;
		private boolean parkingTickets = false;

		public boolean isParkingTickets() {
			return parkingTickets;
		}
		public void setParkingTickets(boolean parkingTickets) {
			this.parkingTickets = parkingTickets;
		}
		
		public Set<Integer> getQuantityOptions() {
			return quantityOptions;
		}
		public void setQuantityOptions(Set<Integer> quantityOptions) {
			this.quantityOptions = quantityOptions;
		}
		public double getMinPrice() {
			return minPrice;
		}
		public void setMinPrice(double minPrice) {
			this.minPrice = minPrice;
		}
		public double getMaxPrice() {
			return maxPrice;
		}
		public void setMaxPrice(double maxPrice) {
			this.maxPrice = maxPrice;
		}
		public double getAvgPrice() {
			return avgPrice;
		}
		public void setAvgPrice(double avgPrice) {
			this.avgPrice = avgPrice;
		}
		public int getTotalTicketsNumber() {
			return totalTicketsNumber;
		}
		public void setTotalTicketsNumber(int totalTicketsNumber) {
			this.totalTicketsNumber = totalTicketsNumber;
		}
		public double getTotalTicketsPrice() {
			return totalTicketsPrice;
		}
		public void setTotalTicketsPrice(double totalTicketsPrice) {
			this.totalTicketsPrice = totalTicketsPrice;
		}
		public String getSummarySectionInfo() {
			return summarySectionInfo;
		}
		public void setSummarySectionInfo(String summarySectionInfo) {
			this.summarySectionInfo = summarySectionInfo;
		}
	}
	
	private About aboutAllTickets = new About();
	
	private Map<String,About> aboutPerSectionTickets = new HashMap<String, About>();
	
	//value can be null
	private Map<Source,String> sourceDirectLinkToPage = new HashMap<Source,String>();
	
	private int minQuantity;
	private int maxQuantity;

	private float takeTime=0;
	
	private String currency;

	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public int getMinQuantity() {
		return minQuantity;
	}
	public void setMinQuantity(int minQuantity) {
		this.minQuantity = minQuantity;
	}
	public int getMaxQuantity() {
		return maxQuantity;
	}
	public void setMaxQuantity(int maxQuantity) {
		this.maxQuantity = maxQuantity;
	}
	public float getTakeTime() {
		return takeTime;
	}
	public void setTakeTime(float takeTime) {
		this.takeTime = takeTime;
	}
	public About getAboutAllTickets() {
		return aboutAllTickets;
	}

	public void setAboutAllTickets(About aboutAllTickets) {
		this.aboutAllTickets = aboutAllTickets;
	}

	public Map<String, About> getAboutPerSectionTickets() {
		return aboutPerSectionTickets;
	}

	public void setAboutPerSectionTickets(Map<String, About> aboutPerSectionTickets) {
		this.aboutPerSectionTickets = aboutPerSectionTickets;
	}

	public Map<Source, String> getSourceDirectLinkToPage() {
		return sourceDirectLinkToPage;
	}

	public void setSourceDirectLinkToPage(Map<Source, String> sourceDirectLinkToPage) {
		this.sourceDirectLinkToPage = sourceDirectLinkToPage;
	}
	
	
}
