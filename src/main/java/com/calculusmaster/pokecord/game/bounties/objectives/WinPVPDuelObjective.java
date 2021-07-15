package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;
import com.calculusmaster.pokecord.game.bounties.objectives.core.Objective;

public class WinPVPDuelObjective extends Objective
{
    public WinPVPDuelObjective()
    {
        super(ObjectiveType.WIN_PVP_DUEL);
    }

    @Override
    public String getDesc()
    {
        return "Win " + this.target + " PvP Duels";
    }
}
