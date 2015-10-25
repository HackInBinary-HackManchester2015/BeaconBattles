package database;

import java.util.*;

import beans.UserBean;
import beans.EncounterBean;
import android.util.Log;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper
{
    private static final String dbName = "dbBeaconBattle";
    private static final String colUserID = "id_user";
    private static final String colUserUsername = "username";
    private static final String colUserLevel = "level";
    private static final String colEncountersID = "id_encounters";
    private static final String colMonsterID = "id_monster";
    private static final String colEncountersNumWins = "num_wins" ;
    private SQLiteDatabase data;

    public DatabaseHelper(Context context)
    {
        super(context, dbName, null, 33);
    }

    @Override
    public void onCreate(SQLiteDatabase db)     //Called when accessed but database is not created
    {

        db.execSQL("CREATE TABLE User(" + colUserID + " INTEGER UNSIGNED AUTO_INCREMENT PRIMARY KEY, " + colUserUsername + " VARCHAR(32) NOT NULL, " + colUserLevel + " INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE Encounters(" + colEncountersID + " INTEGER UNSIGNED AUTO_INCREMENT PRIMARY KEY, " + colMonsterID + " VARCHAR(32) REFERENCES User(id_user) NOT NULL, " + colEncountersNumWins + " INTEGER NOT NULL)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)    //called when tables are altered
    {
        db.execSQL("DROP TABLE IF EXISTS User");
        db.execSQL("DROP TABLE IF EXISTS Encounters");

        onCreate(db);
    }

    public void addUser(UserBean user)
    {
        if(getNumUsers() == 0)
        {
            data = this.getWritableDatabase();
            data.execSQL("INSERT INTO User (" + colUserID + ", " + colUserUsername + ", " + colUserLevel + ") VALUES ('" + user.getId() + "', '" + user.getUsername() + "','" + user.getLevel() + "')");
        } else
        {
            Log.i("DEBUG", "More than 1 user! Insert failed.");
        }
    }

    public void addEncounter(EncounterBean encounter)
    {
        data = this.getWritableDatabase();
        data.execSQL("INSERT INTO Encounters (" + colEncountersID + ", " + colMonsterID + ", " + colEncountersNumWins + ") VALUES ('" + encounter.getId() + "','" + encounter.getMonsterID() + "','" + encounter.getNumWins() + "')");

    }

    public UserBean getUserByID(int id)
    {
        data = this.getReadableDatabase();
        UserBean user = null;
        Cursor c = data.rawQuery("SELECT * FROM User WHERE " + colUserID + " = " + id + "", null);

        if (c.moveToFirst())
        {
            while (!c.isAfterLast())
            {
                user = new UserBean(c.getInt(0), c.getString(1), c.getInt(2));
                c.moveToNext();
            }
        }
        return user;
    }

    private int getNumUsers()
    {
        data = this.getReadableDatabase();
        Cursor c = data.rawQuery("SELECT COUNT(id_user) FROM User", null);
        int numUsers = 0;

        if (c.moveToFirst())
        {
            while (!c.isAfterLast())
            {
                numUsers = c.getInt(0);
                c.moveToNext();
            }
        }
        return numUsers;
    }

    public EncounterBean getEncounterByID(int id)
    {
        data = this.getReadableDatabase();
        EncounterBean user = null;
        Cursor c = data.rawQuery("SELECT * FROM Encounters WHERE " + colEncountersID + " = " + id + "", null);

        if (c.moveToFirst())
        {
            while (!c.isAfterLast())
            {
                user = new EncounterBean(c.getInt(0), c.getInt(1), c.getInt(2));
                c.moveToNext();
            }
        }
        return user;
    }

    public ArrayList<EncounterBean> getAllEncounters()
    {
        data = this.getReadableDatabase();
        ArrayList<EncounterBean> encounters = new ArrayList<EncounterBean>();
        Cursor c = data.rawQuery("SELECT * FROM Encounters", null);

        if (c.moveToFirst())
        {
            while (!c.isAfterLast())
            {
                encounters.add(new EncounterBean(c.getInt(0), c.getInt(1), c.getInt(2)));
                c.moveToNext();
            }
        }
        return encounters;
    }

    public void updateUser(UserBean user)
    {
        data = this.getWritableDatabase();
        data.execSQL("UPDATE User SET id_user=" + user.getId() + " , username=\"" + user.getUsername() + "\", level=" + user.getLevel());

    }

    public void updateEncounter(EncounterBean encounter)
    {
        data = this.getWritableDatabase();
        data.execSQL("UPDATE Encounters SET id_monster=" + encounter.getMonsterID() + ", num_wins=" + encounter.getNumWins() + " WHERE id_encounters=" + encounter.getId());
    }
}
