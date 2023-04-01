package com.calculusmaster.pokecord.game.pokemon.evolution.triggers;

import com.calculusmaster.pokecord.game.pokemon.Pokemon;

public class LevelEvoTrigger implements EvolutionTrigger
{
    protected final int level;

    public LevelEvoTrigger(int level)
    {
        this.level = level;
    }

    @Override
    public boolean canEvolve(Pokemon p, String serverID)
    {
        return p.getLevel() >= this.level;
    }

    @Override
    public String getDescription()
    {
        return "Level " + this.level;
    }
}
