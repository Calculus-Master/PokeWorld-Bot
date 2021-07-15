package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;
import com.calculusmaster.pokecord.game.bounties.objectives.core.Objective;

public class WinEliteDuelObjective extends Objective
{
    public WinEliteDuelObjective()
    {
        super(ObjectiveType.WIN_ELITE_DUEL);
    }

    @Override
    public String getDesc()
    {
        return "Win " + this.target + " Elite Trainer Duels";
    }
}
