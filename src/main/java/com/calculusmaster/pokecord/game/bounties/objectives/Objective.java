package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.ObjectiveType;
import org.bson.Document;

import java.util.Random;

public abstract class Objective
{
    protected ObjectiveType objectiveType;
    protected int progression;
    protected int target;

    public Objective(ObjectiveType objectiveType, int target)
    {
        this.objectiveType = objectiveType;
        this.progression = 0;
        this.target = target;
    }

    public Document addObjectiveData(Document document)
    {
        return document
                .append("objective_type", this.objectiveType.toString())
                .append("progression", this.progression)
                .append("target", this.target);
    }

    public abstract String getDesc();

    public String getStatus()
    {
        return this.progression + " / " + this.target;
    }

    public void update()
    {
        if(!this.isComplete()) this.progression++;
    }

    public boolean isComplete()
    {
        return this.progression >= this.target;
    }

    public static int randomTargetAmount(int min, int max)
    {
        return new Random().nextInt(max - min + 1) + min;
    }

    public ObjectiveType getObjectiveType()
    {
        return this.objectiveType;
    }

    public Objective setTarget(int target)
    {
        this.target = target;
        return this;
    }

    public Objective setProgression(int progression)
    {
        this.progression = progression;
        return this;
    }

    public int getProgression()
    {
        return this.progression;
    }

    public int getTarget()
    {
        return this.target;
    }

    @Override
    public String toString() {
        return "Objective{" +
                "objectiveType=" + objectiveType +
                ", progression=" + progression +
                ", target=" + target +
                '}';
    }
}
