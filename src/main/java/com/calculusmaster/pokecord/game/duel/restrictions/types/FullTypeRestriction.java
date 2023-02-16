package com.calculusmaster.pokecord.game.duel.restrictions.types;

import com.calculusmaster.pokecord.game.duel.restrictions.TeamRestriction;
import com.calculusmaster.pokecord.game.enums.elements.Type;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;

import java.util.List;

public class FullTypeRestriction extends TeamRestriction
{
    private final Type type;

    public FullTypeRestriction(Type type)
    {
        super("FULL_TYPE–" + type.toString());
        this.type = type;
    }

    @Override
    public boolean validate(List<Pokemon> team)
    {
        return team.stream().allMatch(p -> p.isType(this.type));
    }

    @Override
    public String getDescription()
    {
        return "Team must contain **only " + this.type.getStyledName() + "**-type Pokemon.";
    }
}
