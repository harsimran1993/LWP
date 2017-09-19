package com.mygdx.purefaithstudio.android;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.mygdx.purefaithstudio.android.parallaxLayout.ParallaxLayerLayout;
import com.mygdx.purefaithstudio.android.parallaxLayout.SensorTranslationUpdater;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class PreviewActivity extends AppCompatActivity implements FetchLayer.FetchLayerListener {
    private SensorTranslationUpdater sensorTranslationUpdater;
    private ParallaxLayerLayout parallaxLayout;
    private Gallery wallpaperData;
    private ArrayList<Layer> layers;
    private SwipeRefreshLayout msSwipeRefreshLayout;
    private ImageView[] imgv = new ImageView[5];
    private FetchLayer fetchLayer;
    private Button download,preview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        parallaxLayout = (ParallaxLayerLayout) findViewById(R.id.parallax);
        download = (Button) findViewById(R.id.PAdownload);
        preview = (Button) findViewById(R.id.PApreview);
        imgv[0] = (ImageView) findViewById(R.id.img0);
        imgv[1] = (ImageView) findViewById(R.id.img1);
        imgv[2] = (ImageView) findViewById(R.id.img2);
        imgv[3] = (ImageView) findViewById(R.id.img3);
        imgv[4] = (ImageView) findViewById(R.id.img4);
        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        wallpaperData = (Gallery) b.get("wallpaper");
        final String id = wallpaperData.getID();
        Log.i("wallpaperdata",WallpaperDataManager.downloadsGallery.toString());
        if(WallpaperDataManager.downloadsGallery.containsKey(wallpaperData.getID()))
        {
                download.setVisibility(View.GONE);
                preview.setVisibility(View.VISIBLE);
        }

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
            msSwipeRefreshLayout.setRefreshing(false);
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
                    if(msSwipeRefreshLayout.isRefreshing())
                        msSwipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(PreviewActivity.this,"Please check your internet connect and retry",Toast.LENGTH_SHORT).show();
                }
            }
        });
        sensorTranslationUpdater = new SensorTranslationUpdater(this);
        parallaxLayout.setTranslationUpdater(sensorTranslationUpdater);


        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if(!WallpaperDataManager.downloadsGallery.containsKey(wallpaperData.getID()))
                        WallpaperDataManager.downloadsGallery.put(wallpaperData.getID(),wallpaperData);
                    WallpaperDataManager.saveDown(getApplicationContext());
                    download.setVisibility(View.GONE);
                    preview.setVisibility(View.VISIBLE);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

    @Override
    public void onPostExecute(String s) {
        Gson gson = new Gson();
        //Log.i("harsim",s);
        try {
            if (!s.equals("") && s!=null) {

                WallpaperLayer wallpaperLayer = gson.fromJson(s,WallpaperLayer.class);
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
            Toast.makeText(PreviewActivity.this,"Server not reachable plz try again in a while!!",Toast.LENGTH_SHORT).show();
        }

        if(msSwipeRefreshLayout.isRefreshing())
            msSwipeRefreshLayout.setRefreshing(false);
    }


    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }
}
