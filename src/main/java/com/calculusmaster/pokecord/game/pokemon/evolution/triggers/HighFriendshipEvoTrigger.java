package com.calculusmaster.pokecord.game.pokemon.evolution.triggers;

import com.calculusmaster.pokecord.game.pokemon.Pokemon;

public class HighFriendshipEvoTrigger implements EvolutionTrigger
{
    public HighFriendshipEvoTrigger() {}

    @Override
    public boolean canEvolve(Pokemon p, String serverID)
    {
        //TODO: Friendship Evolutions - this is a temporary replacement
        return p.getLevel() > 50 && p.getTotalEV() > 20;
    }

    @Override
    public String getDescription()
    {
        return "High Friendship";
    }
}
