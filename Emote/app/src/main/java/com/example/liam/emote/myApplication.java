package com.example.liam.emote;

import android.app.Application;
import com.estimote.sdk.BeaconManager;

public class myApplication extends Application {

    private BeaconManager beaconManager;

    @Override
    public void onCreate() {
        super.onCreate();
        beaconManager = new BeaconManager(getApplicationContext());
    }
}
