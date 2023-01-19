package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.ObjectiveType;
import com.calculusmaster.pokecord.game.bounties.objectives.core.Objective;

public class CompletePVPDuelObjective extends Objective
{
    public CompletePVPDuelObjective()
    {
        super(ObjectiveType.COMPLETE_PVP_DUEL);
    }

    @Override
    public String getDesc()
    {
        return "Complete " + this.target + " PvP Duels";
    }
}
