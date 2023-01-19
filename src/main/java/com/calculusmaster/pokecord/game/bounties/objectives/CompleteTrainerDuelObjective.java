package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.ObjectiveType;
import com.calculusmaster.pokecord.game.bounties.objectives.core.Objective;

public class CompleteTrainerDuelObjective extends Objective
{
    public CompleteTrainerDuelObjective()
    {
        super(ObjectiveType.COMPLETE_TRAINER_DUEL);
    }

    @Override
    public String getDesc()
    {
        return "Complete " + this.target + " Trainer Duels";
    }
}
