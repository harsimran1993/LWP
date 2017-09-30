package com.mygdx.purefaithstudio.android;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.google.android.gms.internal.zzagz.runOnUiThread;

/**
 * Created by harsimran singh on 19-09-2017.
 */

public class SaveManager {

    public static synchronized void saveToFileSystem(Context context, Object object, String fileName) {
        try {
            File dataDir = null;
            File root = context.getExternalFilesDir("");
            dataDir = new File(root,"LWPData");
            if(!dataDir.isDirectory())
                dataDir.mkdir();
            String tempPath = root + "/LWPData/" + fileName + ".bin";
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
        File root = context.getExternalFilesDir("");
        try {
            String tempPath = root + "/LWPData/" + binFileName + ".bin";
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

    public static synchronized void requestImage(Context context, String url, String directory, String filename){

        //make directory structure
        File dataDir = null;
        File root = context.getExternalFilesDir("");
        dataDir = new File(root,"LWPData");
        if(!dataDir.isDirectory())
            dataDir.mkdir();
        dataDir = new File(root,"LWPData/"+ directory);
        if(!dataDir.isDirectory())
            dataDir.mkdir();
        String tempPath = root + "/LWPData/"+ directory + "/" + filename;
        Log.i("harsim",tempPath);
        final File file = new File(tempPath);

        //request file from server
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                try {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                    FileOutputStream ostream = new FileOutputStream(file, false);
                    ostream.write(response.body().bytes());
                    ostream.flush();
                    ostream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }
}
