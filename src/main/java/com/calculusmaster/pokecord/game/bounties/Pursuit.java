package com.calculusmaster.pokecord.game.bounties;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Pursuit
{
    private List<Bounty> bounties;
    private Size size;

    public static Pursuit create(Size size)
    {
        Pursuit p = new Pursuit();

        p.setSize(size);
        p.setBounties();

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

    public void setSize(Size s)
    {
        this.size = s;
    }

    public Size getSize()
    {
        return this.size;
    }

    private void setBounties()
    {
        this.bounties = new ArrayList<>();
        int size = this.size.generateSize();

        for(int i = 0; i < size; i++) this.bounties.add(Bounty.create());
    }

    private void setBounties(List<String> bountyIDs)
    {
        for(String ID : bountyIDs)
        {
            this.bounties.add(Bounty.fromDB(ID));
        }
    }

    public enum Size
    {
        MINI(2, 3, 1.0, 500),
        SHORT(5, 10, 1.2, 2000),
        AVERAGE(15, 20, 1.5, 5000),
        LONG(25, 35, 2.0, 10000),
        JOURNEY(40, 60, 5.0, 25000),
        LEGEND(75, 100, 10.0, 60000);

        private int min;
        private int max;
        public double multiplier;
        public int finalReward;

        Size(int min, int max, double multiplier, int finalReward)
        {
            this.min = min;
            this.max = max;

            this.multiplier = multiplier;
            this.finalReward = finalReward;
        }

        public int generateSize()
        {
            return new Random().nextInt(this.max - this.min + 1) + this.min;
        }
    }
}
