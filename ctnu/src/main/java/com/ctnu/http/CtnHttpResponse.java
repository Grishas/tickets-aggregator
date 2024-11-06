package com.ctnu.http;

public class CtnHttpResponse
{
	private byte[] data = null;
	private String statusLine = null;
	private int statusCode = -1;
	private CtnHttpRequest httpRequest = null;
	private long takeTime;
	
	public long getTakeTime() {
		return takeTime;
	}
	public void setTakeTime(long takeTime) {
		this.takeTime = takeTime;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getStatusLine() {
		return statusLine;
	}

	public void setStatusLine(String statusLine) {
		this.statusLine = statusLine;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public CtnHttpRequest getHttpRequest() {
		return httpRequest;
	}

	public void setHttpRequest(CtnHttpRequest httpRequest) {
		this.httpRequest = httpRequest;
	}
	@Override
	public String toString() {
		return "HttpResponse [statusLine=" + statusLine + ", statusCode="
				+ statusCode + ", httpRequest=" + httpRequest + ", takeTime=" + takeTime + "]";
	}
}