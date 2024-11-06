package com.ctnc.shared;

import java.util.Date;

public class SearchRequest extends Search{

	private int rangeStart;
	private int rangeLength;
	
	//used only in server side for search func
	private Date from_ = null;
	private Date to_ = null;
	
	public Date getFrom_() {
		return from_;
	}
	public void setFrom_(Date from_) {
		this.from_ = from_;
	}
	public Date getTo_() {
		return to_;
	}
	public void setTo_(Date to_) {
		this.to_ = to_;
	}
	public int getRangeStart() {
		return rangeStart;
	}
	public void setRangeStart(int rangeStart) {
		this.rangeStart = rangeStart;
	}
	public int getRangeLength() {
		return rangeLength;
	}
	public void setRangeLength(int rangeLength) {
		this.rangeLength = rangeLength;
	}
	
	@Override
	public String toString() {
		return "SearchRequest [rangeStart=" + rangeStart + ", rangeLength=" + rangeLength + "]"+super.toString();
	}

}
