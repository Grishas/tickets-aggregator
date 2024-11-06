package com.ctnu.http;

public class CtnHttpRequest {

	// for example : "http://tnwebservices-test.ticketnetwork.com/tnwebservice/v3.2/tnwebservice.asmx";
	private String uri = null;
	
	//for example : http://tnwebservices.ticketnetwork.com/tnwebservice/v3.2/GetEvents 
	private String soapAction = null;
	
//	for example : <soap:Envelope xmlns:soap="http://www.w3.org/2003/05/soap-envelope" xmlns:v3="http://tnwebservices.ticketnetwork.com/tnwebservice/v3.2">
//	   <soap:Header/>
//	   <soap:Body>
//	      <v3:GetEvents>
//	         <v3:websiteConfigID>17395</v3:websiteConfigID>
//	         <v3:stateProvDesc>MN</v3:stateProvDesc>
//	         <v3:stateID>24</v3:stateID>
//	      </v3:GetEvents>
//	   </soap:Body>
//	</soap:Envelope>
	//comming from tickets module : tc , tn etc...
	private String soapBody = null;
	
	//default.ovveride if need
	private String soapHeader = "application/soap+xml";

	private String charset = "UTF-8";
	
	private boolean printResponseBodyAsString = false;
	
	public boolean isPrintResponseBodyAsString() {
		return printResponseBodyAsString;
	}

	public void setPrintResponseBodyAsString(boolean printResponseBodyAsString) {
		this.printResponseBodyAsString = printResponseBodyAsString;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getSoapAction() {
		return soapAction;
	}

	public void setSoapAction(String soapAction) {
		this.soapAction = soapAction;
	}

	public String getSoapBody() {
		return soapBody;
	}

	public void setSoapBody(String soapBody) {
		this.soapBody = soapBody;
	}

	public String getSoapHeader() {
		return soapHeader;
	}

	public void setSoapHeader(String soapHeader) {
		this.soapHeader = soapHeader;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	@Override
	public String toString() {
		return "HttpRequest [uri=" + uri + ", soapAction=" + soapAction + ", soapBody=" + soapBody + ", soapHeader="
				+ soapHeader + ", charset=" + charset + ", printResponseBodyAsString=" + printResponseBodyAsString
				+ "]";
	}
}
