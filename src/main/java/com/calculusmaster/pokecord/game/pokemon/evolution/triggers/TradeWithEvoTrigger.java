package com.calculusmaster.pokecord.game.pokemon.evolution.triggers;

import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonEntity;

public class TradeWithEvoTrigger implements EvolutionTrigger
{
    private final PokemonEntity other;

    public TradeWithEvoTrigger(PokemonEntity other)
    {
        this.other = other;
    }

    @Override
    public boolean canEvolve(Pokemon p, String serverID)
    {
        return false;
    }

    @Override
    public String getDescription()
    {
        return "Trade with a " + this.other.getName();
    }

    public PokemonEntity getOther()
    {
        return this.other;
    }
}
