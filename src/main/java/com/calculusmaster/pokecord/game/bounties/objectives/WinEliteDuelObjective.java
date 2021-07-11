package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.ObjectiveType;

public class WinEliteDuelObjective extends Objective
{
    public WinEliteDuelObjective()
    {
        super(ObjectiveType.WIN_ELITE_DUEL, Objective.randomTargetAmount(2, 8));
    }

    @Override
    public String getDesc()
    {
        return "Win " + this.target + " Elite Trainer Duels";
    }
}
