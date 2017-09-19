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
    private ArrayList<Layer> layers = new ArrayList<Layer>();
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
    public WallpaperLayer(ArrayList<Layer> layers) {
        super();
        this.layers = layers;
    }

    public ArrayList<Layer> getLayers() {
        return layers;
    }

    public void setLayers(ArrayList<Layer> layers) {
        this.layers = layers;
    }

}