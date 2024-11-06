package com.ctns.merge.index;

public class MergeIndexProperties {
	
	private Integer[] maxDistanceKmSteps;
	private String baseFolderPath = null;
	
	public String getBaseFolderPath() {
		return baseFolderPath;
	}
	public void setBaseFolderPath(String baseFolderPath) {
		this.baseFolderPath = baseFolderPath;
	}
	
	public Integer[] getMaxDistanceKmSteps() {
		return maxDistanceKmSteps;
	}
	public void setMaxDistanceKmSteps(Integer[] maxDistanceKmSteps) {
		this.maxDistanceKmSteps = maxDistanceKmSteps;
	}	
}
