package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.ObjectiveType;

public class UseZMoveObjective extends Objective
{
    public UseZMoveObjective()
    {
        super(ObjectiveType.USE_ZMOVE, Objective.randomTargetAmount(2, 15));
    }

    @Override
    public String getDesc()
    {
        return "Use " + this.target + " Z-Moves";
    }
}
