package com.calculusmaster.pokecord.game.duel.teamrules;

import com.calculusmaster.pokecord.game.pokemon.Pokemon;

import java.util.List;

public abstract class TeamRestriction
{
    private final String restrictionID;

    protected TeamRestriction(String restrictionID)
    {
        this.restrictionID = restrictionID;
        TeamRestrictionRegistry.RESTRICTIONS.add(this);
    }

    public abstract boolean validate(List<Pokemon> team);
    public abstract String getDescription();

    public String getRestrictionID()
    {
        return this.restrictionID;
    }
}
