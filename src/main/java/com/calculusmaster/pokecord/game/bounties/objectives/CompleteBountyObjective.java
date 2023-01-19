package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.ObjectiveType;
import com.calculusmaster.pokecord.game.bounties.objectives.core.Objective;

public class CompleteBountyObjective extends Objective
{
    public CompleteBountyObjective()
    {
        super(ObjectiveType.COMPLETE_BOUNTY);
    }

    @Override
    public String getDesc()
    {
        return "Complete " + this.target + " bounties";
    }
}
