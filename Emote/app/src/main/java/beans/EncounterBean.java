package beans;

/**
 * Created by tony on 24/10/2015.
 */
public class EncounterBean
{
    private int id;
    private int monsterID;
    private int numWins;

    public EncounterBean()   //Blank contructor required by JavaBean Standard
    {}

    public EncounterBean(int id, int monsterID, int numWins)
    {
        this.id = id;
        this.monsterID = monsterID;
        this.numWins = numWins;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public int getId()
    {
        return id;
    }

    public void setMonsterID(int monsterID)
    {
        this.monsterID = monsterID;
    }

    public int getMonsterID()
    {
        return monsterID;
    }

    public void setNumWins(int numWins)
    {
        this.numWins = numWins;
    }

    public int getNumWins()
    {
        return numWins;
    }
}
