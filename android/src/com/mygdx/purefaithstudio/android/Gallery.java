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
    @SerializedName("LikeCount")
    @Expose
    private String likeCount;
    @SerializedName("IsLiked")
    @Expose
    private Boolean isLiked;
    @SerializedName("AddedBy")
    @Expose
    private String addedBy;
    @SerializedName("DirectoryName")
    @Expose
    private String directoryName;
    @SerializedName("DownloadCount")
    @Expose
    private String downloadCount;
    @SerializedName("AddedOn")
    @Expose
    private String addedOn;
    private final static long serialVersionUID = 5541266050859493368L;

    /**
     * No args constructor for use in serialization
     *
     */
    public Gallery() {
    }

    /**
     *
     * @param title
     * @param likeCount
     * @param directoryName
     * @param downloadCount
     * @param thumbUrl
     * @param addedOn
     * @param iD
     * @param addedBy
     */
    public Gallery(String iD, String title, String thumbUrl, String likeCount, String addedBy, String directoryName, String downloadCount, String addedOn) {
        super();
        this.iD = iD;
        this.title = title;
        this.thumbUrl = thumbUrl;
        this.likeCount = likeCount;
        this.addedBy = addedBy;
        this.directoryName = directoryName;
        this.downloadCount = downloadCount;
        this.addedOn = addedOn;
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

    public String getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(String likeCount) {
        this.likeCount = likeCount;
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

    public String getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(String downloadCount) {
        this.downloadCount = downloadCount;
    }

    public String getAddedOn() {
        return addedOn;
    }

    public void setAddedOn(String addedOn) {
        this.addedOn = addedOn;
    }

    public Boolean getIsLiked() {
        return isLiked;
    }

    public void setAddedOn(Boolean isLiked) {
        this.isLiked = isLiked;
    }



    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Gallery))
            return false;
        Gallery that = (Gallery) obj;
        return this.getID() == that.getID();
    }

    /*@Override
    public int hashCode() {
        int hash = 3;
        hash = 7 * hash + this.getID().hashCode();
        hash = 7 * hash + this.getDirectoryName().hashCode();
        return hash;
    }*/
}
