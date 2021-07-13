package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;

public class EarnEVsGenericObjective extends Objective
{
    public EarnEVsGenericObjective()
    {
        super(ObjectiveType.EARN_EVS, Objective.randomTargetAmount(10, 50));
    }

    @Override
    public String getDesc()
    {
        return "Earn " + this.target + " EVs in any Stat";
    }
}
