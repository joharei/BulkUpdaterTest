package com.example.johrei.bulkupdatertest;

import android.app.Application;

import com.estimote.coresdk.common.config.EstimoteSDK;


public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        EstimoteSDK.initialize(this, getString(R.string.app_id), getString(R.string.app_token));
    }
}
