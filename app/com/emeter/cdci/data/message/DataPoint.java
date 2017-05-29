package com.emeter.cdci.data.message;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;


@XmlRootElement
@JsonTypeInfo(
	    use = JsonTypeInfo.Id.NAME,
	    include = JsonTypeInfo.As.PROPERTY,
	    property = "type") 
public class DataPoint {


	private Double value;

	private Date readTime;

	private Long flag;
	
	
	@Override
	public String toString() {
		return "DataPoint [value=" + value + ", readTime=" + ((readTime==null)?null: readTime) +
				", flag=" + flag + "]";
	}


	public Double getValue() {
		return value;
	}


	public void setValue(Double value) {
		this.value = value;
	}


	public Date getReadTime() {
		return readTime;
	}


	public void setReadTime(Date readTime) {
		this.readTime = readTime;
	}


	public Long getFlag() {
		return flag;
	}


	public void setFlag(Long flag) {
		this.flag = flag;
	}
	
}
