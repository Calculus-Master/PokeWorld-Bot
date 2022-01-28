package com.calculusmaster.pokecord.game.enums.elements;

import com.calculusmaster.pokecord.util.Global;

import java.util.EnumSet;

public enum Weather
{
    CLEAR("The weather is clear!"),
    HARSH_SUNLIGHT("The sunlight is harsh!"),
    RAIN("It started to rain!!"),
    SANDSTORM("A sandstorm is raging!"),
    HAIL("It started to hail!"),
    EXTREME_HARSH_SUNLIGHT("The sunlight is extremely harsh!"),
    HEAVY_RAIN("Heavy rain began to fall!"),
    STRONG_WINDS("Mysterious strong winds blow through the field!");

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
