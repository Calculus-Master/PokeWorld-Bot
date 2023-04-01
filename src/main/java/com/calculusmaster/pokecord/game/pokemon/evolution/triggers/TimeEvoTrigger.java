package com.calculusmaster.pokecord.game.pokemon.evolution.triggers;

import com.calculusmaster.pokecord.game.enums.elements.Time;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.util.helpers.event.LocationEventHelper;

public class TimeEvoTrigger implements EvolutionTrigger
{
    private final Time time;

    public TimeEvoTrigger(Time time)
    {
        this.time = time;
    }

    @Override
    public boolean canEvolve(Pokemon p, String serverID)
    {
        return LocationEventHelper.getTime().equals(this.time);
    }

    @Override
    public String getDescription()
    {
        return "During " + switch(this.time) {
            case DAY -> "the Daytime";
            case NIGHT -> "the Nighttime";
            case DUSK -> "Dusk";
        } + ".";
    }
}
