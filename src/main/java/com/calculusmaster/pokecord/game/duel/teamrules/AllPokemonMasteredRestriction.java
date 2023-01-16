package com.calculusmaster.pokecord.game.duel.teamrules;

import com.calculusmaster.pokecord.game.pokemon.Pokemon;

import java.util.List;

public class AllPokemonMasteredRestriction extends TeamRestriction
{
    public AllPokemonMasteredRestriction()
    {
        super("ALL_POKEMON_MASTERED");
    }

    @Override
    public boolean validate(List<Pokemon> team)
    {
        return team.stream().allMatch(Pokemon::isMastered);
    }

    @Override
    public String getDescription()
    {
        return "Team must only contain Pokemon that have been **mastered**.";
    }
}
