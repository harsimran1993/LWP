package com.mygdx.purefaithstudio.android;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Layer implements Serializable
{

    @SerializedName("LayerName")
    @Expose
    private String layerName;
    @SerializedName("LayerType")
    @Expose
    private String layerType;
    @SerializedName("LayerOrder")
    @Expose
    private String layerOrder;
    @SerializedName("LayerUrl")
    @Expose
    private String layerUrl;
    private final static long serialVersionUID = 5349670775590504740L;

    /**
     * No args constructor for use in serialization
     *
     */
    public Layer() {
    }

    /**
     *
     * @param layerType
     * @param layerName
     * @param layerUrl
     * @param layerOrder
     */
    public Layer(String layerName, String layerType, String layerOrder, String layerUrl) {
        super();
        this.layerName = layerName;
        this.layerType = layerType;
        this.layerOrder = layerOrder;
        this.layerUrl = layerUrl;
    }

    public String getLayerName() {
        return layerName;
    }

    public void setLayerName(String layerName) {
        this.layerName = layerName;
    }

    public String getLayerType() {
        return layerType;
    }

    public void setLayerType(String layerType) {
        this.layerType = layerType;
    }

    public String getLayerOrder() {
        return layerOrder;
    }

    public void setLayerOrder(String layerOrder) {
        this.layerOrder = layerOrder;
    }

    public String getLayerUrl() {
        return layerUrl;
    }

    public void setLayerUrl(String layerUrl) {
        this.layerUrl = layerUrl;
    }

}