package com.calculusmaster.pokecord.game.duel.component;

import com.calculusmaster.pokecord.game.enums.elements.Weather;

public class WeatherHandler
{
    private Weather weather;
    private int turns;

    public WeatherHandler()
    {
        this.weather = Weather.CLEAR;
        this.turns = -1;
    }

    public Weather get()
    {
        return this.weather;
    }

    public void setWeather(Weather weather)
    {
        this.weather = weather;
        this.turns = 5;
    }

    public void removeWeather()
    {
        this.weather = Weather.CLEAR;
        this.turns = -1;
    }

    public void updateTurns()
    {
        if(this.turns > 0)
        {
            this.turns--;

            if(this.turns <= 0) this.removeWeather();
        }
    }
}
