package com.mygdx.purefaithstudio.android;

import java.util.ArrayList;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GalleryResolver {

    @SerializedName("Gallery")
    @Expose
    private ArrayList<Gallery> gallery = null;

    /**
     * No args constructor for use in serialization
     *
     */
    public GalleryResolver() {
    }

    /**
     *
     * @param gallery
     */
    public GalleryResolver(ArrayList<Gallery> gallery) {
        super();
        this.gallery = gallery;
    }

    public ArrayList<Gallery> getGallery() {
        return gallery;
    }

    public void setGallery(ArrayList<Gallery> gallery) {
        this.gallery = gallery;
    }

    public GalleryResolver withGallery(ArrayList<Gallery> gallery) {
        this.gallery = gallery;
        return this;
    }

}
