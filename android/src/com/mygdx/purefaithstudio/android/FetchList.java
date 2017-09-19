package com.mygdx.purefaithstudio.android;

import android.os.AsyncTask;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by harsimran singh on 13-09-2017.
 */

public class FetchList extends AsyncTask<String,String,String> {
    HttpURLConnection conn;
    URL url = null;
    FetchListener fetchListener;
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }
    @Override
    protected String doInBackground(String... strings) {

        try {

            // Enter URL address where your json file resides
            // Even you can make call to php file which returns json data
            url = new URL(Constants.URL);

        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        try {

            // Setup HttpURLConnection class to send and receive data from php and mysql
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(10000);
            conn.setRequestMethod("GET");

            // setDoOutput to true as we recieve data from json file
            //conn.setDoOutput(true);

        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            return null;
        }
        try {

            int response_code = conn.getResponseCode();
            Log.i("harsim",""+response_code);
            // Check if successful connection made
            if (response_code == HttpURLConnection.HTTP_OK) {

                // Read data sent from server
                InputStream input = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                // Pass data to onPostExecute method
                return (result.toString());

            } else {
                return null;
            }

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            conn.disconnect();
        }
    }
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
            fetchListener.onPostExecute(s);
    }

    public void setFetchListener(FetchListener fl){
        this.fetchListener = fl;
    }

    public interface FetchListener{
        void onPostExecute(String s);
    }
}
