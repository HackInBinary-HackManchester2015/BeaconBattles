package com.example.liam.emote;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.JsonReader;
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

    Monster(URLConnection url) {
        try {
            url.connect();
            InputStream in = url.getInputStream();
            JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                switch (name) {
                    case "health":
                        this.health = reader.nextInt();
                        break;
                    case "id":
                        this.iD = reader.nextInt();
                        break;
                    case "picture":
                        bitmap = BitmapFactory.decodeStream((InputStream) new URL(reader.nextString()).getContent());
                        break;
                    case "level":
                        this.level = reader.nextInt();
                        break;
                    case "name":
                        this.name = reader.nextString();
                        break;
                    default:
                        //Do nothing
                        break;
                }
            }//end while
        } catch (Exception e) {
            //PANIC!!!!!!
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
