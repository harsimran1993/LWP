package com.mygdx.purefaithstudio.android;

import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.mygdx.purefaithstudio.Config;
import com.mygdx.purefaithstudio.WallpaperConfigData;
import com.mygdx.purefaithstudio.android.parallaxLayout.ParallaxLayerLayout;
import com.mygdx.purefaithstudio.android.parallaxLayout.SensorTranslationUpdater;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class PreviewActivity extends AppCompatActivity implements FetchLayer.FetchLayerListener,SaveManager.DownloadListner {
    private SensorTranslationUpdater sensorTranslationUpdater;
    private ParallaxLayerLayout parallaxLayout;
    private Gallery wallpaperData;
    private ArrayList<Layer> layers;
    private SwipeRefreshLayout msSwipeRefreshLayout;
    private ImageView[] imgv = new ImageView[5];
    private FetchLayer fetchLayer;
    private Button download,preview;
    private WallpaperLayer wallpaperLayer;
    private String lastWallp="0";
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private int step,progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //load views
        setContentView(R.layout.activity_preview);
        parallaxLayout = (ParallaxLayerLayout) findViewById(R.id.parallax);
        download = (Button) findViewById(R.id.PAdownload);
        preview = (Button) findViewById(R.id.PApreview);
        imgv[0] = (ImageView) findViewById(R.id.img0);
        imgv[1] = (ImageView) findViewById(R.id.img1);
        imgv[2] = (ImageView) findViewById(R.id.img2);
        imgv[3] = (ImageView) findViewById(R.id.img3);
        imgv[4] = (ImageView) findViewById(R.id.img4);

        //load shared-preferences
        prefs = getSharedPreferences("preferences", Context.MODE_PRIVATE);
        editor = prefs.edit();

        //receive wallpaper data from main gallery
        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        wallpaperData = (Gallery) b.get("wallpaper");
        final String id = wallpaperData.getID();
        Log.i("wallpaperdata",WallpaperDataManager.downloadsGallery.toString());
        Log.i("harsim",""+getFilesDir());
        Log.i("harsim",""+getExternalFilesDir(""));

        //for download listener
        SaveManager.downloadListner = this;

        //check if already downlaoded
        if(WallpaperDataManager.downloadsGallery.containsKey(wallpaperData.getID()))
        {
            download.setVisibility(View.GONE);
            preview.setVisibility(View.VISIBLE);
        }

        //swipre refresh parallaxView
        msSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.layerSwipeRefreshLayout);
        msSwipeRefreshLayout.setRefreshing(true);
        if(isNetworkConnected()){
            //new DownloadImage().execute();
            fetchLayer = new FetchLayer();
            fetchLayer.setid(id);
            fetchLayer.setFetchLayerListener(this);
            fetchLayer.execute();
        }
        else{
            Toast.makeText(PreviewActivity.this,"Please check your internet connect and retry",Toast.LENGTH_SHORT).show();
            loadLayerOffline();
            //msSwipeRefreshLayout.setRefreshing(false);
        }
        msSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(isNetworkConnected()){
                    //new DownloadImage().execute();
                    fetchLayer = new FetchLayer();
                    fetchLayer.setid(id);
                    fetchLayer.setFetchLayerListener(PreviewActivity.this);
                    fetchLayer.execute();
                }
                else{
                    loadLayerOffline();
                    /*if(msSwipeRefreshLayout.isRefreshing())
                        msSwipeRefreshLayout.setRefreshing(false);*/
                    Toast.makeText(PreviewActivity.this,"Please check your internet connect and retry",Toast.LENGTH_SHORT).show();
                }
            }
        });

        //parallax view system
        sensorTranslationUpdater = new SensorTranslationUpdater(this);
        parallaxLayout.setTranslationUpdater(sensorTranslationUpdater);

        //downlaod button action
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    if(!WallpaperDataManager.downloadsGallery.containsKey(wallpaperData.getID())) {
                        layers = wallpaperLayer.getLayers();
                        step = 100/layers.size() + 1;
                        progress = 0;
                        for(Layer layer : layers)
                        {
                            SaveManager.requestImage(getApplicationContext(),layer.getLayerUrl(),wallpaperData.getDirectoryName(),layer.getLayerName()+layer.getLayerType());
                        }
                    }
                    download.setVisibility(View.GONE);
                    //preview.setVisibility(View.VISIBLE);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        //preview button action
        preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //save in shared preferecnes
                layers = wallpaperLayer.getLayers();
                String[] layerNames = new String[layers.size()];
                int i=0;
                for(Layer layer : layers)
                {
                    layerNames[i] = layer.getLayerName()+layer.getLayerType();
                    i++;
                }
                WallpaperConfigData wcd = new WallpaperConfigData();
                wcd.setDirectorName(wallpaperData.getDirectoryName());
                wcd.setSize(layers.size());
                wcd.setLayers(layerNames);
                Gson gson = new Gson();
                Log.i("harsim",""+gson.toJson(wcd));
                editor.putString("wcd",""+gson.toJson(wcd));
                editor.commit();

                //call wallpaer intent
                choosewall(Integer.parseInt(wallpaperData.getID()));
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorTranslationUpdater.unregisterSensorManager();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorTranslationUpdater.registerSensorManager();
    }

    public void loadLayerOffline(){
        try {
            wallpaperLayer = (WallpaperLayer) SaveManager.readFromFileSystem(getApplicationContext(), wallpaperData.getDirectoryName() + "/" + wallpaperData.getTitle());
            if (wallpaperLayer != null) {
                layers = wallpaperLayer.getLayers();
                if (layers != null) {
                    int i = 0;
                    for (Layer layer : layers) {
                        //Log.i("harsim",layer.getLayerUrl());
                        if (i == (layers.size() - 1)) i = 4;
                        Glide.with(PreviewActivity.this).load(getFilesDir()+"/LWPData/"+wallpaperData.getDirectoryName()+"/"+layer.getLayerName()+layer.getLayerType())
                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                .placeholder(R.drawable.download)
                                .error(R.drawable.error)
                                .into(imgv[i]);
                        i++;
                    }
                }
            }
        }
        catch (Exception e){
            //Log.i("harsim", "server unreachable!!");
            e.printStackTrace();
            Toast.makeText(PreviewActivity.this,"Server not reachable plz try again in a while!!",Toast.LENGTH_SHORT).show();
        }

        if(msSwipeRefreshLayout.isRefreshing())
            msSwipeRefreshLayout.setRefreshing(false);
    }
    @Override
    public void onPostExecute(String s) {
        Gson gson = new Gson();
        //Log.i("harsim",s);
        try {
            if (!s.equals("") && s!=null) {

                wallpaperLayer = gson.fromJson(s,WallpaperLayer.class);
                if(wallpaperLayer !=null){
                    layers = wallpaperLayer.getLayers();
                    if(layers !=null){
                        int i = 0;
                        for(Layer layer : layers){
                            //Log.i("harsim",layer.getLayerUrl());
                            if(i == (layers.size() - 1)) i = 4;
                            Glide.with(PreviewActivity.this).load(layer.getLayerUrl())
                                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                    .placeholder(R.drawable.download)
                                    .error(R.drawable.error)
                                    .into(imgv[i]);
                            i++;
                        }
                    }
                }
            }
        }
        catch (Exception e){
            //Log.i("harsim", "server unreachable!!");
            e.printStackTrace();
            Toast.makeText(PreviewActivity.this,"Server not reachable plz try again in a while!!",Toast.LENGTH_SHORT).show();
        }

        if(msSwipeRefreshLayout.isRefreshing())
            msSwipeRefreshLayout.setRefreshing(false);
    }


    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    public void choosewall(int position){
        if(position * 10 < (Config.points + Config.fps)){
            lastWallp = Config.listTest;
            Config.listTest = ""+position;
            editor.putString("listTest",""+position);
            editor.commit();
            try {
                Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
                if(Config.lockScreen)
                    intent.putExtra("SET_LOCKSCREEN_WALLPAPER", true);
                intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, new ComponentName(getApplicationContext(), LWP_Android.class));
                startActivity(intent);
            } catch (Exception e) {
                try {
                    Log.e("harsim", "moved to chooser");
                    Intent intent2 = new Intent();
                    Toast makeText = Toast.makeText(PreviewActivity.this, "Choose Live wallpaper-3d parallax...\n in the list to start the Live Wallpaper.", Toast.LENGTH_SHORT);
                    intent2.setAction(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER);
                    startActivity(intent2);
                    makeText.show();
                } catch (Exception e2) {
                    Toast.makeText(PreviewActivity.this, "Please go to your system settings or long press on your homescreen to set Live Wallpaper", Toast.LENGTH_SHORT).show();
                }
            }
        }
        else{
            AlertDialog.Builder builder = new AlertDialog.Builder(PreviewActivity.this);
            builder.setTitle("Insufficient Points!!");
            builder.setMessage("You have "+Config.points+" points, You need "+(position*10 - (Config.points+Config.fps -10))+" points.\n\n" +
                    "You can get more points using earn button above.");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    dialog.cancel();
                }
            });
            builder.show();
        }
    }

    @Override
    public void update() {
        progress += step;
        Log.i("harsim",""+progress);
        if(progress >=100){
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    preview.setVisibility(View.VISIBLE);
                }
            });
            WallpaperDataManager.downloadsGallery.put(wallpaperData.getID(), wallpaperData);
            WallpaperDataManager.saveDown(getApplicationContext());
            SaveManager.saveToFileSystem(getApplicationContext(),wallpaperLayer,wallpaperData.getDirectoryName()+"/"+wallpaperData.getTitle());
        }
    }

    @Override
    public void failure(String errorMessage) {
        Toast.makeText(getApplicationContext(),"DownloadFailure",Toast.LENGTH_SHORT).show();
    }
}
