package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.ObjectiveType;
import com.calculusmaster.pokecord.game.bounties.objectives.core.Objective;

public class WinTrainerDuelObjective extends Objective
{
    public WinTrainerDuelObjective()
    {
        super(ObjectiveType.WIN_TRAINER_DUEL);
    }

    @Override
    public String getDesc()
    {
        return "Win " + this.target + " Trainer Duels";
    }
}
