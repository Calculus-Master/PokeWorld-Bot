package com.calculusmaster.pokecord.game.bounties;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Pursuit
{
    public static final int MIN_PURSUIT_SIZE = 15;
    public static final int MAX_PURSUIT_SIZE = 20;

    private List<Bounty> bounties;

    public static Pursuit create()
    {
        Pursuit p = new Pursuit();

        int size = new Random().nextInt(MAX_PURSUIT_SIZE - MIN_PURSUIT_SIZE + 1) + MIN_PURSUIT_SIZE;

        p.setBounties(size);

        return p;
    }

    //Only if Pursuit.create() has been called immediately beforehand (newly generated Pursuit)
    public Pursuit upload()
    {
        for(Bounty b : this.bounties) Bounty.toDB(b);
        return this;
    }

    public static Pursuit fromIDs(List<String> bountyIDs)
    {
        Pursuit p = new Pursuit();

        p.setBounties(bountyIDs);

        return p;
    }

    public int getProgressLevel()
    {
        int level = -1;
        for(int i = 0; i < this.bounties.size(); i++) if(!this.bounties.get(i).getObjective().isComplete()) level = i;

        return level + 1;
    }

    public String getProgress()
    {
        return this.getProgress() + " / " + this.bounties.size();
    }

    public List<Bounty> getBounties()
    {
        return this.bounties;
    }

    private void setBounties(int size)
    {
        this.bounties = new ArrayList<>();

        for(int i = 0; i < size; i++) this.bounties.add(Bounty.create());
    }

    private void setBounties(List<String> bountyIDs)
    {
        for(String ID : bountyIDs)
        {
            this.bounties.add(Bounty.fromDB(ID));
        }
    }
}
