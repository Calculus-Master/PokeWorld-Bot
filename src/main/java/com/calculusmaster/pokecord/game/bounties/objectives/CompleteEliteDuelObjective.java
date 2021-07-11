package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.ObjectiveType;

public class CompleteEliteDuelObjective extends Objective
{
    public CompleteEliteDuelObjective()
    {
        super(ObjectiveType.COMPLETE_ELITE_DUEL, Objective.randomTargetAmount(2, 15));
    }

    @Override
    public String getDesc()
    {
        return "Complete " + this.target + " Elite Trainer Duels";
    }
}
