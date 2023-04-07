package com.calculusmaster.pokecord.game.pokemon.evolution.triggers;

import com.calculusmaster.pokecord.game.enums.elements.Time;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonEntity;
import com.calculusmaster.pokecord.game.world.RegionManager;

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
        Time current = RegionManager.getCurrentTime();

        //If not Rockruff, daytime evolutions will now activate at Dusk as well
        if(!p.getEntity().equals(PokemonEntity.ROCKRUFF) && this.time.equals(Time.DAY)) return current.isDay();
        return current.equals(this.time);
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
