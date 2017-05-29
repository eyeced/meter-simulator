package com.emeter.cdci.data.message;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
@JsonTypeInfo(
	    use = JsonTypeInfo.Id.NAME,
	    include = JsonTypeInfo.As.PROPERTY,
	    property = "type") 
public class MeasDataPoint {

private Long measTypeId;

private List<DataPoint> dataPoints;


public Long getMeasTypeId() {
	return measTypeId;
}


public void setMeasTypeId(Long measTypeId) {
	this.measTypeId = measTypeId;
}

public List<DataPoint> getDataPoints() {
	return dataPoints;
}


public void setDataPoints(List<DataPoint> dataPoints) {
	this.dataPoints = dataPoints;
}

@Override
public String toString() {
	return "MeasDataPoint [measTypeId=" + measTypeId + ", dataPoints="
			+ dataPoints + "]";
}

}
