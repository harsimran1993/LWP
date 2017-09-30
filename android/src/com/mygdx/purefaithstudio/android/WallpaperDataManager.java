package com.mygdx.purefaithstudio.android;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by harsimran singh on 19-09-2017.
 */

public class WallpaperDataManager {
    public static HashMap<String,Gallery> downloadsGallery = new HashMap<>();
    public static HashMap<String,WallpaperLayer> galleryLayers = new HashMap<>();

    //when fetching downlaoded list
    public static void loadDown(Context context){
        try {
            downloadsGallery = (HashMap<String, Gallery>) SaveManager.readFromFileSystem(context, "downsave");
            galleryLayers = (HashMap<String, WallpaperLayer>) SaveManager.readFromFileSystem(context,"gallerLayers");
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void saveDown(Context context){
        try {
            SaveManager.saveToFileSystem(context, downloadsGallery, "downsave");
            SaveManager.saveToFileSystem(context, galleryLayers, "gallerLayers");
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
