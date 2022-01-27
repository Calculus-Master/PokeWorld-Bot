package com.calculusmaster.pokecord.game.enums.elements;

import com.calculusmaster.pokecord.util.Global;

import java.util.EnumSet;

public enum Weather
{
    CLEAR("The weather is clear!"),
    HARSH_SUNLIGHT("Harsh sunlight roasts the battlefield!"),
    RAIN("There is a heavy rainstorm!"),
    SANDSTORM("There is an intense sandstorm!"),
    HAIL("There is a freezing hailstorm!"),
    EXTREME_HARSH_SUNLIGHT("Extreme harsh sunlight envelops the field!"),
    HEAVY_RAIN("There is an extreme rainstorm!"),
    STRONG_WINDS("Strong winds appeared!");

    private String status;
    Weather(String status)
    {
        this.status = status;
    }

    public String getStatus()
    {
        return this.status;
    }

    public String getName()
    {
        return Global.normalize(this.toString().replaceAll("_", " "));
    }

    public boolean isPrimalWeather()
    {
        return EnumSet.of(EXTREME_HARSH_SUNLIGHT, HEAVY_RAIN, STRONG_WINDS).contains(this);
    }
}
