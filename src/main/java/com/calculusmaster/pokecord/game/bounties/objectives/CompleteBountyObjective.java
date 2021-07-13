package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;

public class CompleteBountyObjective extends Objective
{
    public CompleteBountyObjective()
    {
        super(ObjectiveType.COMPLETE_BOUNTY, Objective.randomTargetAmount(3, 10));
    }

    @Override
    public String getDesc()
    {
        return "Complete " + this.target + " bounties";
    }
}
