package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;

public class WinPVPDuelObjective extends Objective
{
    public WinPVPDuelObjective()
    {
        super(ObjectiveType.WIN_PVP_DUEL, Objective.randomTargetAmount(2, 5));
    }

    @Override
    public String getDesc()
    {
        return "Win " + this.target + " PvP Duels";
    }
}
