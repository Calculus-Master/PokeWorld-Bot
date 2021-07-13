package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;

public class UseMovePriorityLowObjective extends Objective
{
    public UseMovePriorityLowObjective()
    {
        super(ObjectiveType.USE_MOVES_PRIORITY_LOW, Objective.randomTargetAmount(5, 30));
    }

    @Override
    public String getDesc()
    {
        return "Use any " + this.target + " moves with low priority";
    }
}
