package com.calculusmaster.pokecord.game.duel.teamrules;

import com.calculusmaster.pokecord.game.enums.elements.Type;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;

import java.util.List;

public class SingleTypeRestriction extends TeamRestriction
{
    private final Type type;

    public SingleTypeRestriction(Type type)
    {
        super("SINGLE_TYPE–" + type.toString());
        this.type = type;
    }

    @Override
    public boolean validate(List<Pokemon> team)
    {
        return team.stream().anyMatch(p -> p.isType(this.type));
    }

    @Override
    public String getDescription()
    {
        return "Team must contain **at least one " + this.type.getStyledName() + "**-type Pokemon.";
    }
}
