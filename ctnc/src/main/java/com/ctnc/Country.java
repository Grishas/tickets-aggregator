package com.ctnc;

import com.ctnc.shared.Base;

public class Country extends Base{

	private String abbreviation = null;

	public String getAbbreviation() {
		return abbreviation;
	}

	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}

	@Override
	public String toString() {
		return "Country [abbreviation=" + abbreviation + ", toString()="
				+ super.toString() + "]";
	}

}
