package com.emeter.cdci.data.message;

import com.emeter.message.base.IMessage;

public class CdciMessage implements IMessage {
	private Long orgId;
	
	private String topic;
	
	public String getKey(){
		return null;
	}
	public Long getOrgId() {
		return orgId;
	}
	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}
	
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	
}
