package com.example.liam.emote;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class Monster {
    private int level = 0;
    private int health = 0;
    private int iD = 0;
    Bitmap bitmap = null;
    String name = "Exception caught";
    InputStream stream;

    private static final OkHttpClient HTTP_CLIENT = new OkHttpClient();

    Monster(String json) {

        JSONObject obj = null;
        try {
            obj = new JSONObject(json);
            this.health = obj.getInt("health");
            this.level = obj.getInt("level");
            this.iD = obj.getInt("id");
            this.name = obj.getString("name");
            //URL url = new URL("http://192.168.224.130:5001/7.jpg");
            URL url = new URL(obj.getString("imgPath"));
            new HttpGetJsonTask().execute(url);
        } catch (Exception e ){
            Log.e("Monster","Exception caught");
        }
    }

    public Bitmap getImage(){
        return bitmap;
    }
    public String getName(){
        return name;
    }
    public int getHealth(){
        return health;
    }


    private class HttpGetJsonTask extends AsyncTask<URL, Void, String> {
        protected String doInBackground(URL... urls) {
            URL url = urls[0];
            try {
                Request request = new Request.Builder()
                        .url(url)
                        .build();

                Response response = HTTP_CLIENT.newCall(request).execute();
                stream = response.body().byteStream();
                bitmap = BitmapFactory.decodeStream(stream);


                return "Worked";
            } catch (IOException ex) {
                Log.e("MONSTER_CLASS", "Could not retreive JSON from server:");
            }
            return null;
        }

        protected void onPostExecute(String byteStream) {
        }
    }
    public void hit(){
        health --;
    }
    public int getLevel(){
        return level;
    }
}