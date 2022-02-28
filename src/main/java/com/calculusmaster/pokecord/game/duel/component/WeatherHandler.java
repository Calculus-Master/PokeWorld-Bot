package com.calculusmaster.pokecord.game.duel.component;

import com.calculusmaster.pokecord.game.enums.elements.Weather;

public class WeatherHandler
{
    private Weather weather;
    private int turns;

    public WeatherHandler()
    {
        this.setPermanentWeather(Weather.CLEAR);
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

    public void setPermanentWeather(Weather weather)
    {
        this.weather = weather;
        this.turns = -1;
    }

    public void removeWeather()
    {
        this.setPermanentWeather(Weather.CLEAR);
    }

    public void updateTurns()
    {
        //Primal Weathers don't remove automatically
        if(this.weather.isPrimalWeather()) return;

        //Standard weathers do
        if(this.turns > 0)
        {
            this.turns--;

            if(this.turns <= 0) this.removeWeather();
        }
    }
}
