package com.emeter.a2f.kafka.message;

import com.emeter.message.base.IMessage;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = As.PROPERTY, property = "type")
public class A2FKafkaMessage implements IMessage {

	public A2FKafkaMessage(Long orgId, IMessage message) {
		this.orgId = orgId;
		this.message = message;
	}

	private Long orgId;
	

	public Long getOrgId() {
		return orgId;
	}

	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}
    

	private IMessage message;

	public IMessage getMessage() {
		return message;
	}

	public void setMessage(IMessage message) {
		this.message = message;
	}
	
	@Override
	public String toString(){
		
		return "a2fKafkaMessage:" + "[orgId="+orgId+", message=["+ (message==null?message:message.toString())+"]]";
	}
}
