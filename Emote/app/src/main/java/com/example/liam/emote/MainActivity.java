package com.example.liam.emote;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.net.MalformedURLException;
import java.net.URL;

import android.widget.ImageView;
import android.widget.Toast;

import com.estimote.sdk.BeaconManager;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.net.URLConnection;

import android.app.Application;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import android.app.*;
import android.content.*;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import java.util.List;

import beans.UserBean;
import database.DatabaseHelper;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper dbhelper;
    Monster fightingMonster = null;
    private static final OkHttpClient HTTP_CLIENT = new OkHttpClient();
    private BeaconManager beaconManager;
    private static final String APP_NAME = "BeaconBattles";
    private UserBean user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbhelper = new DatabaseHelper(getApplicationContext());
        beaconManager = new BeaconManager(getApplicationContext());
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startMonitoring(new Region("monitored region", null, null, null));
            }
        });

        dbhelper.addUser(new UserBean(1, "Player", 0));

        user = dbhelper.getUserByID(1);

        Log.i(APP_NAME,user.getUsername() + ", " + user.getLevel() + ", " + user.getId());

        // set up listeners
        beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {
            @Override
            public void onEnteredRegion(Region region, List<Beacon> list) {
                try {
                    new HttpGetJsonTask().execute(new URL("http://192.168.224.130:5000/monsters?userlevel=" + user.getLevel()));

                } catch (Exception e) {

                }
                showNotification("You have entered a battle", "You have entered a battle with a monster click to fight and check it out.");
            }

            @Override
            public void onExitedRegion(Region region) {
                Toast.makeText(getApplicationContext(), "Exited a beacon", Toast.LENGTH_SHORT).show();
            }
        });
        // end of listeners
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class HttpGetJsonTask extends AsyncTask<URL, Void, String> {
        protected String doInBackground(URL... urls) {
            URL url = urls[0];
            try {
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                Response response = HTTP_CLIENT.newCall(request).execute();
                return response.body().string();
            } catch (IOException ex) {
                Log.e(APP_NAME, "Could not retreive JSON from server:");
            }
            return null;
        }

        protected void onPostExecute(String jsonResult) {
            if(jsonResult == null){
                Log.i(APP_NAME + "Failed", jsonResult);
            }else{
                fightingMonster = new Monster(jsonResult);
                Log.i(APP_NAME, jsonResult);
                runBattle();
            }

        }
    }

    public void runBattle(){

        if(fightingMonster != null) {
            // load monster on screen

            while(fightingMonster.getImage() == null){}

            ImageView imgV = (ImageView) findViewById(R.id.myImageView);
            imgV.setImageBitmap(fightingMonster.getImage());
            

            boolean battleWon = true;
            if (battleWon) {
                user.increaseLevel();
                dbhelper.updateUser(user);
            } else {

            }
        }
        else{
            Log.e("BITMAP", "Failed");
        }
    }
}
