package com.example.liam.emote;

import android.app.Application;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import android.app.*;
import android.content.*;
import android.widget.Toast;
import android.widget.TextView;

import com.estimote.sdk.Beacon;
import java.util.List;
import java.net.URL;
import java.io.InputStream;
import java.io.BufferedInputStream;


import java.util.UUID;

public class myApplication extends Application {

    private BeaconManager beaconManager;

    @Override
    public void onCreate() {
        super.onCreate();
        beaconManager = new BeaconManager(getApplicationContext());
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startMonitoring(new Region("monitored region", null,null,null));
            }
        });



        beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {
            @Override
            public void onEnteredRegion(Region region, List<Beacon> list) {
                String content = "";
                for(Beacon beacon : list) {
                    content = content + beacon.getMacAddress() + ", ";
                }
               showNotification("You have entered a battle", "You have entered a battle with a monster click to fight and check it out.");
            }

            @Override
            public void onExitedRegion(Region region) {
                Toast.makeText(getApplicationContext(), "Exited a beacon", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showNotification(String title, String message) {
        Intent notifyIntent = new Intent(this, MainActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivities(this, 0,
                new Intent[] { notifyIntent }, PendingIntent.FLAG_UPDATE_CURRENT);


        Notification notification = new Notification.Builder(this)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();

        notification.defaults |= Notification.DEFAULT_SOUND;
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }
}
