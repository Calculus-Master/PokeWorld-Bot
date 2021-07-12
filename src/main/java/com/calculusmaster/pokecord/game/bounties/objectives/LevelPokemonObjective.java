package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;

public class LevelPokemonObjective extends Objective
{
    public LevelPokemonObjective()
    {
        super(ObjectiveType.LEVEL_POKEMON, Objective.randomTargetAmount(2, 10));
    }

    @Override
    public String getDesc()
    {
        return "Level Up " + " Pokemon " + this.target + " times";
    }
}
