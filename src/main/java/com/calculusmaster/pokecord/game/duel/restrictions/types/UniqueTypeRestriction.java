package com.calculusmaster.pokecord.game.duel.restrictions.types;

import com.calculusmaster.pokecord.game.duel.restrictions.TeamRestriction;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;

import java.util.List;

public class UniqueTypeRestriction extends TeamRestriction
{
    public UniqueTypeRestriction()
    {
        super("UNIQUE_TYPE_RESTRICTION");
    }

    @Override
    public boolean validate(List<Pokemon> team)
    {
        return team.stream().map(p -> p.getType().get(0)).distinct().count() == team.size();
    }

    @Override
    public String getDescription()
    {
        return "Team must contain **unique primary types**. No two Pokemon on the team can share a primary type.";
    }
}
