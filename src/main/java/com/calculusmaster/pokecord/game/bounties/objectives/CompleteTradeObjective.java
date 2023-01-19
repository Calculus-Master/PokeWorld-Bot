package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.ObjectiveType;
import com.calculusmaster.pokecord.game.bounties.objectives.core.Objective;

public class CompleteTradeObjective extends Objective
{
    public CompleteTradeObjective()
    {
        super(ObjectiveType.COMPLETE_TRADE);
    }

    @Override
    public String getDesc()
    {
        return "Complete " + this.target + " Trades";
    }
}
