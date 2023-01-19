package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.ObjectiveType;
import com.calculusmaster.pokecord.game.bounties.objectives.core.AbstractStatObjective;
import com.calculusmaster.pokecord.game.enums.elements.Stat;

import java.util.Random;

public class EarnEVsStatObjective extends AbstractStatObjective
{
    public EarnEVsStatObjective()
    {
        super(ObjectiveType.EARN_EVS_STAT);
        this.stat = Stat.values()[new Random().nextInt(Stat.values().length)];
    }

    @Override
    public String getDesc()
    {
        return "Earn " + this.target + " EVs in " + this.stat.name;
    }
}
