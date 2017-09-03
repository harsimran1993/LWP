package com.mygdx.purefaithstudio.android;
import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Gallery implements Serializable
{

    @SerializedName("ID")
    @Expose
    private String iD;
    @SerializedName("Title")
    @Expose
    private String title;
    @SerializedName("ThumbUrl")
    @Expose
    private String thumbUrl;
    @SerializedName("IsNew")
    @Expose
    private String isNew;
    @SerializedName("AddedBy")
    @Expose
    private String addedBy;
    @SerializedName("DirectoryName")
    @Expose
    private String directoryName;
    @SerializedName("Downloads")
    @Expose
    private String downloads;
    private final static long serialVersionUID = -8636418817250974498L;

    /**
     * No args constructor for use in serialization
     *
     */
    public Gallery() {
    }

    /**
     *
     * @param title
     * @param directoryName
     * @param isNew
     * @param thumbUrl
     * @param downloads
     * @param iD
     * @param addedBy
     */
    public Gallery(String iD, String title, String thumbUrl, String isNew, String addedBy, String directoryName, String downloads) {
        super();
        this.iD = iD;
        this.title = title;
        this.thumbUrl = thumbUrl;
        this.isNew = isNew;
        this.addedBy = addedBy;
        this.directoryName = directoryName;
        this.downloads = downloads;
    }

    public String getID() {
        return iD;
    }

    public void setID(String iD) {
        this.iD = iD;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public String getIsNew() {
        return isNew;
    }

    public void setIsNew(String isNew) {
        this.isNew = isNew;
    }

    public String getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(String addedBy) {
        this.addedBy = addedBy;
    }

    public String getDirectoryName() {
        return directoryName;
    }

    public void setDirectoryName(String directoryName) {
        this.directoryName = directoryName;
    }

    public String getDownloads() {
        return downloads;
    }

    public void setDownloads(String downloads) {
        this.downloads = downloads;
    }

}
