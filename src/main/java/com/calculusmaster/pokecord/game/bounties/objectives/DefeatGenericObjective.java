package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;

public class DefeatGenericObjective extends Objective
{
    public DefeatGenericObjective()
    {
        super(ObjectiveType.DEFEAT_POKEMON, Objective.randomTargetAmount(1, 10));
    }

    @Override
    public String getDesc()
    {
        return "Defeat any " + this.target + " Pokemon";
    }
}
