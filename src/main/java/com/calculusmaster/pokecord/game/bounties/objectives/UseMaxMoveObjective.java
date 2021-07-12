package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;

public class UseMaxMoveObjective extends Objective
{
    public UseMaxMoveObjective()
    {
        super(ObjectiveType.USE_MAX_MOVE, Objective.randomTargetAmount(5, 30));
    }

    @Override
    public String getDesc()
    {
        return "Use " + this.target + " Max Moves";
    }
}
