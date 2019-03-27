package com.common;

import java.util.Date;

public class SpiderStatus {
	
	private short spiderId;

	private Date sendTime;
	
	private Date nextRunTime;

	private String runBy;

	private Short statusId;

	private Short errorLevelId;

	private String hostIp;

	private Short userId;

	private String describing;

	public String getDescribing() {
		return describing;
	}

	public void setDescribing(String describing) {
		this.describing = describing;
	}

	public Short getErrorLevelId() {
		return errorLevelId;
	}

	public void setErrorLevelId(Short errorLevelId) {
		this.errorLevelId = errorLevelId;
	}

	public String getHostIp() {
		return hostIp;
	}

	public void setHostIp(String hostIp) {
		this.hostIp = hostIp;
	}

	public Date getNextRunTime() {
		return nextRunTime;
	}

	public void setNextRunTime(Date nextRunTime) {
		this.nextRunTime = nextRunTime;
	}

	public String getRunBy() {
		return runBy;
	}

	public void setRunBy(String runBy) {
		this.runBy = runBy;
	}

	public Date getSendTime() {
		return sendTime;
	}

	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}

	public short getSpiderId() {
		return spiderId;
	}

	public void setSpiderId(short spiderId) {
		this.spiderId = spiderId;
	}

	public Short getStatusId() {
		return statusId;
	}

	public void setStatusId(Short statusId) {
		this.statusId = statusId;
	}

	public Short getUserId() {
		return userId;
	}

	public void setUserId(Short userId) {
		this.userId = userId;
	}	

}
