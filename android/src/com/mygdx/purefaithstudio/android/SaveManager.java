package com.mygdx.purefaithstudio.android;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by harsimran singh on 19-09-2017.
 */

public class SaveManager {

    public static synchronized void saveToFileSystem(Context context, Object object, String fileName) {
        try {
            File dataDir = null;
            dataDir = new File(context.getFilesDir(),"LWPData");
            if(!dataDir.isDirectory())
                dataDir.mkdir();
            String tempPath = context.getFilesDir() + "/LWPData/" + fileName + ".bin";
            File file = new File(tempPath);
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(object);
            oos.flush();
            oos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static synchronized Object readFromFileSystem(Context context, String binFileName) {
        Object obj = new Object();
        try {
            String tempPath = context.getFilesDir() + "/LWPData/" + binFileName + ".bin";
            File file = new File(tempPath);
            if (file.exists()) {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
                obj = ois.readObject();
                ois.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return obj;
    }
}
