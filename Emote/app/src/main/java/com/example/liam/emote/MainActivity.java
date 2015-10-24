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

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {

    private static final OkHttpClient HTTP_CLIENT = new OkHttpClient();

    private static final String APP_NAME = "BeaconBattles";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            new HttpGetJsonTask().execute(new URL("http://192.168.224.130:5000/monsters?userlevel=5"));
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }
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
                Log.e(APP_NAME, "Could not retreive JSON from server:", ex);
            }
            return null;
        }

        protected void onPostExecute(String jsonResult) {
            Log.e(APP_NAME, jsonResult);
            Toast.makeText(MainActivity.this, "Got", Toast.LENGTH_LONG).show();
        }
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


}
