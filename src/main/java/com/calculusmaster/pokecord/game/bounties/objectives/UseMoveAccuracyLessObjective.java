package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;
import org.bson.Document;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class UseMoveAccuracyLessObjective extends Objective
{
    private int accuracy;

    public UseMoveAccuracyLessObjective()
    {
        super(ObjectiveType.USE_MOVES_ACCURACY_LESS, Objective.randomTargetAmount(2, 10));
        List<Integer> choices = Arrays.asList(90, 80, 70, 60);
        this.accuracy = choices.get(new Random().nextInt(choices.size()));
    }

    @Override
    public String getDesc()
    {
        return "Use any " + this.target + " Moves with base accuracy less than or equal to " + this.accuracy;
    }

    @Override
    public Document addObjectiveData(Document document)
    {
        return super.addObjectiveData(document)
                .append("accuracy", this.accuracy);
    }

    public int getAccuracy()
    {
        return this.accuracy;
    }

    public UseMoveAccuracyLessObjective setAccuracy(int accuracy)
    {
        this.accuracy = accuracy;
        return this;
    }
}
