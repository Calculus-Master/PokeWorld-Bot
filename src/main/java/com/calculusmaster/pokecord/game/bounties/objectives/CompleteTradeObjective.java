package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.ObjectiveType;

public class CompleteTradeObjective extends Objective
{
    public CompleteTradeObjective()
    {
        super(ObjectiveType.COMPLETE_TRADE, Objective.randomTargetAmount(2, 6));
    }

    @Override
    public String getDesc()
    {
        return "Complete " + this.target + " Trades";
    }
}
