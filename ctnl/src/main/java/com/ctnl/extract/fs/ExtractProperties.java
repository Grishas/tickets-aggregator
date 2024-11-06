package com.ctnl.extract.fs;

public class ExtractProperties {

	private String baseFolderPath = null;
	private boolean isPrettyPrinting = false;
	private boolean extractOnlyMergedEvents = false;
	
	
	public boolean isExtractOnlyMergedEvents() {
		return extractOnlyMergedEvents;
	}

	public void setExtractOnlyMergedEvents(boolean extractOnlyMergedEvents) {
		this.extractOnlyMergedEvents = extractOnlyMergedEvents;
	}

	public boolean isPrettyPrinting() {
		return isPrettyPrinting;
	}
	
	public void setPrettyPrinting(boolean isPrettyPrinting) {
		this.isPrettyPrinting = isPrettyPrinting;
	}
	
	public String getBaseFolderPath() {
		return baseFolderPath;
	}
	
	public void setBaseFolderPath(String baseFolderPath) {
		this.baseFolderPath = baseFolderPath;
	}
}
