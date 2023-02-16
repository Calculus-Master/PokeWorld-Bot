package com.calculusmaster.pokecord.game.duel.restrictions.types;

import com.calculusmaster.pokecord.game.duel.restrictions.TeamRestriction;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;

import java.util.List;

public class TotalStatRestriction extends TeamRestriction
{
    private final int statTotalLimit;

    public TotalStatRestriction(int statTotalLimit)
    {
        super("TOTAL_STAT_RESTRICTION");
        this.statTotalLimit = statTotalLimit;
    }

    @Override
    public boolean validate(List<Pokemon> team) {
        return false;
    }

    @Override
    public String getDescription()
    {
        return "Team must **not** contain any Pokemon with a **stat total greater than " + this.statTotalLimit + "**.";
    }
}
