package beans;

import android.util.Log;

/**
 * Created by tony on 24/10/2015.
 */
public class UserBean
{
    private int id;
    private String username;
    private int level;
    private int health;

    public UserBean()   //Blank contructor required by JavaBean Standard
    {
    }

    public UserBean(int id, String username, int level)
    {
        this.id = id;
        this.username = username;
        this.level = level;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public int getId()
    {
        return id;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getUsername()
    {
        return username;
    }

    public void setLevel(int level)
    {
        this.level = level;
    }

    public int getLevel()
    {
        return level;
    }

    public void increaseLevel(){
        level ++;
    }

    public void hit(){
        health = health - (level *2);
    }

    public void setHealth(int health){
        this.health = health;
    }

    public int getHealth(){
        return health;
    }
}
