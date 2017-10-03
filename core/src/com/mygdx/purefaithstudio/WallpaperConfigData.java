package com.mygdx.purefaithstudio;

/**
 * Created by harsimran singh on 03-10-2017.
 */

public class WallpaperConfigData {

    private int size;
    private String[] layers;
    private String directorName;

    public WallpaperConfigData(){

    }
    public WallpaperConfigData(int size,String[] layers, String directoryName){
        this.size = size;
        this.layers = layers;
        this.directorName = directoryName;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String[] getLayers() {
        return layers;
    }

    public void setLayers(String[] layers) {
        this.layers = layers;
    }

    public String getDirectorName() {
        return directorName;
    }

    public void setDirectorName(String directorName) {
        this.directorName = directorName;
    }
}
