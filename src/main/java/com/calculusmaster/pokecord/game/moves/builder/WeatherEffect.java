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
        this.duel.weather = this.weather;
        this.duel.weatherTurns = 5;

        return this.user.getName() + " " + switch(this.weather) {
            case HAIL -> "summoned a Hailstorm!";
            case SANDSTORM -> "summoned a Sandstorm!";
            case RAIN -> "summoned a Rain Shower!";
            case HARSH_SUNLIGHT -> "summoned Harsh Sunlight!";
            case CLEAR -> "cleared the weather!";
        };
    }
}
