package com.ctnb;

public class BackendProperties {

	private long getTicketsTimeout;
	private long keepTicketsInCacheMs;
	private int  getTicketsRetryThreshold;
	private boolean enableMokeIp;
	private String mokeIpTarget;
	private String[] mokeIpSourceList;
	private String maintenanceToEmails;
	private String gmailPassword;
	private boolean sendEmailsEnable;

	public boolean isSendEmailsEnable() {
		return sendEmailsEnable;
	}
	public void setSendEmailsEnable(boolean sendEmailsEnable) {
		this.sendEmailsEnable = sendEmailsEnable;
	}
	public String getMaintenanceToEmails() {
		return maintenanceToEmails;
	}
	public void setMaintenanceToEmails(String maintenanceToEmails) {
		this.maintenanceToEmails = maintenanceToEmails;
	}
	public String getGmailPassword() {
		return gmailPassword;
	}
	public void setGmailPassword(String gmailPassword) {
		this.gmailPassword = gmailPassword;
	}
	public long getKeepTicketsInCacheMs() {
		return keepTicketsInCacheMs;
	}
	public void setKeepTicketsInCacheMs(long keepTicketsInCacheMs) {
		this.keepTicketsInCacheMs = keepTicketsInCacheMs;
	}
	public String getMokeIpTarget() {
		return mokeIpTarget;
	}
	public void setMokeIpTarget(String mokeIpTarget) {
		this.mokeIpTarget = mokeIpTarget;
	}
	public String[] getMokeIpSourceList() {
		return mokeIpSourceList;
	}
	public void setMokeIpSourceList(String[] mokeIpSourceList) {
		this.mokeIpSourceList = mokeIpSourceList;
	}
	public boolean isEnableMokeIp() {
		return enableMokeIp;
	}
	public void setEnableMokeIp(boolean enableMokeIp) {
		this.enableMokeIp = enableMokeIp;
	}
	
	public long getGetTicketsTimeout() {
		return getTicketsTimeout;
	}
	public void setGetTicketsTimeout(long getTicketsTimeout) {
		this.getTicketsTimeout = getTicketsTimeout;
	}
	public int getGetTicketsRetryThreshold() {
		return getTicketsRetryThreshold;
	}
	public void setGetTicketsRetryThreshold(int getTicketsRetryThreshold) {
		this.getTicketsRetryThreshold = getTicketsRetryThreshold;
	}


	
}
