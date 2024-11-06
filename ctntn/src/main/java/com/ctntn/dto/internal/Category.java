package com.ctntn.dto.internal;

public class Category {

	private String childCategoryDescription = null;
	private long childCategoryID;
	private String grandchildCategoryDescription = null;
	private long grandchildCategoryID;
	private String parentCategoryDescription = null;
	private long parentCategoryID;
	
	public String getChildCategoryDescription() {
		return childCategoryDescription;
	}
	public void setChildCategoryDescription(String childCategoryDescription) {
		this.childCategoryDescription = childCategoryDescription;
	}
	public long getChildCategoryID() {
		return childCategoryID;
	}
	public void setChildCategoryID(long childCategoryID) {
		this.childCategoryID = childCategoryID;
	}
	public String getGrandchildCategoryDescription() {
		return grandchildCategoryDescription;
	}
	public void setGrandchildCategoryDescription(
			String grandchildCategoryDescription) {
		this.grandchildCategoryDescription = grandchildCategoryDescription;
	}
	public long getGrandchildCategoryID() {
		return grandchildCategoryID;
	}
	public void setGrandchildCategoryID(long grandchildCategoryID) {
		this.grandchildCategoryID = grandchildCategoryID;
	}
	public String getParentCategoryDescription() {
		return parentCategoryDescription;
	}
	public void setParentCategoryDescription(String parentCategoryDescription) {
		this.parentCategoryDescription = parentCategoryDescription;
	}
	public long getParentCategoryID() {
		return parentCategoryID;
	}
	public void setParentCategoryID(long parentCategoryID) {
		this.parentCategoryID = parentCategoryID;
	}
	
	
	
}
