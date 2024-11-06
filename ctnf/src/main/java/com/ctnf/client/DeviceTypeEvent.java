package com.ctnf.client;

import com.google.gwt.event.shared.GwtEvent;

public class DeviceTypeEvent extends GwtEvent<DeviceTypeEventHandler> {

	public static Type<DeviceTypeEventHandler> TYPE = new Type<DeviceTypeEventHandler>();
	
	private  DeviceType type;	
	private int width = 0;
	private int height = 0;

	public DeviceTypeEvent(DeviceType type, int width, int height) {
		super();
		this.type = type;
		this.width = width;
		this.height = height;
	}

	public DeviceTypeEvent(DeviceType type){
		this.type = type;
	}
	
	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public DeviceType getType() {
		return type;
	}
	public void setType(DeviceType type) {
		this.type = type;
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<DeviceTypeEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(DeviceTypeEventHandler handler) {
		handler.setDeviceTypeEvent(this);
	}

}
