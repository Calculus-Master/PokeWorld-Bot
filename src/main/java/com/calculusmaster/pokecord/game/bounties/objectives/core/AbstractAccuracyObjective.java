package com.calculusmaster.pokecord.game.bounties.objectives.core;

import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;
import org.bson.Document;

public abstract class AbstractAccuracyObjective extends Objective
{
    protected int accuracy;

    public AbstractAccuracyObjective(ObjectiveType type)
    {
        super(type);
    }

    protected abstract void setRandomAccuracy();

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

    public void setAccuracy(int accuracy)
    {
        this.accuracy = accuracy;
    }
}
