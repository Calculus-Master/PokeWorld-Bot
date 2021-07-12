package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;

public class CatchGenericObjective extends Objective
{
    public CatchGenericObjective()
    {
        super(ObjectiveType.CATCH_POKEMON, Objective.randomTargetAmount(10, 80));
    }

    @Override
    public String getDesc()
    {
        return "Catch " + this.target + " Pokemon";
    }
}
