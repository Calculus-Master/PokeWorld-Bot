package com.calculusmaster.pokecord.game.duel.restrictions.types;

import com.calculusmaster.pokecord.game.duel.restrictions.TeamRestriction;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;

import java.util.List;

public class ContainsSpecificPokemonRestriction extends TeamRestriction
{
    private final String pokemon;

    public ContainsSpecificPokemonRestriction(String pokemon)
    {
        super("CONTAINS_SPECIFIC_POKEMON_" + pokemon.toUpperCase());
        this.pokemon = pokemon;
    }

    @Override
    public boolean validate(List<Pokemon> team)
    {
        return team.stream().anyMatch(p -> p.getName().equalsIgnoreCase(this.pokemon));
    }

    @Override
    public String getDescription()
    {
        return "Team must contain a **" + this.pokemon + "**.";
    }
}
