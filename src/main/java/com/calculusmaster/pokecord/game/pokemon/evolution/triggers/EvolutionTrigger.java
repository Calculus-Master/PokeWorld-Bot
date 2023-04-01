package com.calculusmaster.pokecord.game.pokemon.evolution.triggers;

import com.calculusmaster.pokecord.game.pokemon.Pokemon;

public interface EvolutionTrigger
{
    boolean canEvolve(Pokemon p, String serverID);

    String getDescription();
}
