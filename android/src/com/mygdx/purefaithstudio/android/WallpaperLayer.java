package com.mygdx.purefaithstudio.android;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WallpaperLayer implements Serializable
{

    @SerializedName("Layers")
    @Expose
    private List<Layer> layers = new ArrayList<Layer>();
    private final static long serialVersionUID = -3629294240545618357L;

    /**
     * No args constructor for use in serialization
     *
     */
    public WallpaperLayer() {
    }

    /**
     *
     * @param layers
     */
    public WallpaperLayer(List<Layer> layers) {
        super();
        this.layers = layers;
    }

    public List<Layer> getLayers() {
        return layers;
    }

    public void setLayers(List<Layer> layers) {
        this.layers = layers;
    }

}