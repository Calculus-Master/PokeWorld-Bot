package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;
import com.calculusmaster.pokecord.game.bounties.objectives.core.AbstractAccuracyObjective;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class UseMoveAccuracyLessObjective extends AbstractAccuracyObjective
{
    public UseMoveAccuracyLessObjective()
    {
        super(ObjectiveType.USE_MOVES_ACCURACY_LESS);
    }

    @Override
    protected void setRandomAccuracy()
    {
        List<Integer> choices = Arrays.asList(90, 80, 70, 60);
        this.accuracy = choices.get(new Random().nextInt(choices.size()));
    }

    @Override
    public String getDesc()
    {
        return "Use any " + this.target + " Moves with base accuracy less than or equal to " + this.accuracy;
    }
}
