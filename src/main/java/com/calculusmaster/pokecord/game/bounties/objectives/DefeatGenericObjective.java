package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.ObjectiveType;

public class DefeatGenericObjective extends Objective
{
    public DefeatGenericObjective(int target)
    {
        super(ObjectiveType.DEFEAT_POKEMON, target);
    }

    public DefeatGenericObjective()
    {
        this(Objective.randomTargetAmount(1, 10));
    }

    @Override
    public String getDesc()
    {
        return "Defeat any " + this.target + " Pokemon";
    }
}
