package com.calculusmaster.pokecord.game.duel.restrictions.types;

import com.calculusmaster.pokecord.game.duel.restrictions.TeamRestriction;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonEntity;

import java.util.List;

public class ContainsSpecificPokemonRestriction extends TeamRestriction
{
    private final PokemonEntity pokemonEntity;

    public ContainsSpecificPokemonRestriction(PokemonEntity pokemonEntity)
    {
        super("CONTAINS_SPECIFIC_POKEMON_" + pokemonEntity.toString());
        this.pokemonEntity = pokemonEntity;
    }

    @Override
    public boolean validate(List<Pokemon> team)
    {
        return team.stream().anyMatch(p -> p.getEntity() == this.pokemonEntity);
    }

    @Override
    public String getDescription()
    {
        return "Team must contain a **" + this.pokemonEntity.getName() + "**.";
    }
}
