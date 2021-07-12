package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;

public class WinPVPDuelObjective extends Objective
{
    public WinPVPDuelObjective()
    {
        super(ObjectiveType.COMPLETE_TRADE, Objective.randomTargetAmount(2, 5));
    }

    @Override
    public String getDesc()
    {
        return "Win " + this.target + " PvP Duels";
    }
}
