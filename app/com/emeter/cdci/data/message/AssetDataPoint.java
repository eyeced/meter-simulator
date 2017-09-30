package com.emeter.cdci.data.message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type")
public class AssetDataPoint extends CdciMessage {

    private Long assetId;

    private Long gridId;

    private String assetType;

    private List<MeasDataPoint> reads;

    private Long rtuId;
    private Long paId;

    @Override
    public String toString() {
        return "AssetDataPoint [assetId=" + assetId + ", assetType=" + assetType
            + ", reads=" + reads + ", rtuId=" + rtuId + ", paId=" + paId + ", gridId=" + gridId + "]";
    }

    @JsonIgnore
    public String getKey()
    {
        return String.valueOf(assetId);
    }

    public Long getAssetId()
    {
        return assetId;
    }

    public void setAssetId(Long assetId)
    {
        this.assetId = assetId;
    }

    public String getAssetType()
    {
        return assetType;
    }

    public void setAssetType(String assetType)
    {
        this.assetType = assetType;
    }

    public List<MeasDataPoint> getReads()
    {
        return reads;
    }

    public void setReads(List<MeasDataPoint> lmdp)
    {
        this.reads = lmdp;
    }

    public Long getRtuId()
    {
        return rtuId;
    }

    public void setRtuId(Long rtuId)
    {
        this.rtuId = rtuId;
    }

    public Long getPaId()
    {
        return paId;
    }

    public Long getGridId() {
        return gridId;
    }

    public void setGridId(Long gridId) {
        this.gridId = gridId;
    }

    public void setPaId(Long paId)
    {
        this.paId = paId;
    }

}
