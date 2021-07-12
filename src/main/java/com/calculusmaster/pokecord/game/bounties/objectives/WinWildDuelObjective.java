package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;

public class WinWildDuelObjective extends Objective
{
    public WinWildDuelObjective()
    {
        super(ObjectiveType.WIN_WILD_DUEL, Objective.randomTargetAmount(10, 30));
    }

    @Override
    public String getDesc()
    {
        return "Win " + this.target + " Wild Pokemon Duels";
    }
}
