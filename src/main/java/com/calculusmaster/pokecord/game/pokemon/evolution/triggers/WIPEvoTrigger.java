package com.calculusmaster.pokecord.game.pokemon.evolution.triggers;

import com.calculusmaster.pokecord.game.pokemon.Pokemon;

public class WIPEvoTrigger implements EvolutionTrigger
{
    public WIPEvoTrigger() {}

    @Override
    public boolean canEvolve(Pokemon p, String serverID)
    {
        return false;
    }

    @Override
    public String getDescription()
    {
        return "**Cannot Evolve**. This Pokemon's Evolution method has not been implemented yet.";
    }
}
