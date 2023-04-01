package com.calculusmaster.pokecord.game.moves.builder;

import com.calculusmaster.pokecord.game.enums.elements.Weather;

public class WeatherEffect extends MoveEffect
{
    private Weather weather;

    public WeatherEffect(Weather weather)
    {
        this.weather = weather;
    }

    @Override
    public String get()
    {
        //Primal Weathers cannot be overwritten
        if(this.weather.isPrimalWeather())
            return this.move.getName() + " failed due to the presence of " + this.weather.getName() + "!";

        this.duel.weather.setWeather(this.weather);

        return this.user.getName() + " " + switch(this.weather) {
            case HAIL -> "summoned a Hailstorm!";
            case SANDSTORM -> "summoned a Sandstorm!";
            case RAIN -> "summoned a Rain Shower!";
            case HARSH_SUNLIGHT -> "summoned Harsh Sunlight!";
            case CLEAR -> "cleared the weather!";
            //These should never occur, as they don't come from moves, but they're here in case
            case EXTREME_HARSH_SUNLIGHT -> "summoned Extreme Harsh Sunlight!";
            case HEAVY_RAIN -> "summoned a Heavy Rain Shower!";
            case STRONG_WINDS -> "summoned a Delta Stream!";
        };
    }
}
