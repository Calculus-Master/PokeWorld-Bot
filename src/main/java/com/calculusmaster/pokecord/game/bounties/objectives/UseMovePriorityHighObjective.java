package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;

public class UseMovePriorityHighObjective extends Objective
{
    public UseMovePriorityHighObjective()
    {
        super(ObjectiveType.USE_MOVES_PRIORITY_HIGH, Objective.randomTargetAmount(5, 30));
    }

    @Override
    public String getDesc()
    {
        return "Use any " + this.target + " moves with high priority";
    }
}
