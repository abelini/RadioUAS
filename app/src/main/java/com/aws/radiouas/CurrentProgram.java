package com.aws.radiouas;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class CurrentProgram implements Runnable {

    public void CurrentProgram(String r){

    }
    @Override
    public void run() {/*
        HttpURLConnection urlConnection;
        try  {
            ConnectivityManager conManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
            if(networkInfo != null && networkInfo.isConnected()) {
                try {
                    URL url = new URL(programURL);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream stream = new BufferedInputStream(urlConnection.getInputStream());
                    program = InputStreamToString.getString(stream);
                    urlConnection.disconnect();
                }
                catch(Exception e){
                    program = e.getMessage();
                }
            }
            else {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    */}
}
