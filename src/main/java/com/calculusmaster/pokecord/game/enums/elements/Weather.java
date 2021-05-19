package com.calculusmaster.pokecord.game.enums.elements;

public enum Weather
{
    CLEAR ("The weather is clear!"),
    HARSH_SUNLIGHT ("Harsh sunlight roasts the battlefield!"),
    RAIN ("There is a heavy rainstorm!"),
    SANDSTORM("There is an intense sandstorm!"),
    HAIL("There is a freezing hailstorm!");

    private String status;
    Weather(String status)
    {
        this.status = status;
    }

    public String getStatus()
    {
        return this.status;
    }
}
