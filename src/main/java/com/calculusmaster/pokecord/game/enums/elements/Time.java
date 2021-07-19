package com.calculusmaster.pokecord.game.enums.elements;

public enum Time
{
    DAY,
    NIGHT,
    DUSK;

    public boolean isDay()
    {
        return this.equals(DAY) || this.equals(DUSK);
    }

    public boolean isNight()
    {
        return this.equals(NIGHT);
    }
}
