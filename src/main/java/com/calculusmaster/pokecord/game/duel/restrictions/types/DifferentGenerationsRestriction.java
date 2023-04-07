package com.calculusmaster.pokecord.game.duel.restrictions.types;

import com.calculusmaster.pokecord.game.duel.restrictions.TeamRestriction;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;

import java.util.List;

public class DifferentGenerationsRestriction extends TeamRestriction
{
    public DifferentGenerationsRestriction()
    {
        super("DIFFERENT_GENERATIONS_RESTRICTION");
    }

    @Override
    public boolean validate(List<Pokemon> team)
    {
        return team.stream().map(p -> p.getEntity().getGeneration()).distinct().count() == team.size();
    }

    @Override
    public String getDescription()
    {
        return "Team must contain Pokemon from **different Generations**.";
    }
}
