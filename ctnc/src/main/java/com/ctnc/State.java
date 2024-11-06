package com.ctnc;

import com.ctnc.shared.Base;

public class State extends Base{

	private String abbreviation = null;

	public String getAbbreviation() {
		return abbreviation;
	}

	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}

	@Override
	public String toString() {
		return "State [abbreviation=" + abbreviation + ", toString()="
				+ super.toString() + "]";
	}
}
