package com.calculusmaster.pokecord.game.bounties;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class PursuitBuilder
{
    private List<Bounty> bounties;

    public static PursuitBuilder create(Size s)
    {
        PursuitBuilder p = new PursuitBuilder();

        p.bounties = new ArrayList<>();

        int size = s.generateSize();
        for(int i = 0; i < size; i++)
        {
            Bounty b = Bounty.create();

            if(i == size - 1) b.getObjective().setTarget(1.75);
            else if(i == size - 2) b.getObjective().setTarget(1.25);

            p.bounties.add(b);
        }

        p.bounties.get(new Random().nextInt(p.bounties.size())).getObjective().setTarget(2.0);

        return p;
    }

    public List<String> getIDs()
    {
        return this.bounties.stream().map(b -> b.getBountyID()).collect(Collectors.toList());
    }

    public void build()
    {
        for(Bounty b : this.bounties) Bounty.toDB(b);
    }

    public enum Size
    {
        MINI(2, 3, 1.0, 500, 0.9, "An extremely small Pursuit. Usually meant for developer testing."),
        SHORT(5, 10, 1.2, 2000, 0.95, "A shorter Pursuit."),
        AVERAGE(15, 20, 1.5, 5000, 1.0, "A normal-sized Pursuit."),
        LONG(25, 35, 2.0, 10000, 1.05, "A longer Pursuit."),
        JOURNEY(40, 60, 5.0, 25000, 1.1, "An experience. Much larger rewards than previous sizes."),
        LEGEND(75, 100, 10.0, 60000, 1.2, "The True Test.");

        private int min;
        private int max;
        public double multiplier;
        public int finalRewardCredits;
        private double xpMultiplier;
        public String desc;

        Size(int min, int max, double multiplier, int finalRewardCredits, double xpMultiplier, String desc)
        {
            this.min = min;
            this.max = max;

            this.multiplier = multiplier;
            this.finalRewardCredits = finalRewardCredits;
            this.xpMultiplier = xpMultiplier;
            this.desc = desc;
        }

        public int generateSize()
        {
            return new Random().nextInt(this.max - this.min + 1) + this.min;
        }

        public int getPokePassXPReward(int size)
        {
            return (int)(size * (Bounty.POKEPASS_EXP_YIELD * this.xpMultiplier));
        }

        public String getOverview()
        {
            return this.desc + "\nBetween " + this.min + " and " + this.max + " Bounties\nFinal Reward: " + this.finalRewardCredits + "c\nBounty Reward Multiplier: " + this.multiplier;
        }

        public static Size get(int size)
        {
            for(Size s : values()) if(s.min <= size && s.max >= size) return s;
            return null;
        }

        public static Size cast(String size)
        {
            for(Size s : values()) if(s.toString().equalsIgnoreCase(size)) return s;
            return null;
        }
    }
}
