package com.calculusmaster.pokecord.game.duel.restrictions.types;

import com.calculusmaster.pokecord.game.duel.restrictions.TeamRestriction;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.util.Global;

import java.util.List;

public class SpecificGenerationRestriction extends TeamRestriction
{
    private final int generation;

    public SpecificGenerationRestriction(int generation)
    {
        super("SPECIFIC_GENERATION_" + generation);
        this.generation = generation;
    }

    @Override
    public boolean validate(List<Pokemon> team)
    {
        return team.stream().allMatch(p -> Global.getGeneration(p.getData()) == this.generation);
    }

    @Override
    public String getDescription()
    {
        return "Team must contain only **Generation " + this.generation + " Pokemon**.";
    }
}
