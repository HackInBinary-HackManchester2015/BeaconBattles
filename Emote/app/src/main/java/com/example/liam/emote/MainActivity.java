package com.example.liam.emote;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.net.URL;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import beans.EncounterBean;
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

        dbhelper = new DatabaseHelper(getApplicationContext());
        setContentView(R.layout.activity_main);

        beaconManager = new BeaconManager(getApplicationContext());
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startMonitoring(new Region("monitored region", null, null, null));
            }
        });

        dbhelper.addUser(new UserBean(1, "Player", 0));

        user = dbhelper.getUserByID(1);

        Log.i(APP_NAME, user.getUsername() + ", " + user.getLevel() + ", " + user.getId());

        Button run = (Button) findViewById(R.id.myRunButton);
        run.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runaway();
            }
        });


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

    public void runBattle() {

        if (fightingMonster != null) {

            // load monster on screen
            int counter = 0;
            while (fightingMonster.getImage() == null && counter != 1000) {
            }

            final ProgressBar progBar = (ProgressBar) findViewById(R.id.progressBar);
            progBar.setMax(fightingMonster.getHealth());
            progBar.setProgress(fightingMonster.getHealth());
            ImageView imgV = (ImageView) findViewById(R.id.myImageView);

            final ProgressBar userHealthBar = (ProgressBar) findViewById(R.id.userHealthBar);
            user.setHealth(12 * user.getLevel());
            userHealthBar.setMax(user.getHealth());
            userHealthBar.setProgress(user.getHealth());
            Random rndNumGen = new Random();
            //Generate 2 numbers (one for each combatant) who ever gets higher wins
            int playerScore = rndNumGen.nextInt(10);
            int monsterScore = rndNumGen.nextInt(9);
            imgV.setImageBitmap(fightingMonster.getImage());
            imgV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Random rndNumGen = new Random();
                    //Generate 2 numbers (one for each combatant) who ever gets higher wins
                    int playerScore = rndNumGen.nextInt(10);
                    int monsterScore = rndNumGen.nextInt(9);
                    if (playerScore >= monsterScore) {
                        fightingMonster.hit();
                        userHealthBar.setProgress(user.getHealth());
                    }
                    else if (monsterScore >= playerScore){
                        user.hit();
                        progBar.setProgress(fightingMonster.getHealth());
                    }
                    if (fightingMonster.getHealth() == 0) {
                        Toast.makeText(MainActivity.this, "Health 0", Toast.LENGTH_LONG).show();
                        user.increaseLevel();
                        dbhelper.updateUser(user);

                        loadMainPage("Congratulations you defeated " + fightingMonster.getName() + " your new level is " + user.getLevel());
                    }
                }
            });
        }
    }

    public void loadMainPage(String input){
        setContentView(R.layout.mainpage_layout);
        if (input.length() != 0){
            TextView textV = (TextView) findViewById(R.id.inputTextView);
            textV.setText(input);
        }
        Button accountSettings = (Button) findViewById(R.id.accountSettings);
        accountSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadAccountSettings();
            }
        });
    }

    public void runaway(){
        fightingMonster = null;
        loadMainPage("");
    }


    public void loadAccountSettings(){
        setContentView(R.layout.listview);
        ArrayList<EncounterBean> array = dbhelper.getAllEncounters();
        List<String> firstArray = new ArrayList();
        List<String> secondArray = new ArrayList();
        for(EncounterBean bean : array){
            firstArray.add(bean.getMonsterID() + "");
            secondArray.add(bean.getNumWins() + "");
        }
        String[] first = new String[firstArray.size()];
        String[] second = new String[secondArray.size()];

        first = firstArray.toArray(first);
        second = secondArray.toArray(second);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.listview,R.id.firstList,first);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, R.layout.listview,R.id.secondList,second);

    }
}
