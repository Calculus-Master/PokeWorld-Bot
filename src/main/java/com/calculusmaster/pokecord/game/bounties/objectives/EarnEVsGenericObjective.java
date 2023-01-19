package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.ObjectiveType;
import com.calculusmaster.pokecord.game.bounties.objectives.core.Objective;

public class EarnEVsGenericObjective extends Objective
{
    public EarnEVsGenericObjective()
    {
        super(ObjectiveType.EARN_EVS);
    }

    @Override
    public String getDesc()
    {
        return "Earn " + this.target + " EVs in any Stat";
    }
}
