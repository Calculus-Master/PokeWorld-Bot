package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;

public class CompleteWildDuelObjective extends Objective
{
    public CompleteWildDuelObjective()
    {
        super(ObjectiveType.COMPLETE_WILD_DUEL, Objective.randomTargetAmount(20, 50));
    }

    @Override
    public String getDesc()
    {
        return "Complete " + this.target + " Wild Duels";
    }
}
