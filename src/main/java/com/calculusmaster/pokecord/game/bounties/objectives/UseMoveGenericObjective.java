package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;

public class UseMoveGenericObjective extends Objective
{
    public UseMoveGenericObjective()
    {
        super(ObjectiveType.USE_MOVES, Objective.randomTargetAmount(20, 80));
    }

    @Override
    public String getDesc()
    {
        return "Use any " + this.target + " moves";
    }
}
