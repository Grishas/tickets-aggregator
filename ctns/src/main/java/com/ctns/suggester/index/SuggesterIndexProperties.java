package com.ctns.suggester.index;

public class SuggesterIndexProperties {

	private int suggesterMaxNumberOfItemsToReturn;
	private String baseFolderPath = null;
	
	public String getBaseFolderPath() {
		return baseFolderPath;
	}
	
	public void setBaseFolderPath(String baseFolderPath) {
		this.baseFolderPath = baseFolderPath;
	}
	
	public int getSuggesterMaxNumberOfItemsToReturn() {
		return suggesterMaxNumberOfItemsToReturn;
	}
	
	public void setSuggesterMaxNumberOfItemsToReturn(
		int suggesterMaxNumberOfItemsToReturn) {
		this.suggesterMaxNumberOfItemsToReturn = suggesterMaxNumberOfItemsToReturn;
	}
}
