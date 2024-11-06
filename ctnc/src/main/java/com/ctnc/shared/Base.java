package com.ctnc.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public abstract class Base implements IsSerializable{

	private long id = -1;	
	private String name;//can past modification for merge 
    private String originalName;//this value is original final value ... for now in use for venue name and events name
    //created  for easy map resolve for ticket utils input

	public long getId() {
		return id;
	}

	public String getOriginalName() {
		return originalName;
	}

	public void setOriginalName(String originalName) {
		this.originalName = originalName;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Base [id=" + id + ", name=" + name + ", originalValue=" + originalName + "]";
	}
}
