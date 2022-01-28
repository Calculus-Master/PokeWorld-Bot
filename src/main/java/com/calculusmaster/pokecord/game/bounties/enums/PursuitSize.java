package com.calculusmaster.pokecord.game.bounties.enums;

import java.util.Random;

public enum PursuitSize
{
    MINI(2, 3, 1.0, 500, "An extremely small Pursuit. Usually meant for developer testing."),
    SHORT(5, 10, 1.2, 2000,  "A shorter Pursuit."),
    AVERAGE(15, 20, 1.5, 5000, "A normal-sized Pursuit."),
    LONG(25, 35, 2.0, 10000,  "A longer Pursuit."),
    JOURNEY(40, 60, 5.0, 25000,  "An experience. Much larger rewards than previous sizes."),
    LEGEND(75, 100, 10.0, 60000,  "The True Test.");

    private int min;
    private int max;
    public double multiplier;
    public int finalRewardCredits;
    public String desc;

    PursuitSize(int min, int max, double multiplier, int finalRewardCredits, String desc)
    {
        this.min = min;
        this.max = max;

        this.multiplier = multiplier;
        this.finalRewardCredits = finalRewardCredits;
        this.desc = desc;
    }

    public int generateSize()
    {
        return new Random().nextInt(this.max - this.min + 1) + this.min;
    }

    public String getOverview()
    {
        return this.desc + "\nBetween " + this.min + " and " + this.max + " Bounties\nFinal Reward: " + this.finalRewardCredits + "c\nBounty Reward Multiplier: " + this.multiplier;
    }

    public static PursuitSize get(int size)
    {
        for(PursuitSize s : values()) if(s.min <= size && s.max >= size) return s;
        return null;
    }

    public static PursuitSize cast(String size)
    {
        for(PursuitSize s : values()) if(s.toString().equalsIgnoreCase(size)) return s;
        return null;
    }
}
