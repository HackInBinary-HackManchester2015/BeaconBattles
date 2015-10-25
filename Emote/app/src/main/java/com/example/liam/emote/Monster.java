package com.example.liam.emote;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.JsonReader;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLConnection;

public class Monster {
    private int level = 0;
    private int health = 0;
    private int iD = 0;
    Bitmap bitmap = null;
    String name = "Exception caught";

    Monster(String json) {

        JSONObject obj = null;
        try {
            obj = new JSONObject(json);
            this.health = obj.getInt("health");
            this.level = obj.getInt("level");
            this.iD = obj.getInt("id");
            this.name = obj.getString("name");
            URL url = new URL(obj.getString("imagePath"));
            bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (Exception e ){

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
}
