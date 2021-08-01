package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;
import com.calculusmaster.pokecord.game.bounties.objectives.core.Objective;

public class WinRaidObjective extends Objective
{
    public WinRaidObjective()
    {
        super(ObjectiveType.WIN_RAID_DUEL);
    }

    @Override
    public String getDesc()
    {
        return "Win " + this.target + " Raids";
    }
}
