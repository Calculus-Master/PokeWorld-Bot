package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;

public class CompletePVPDuelObjective extends Objective
{
    public CompletePVPDuelObjective()
    {
        super(ObjectiveType.COMPLETE_PVP_DUEL, Objective.randomTargetAmount(5, 15));
    }

    @Override
    public String getDesc()
    {
        return "Complete " + this.target + " PvP Duels";
    }
}
