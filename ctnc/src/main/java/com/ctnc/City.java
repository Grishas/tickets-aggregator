package com.ctnc;
import java.util.HashSet;
import java.util.Set;
import com.google.gwt.user.client.rpc.IsSerializable;

public class City implements IsSerializable{

	private Set<String> cityNames = new HashSet<String>(1);

	public Set<String> getCityNames() {
		return cityNames;
	}

	public void setCityNames(Set<String> cityNames) {
		this.cityNames = cityNames;
	}

	@Override
	public String toString() {
		return "City [cityNames=" + cityNames + "]";
	}
}
