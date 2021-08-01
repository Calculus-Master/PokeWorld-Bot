package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;
import com.calculusmaster.pokecord.game.bounties.objectives.core.Objective;

public class ParticipateRaidObjective extends Objective
{
    public ParticipateRaidObjective()
    {
        super(ObjectiveType.PARTICIPATE_RAID);
    }

    @Override
    public String getDesc()
    {
        return "Participate in " + this.target + " Raids";
    }
}
