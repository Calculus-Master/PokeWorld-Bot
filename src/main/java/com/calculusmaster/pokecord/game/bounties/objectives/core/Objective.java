package com.calculusmaster.pokecord.game.bounties.objectives.core;

import com.calculusmaster.pokecord.game.bounties.ObjectiveType;
import org.bson.Document;

public abstract class Objective
{
    protected ObjectiveType objectiveType;
    protected int progression;
    protected int target;

    public Objective(ObjectiveType objectiveType)
    {
        this.objectiveType = objectiveType;
        this.progression = 0;
        this.target = objectiveType.getRandomTarget();
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

    public ObjectiveType getObjectiveType()
    {
        return this.objectiveType;
    }

    public Objective setTarget(int target)
    {
        this.target = target;
        return this;
    }

    public Objective setTarget(double multiplier)
    {
        this.target = (int)(this.target * multiplier);
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

    //Casts
    public AbstractCategoryObjective asCategoryObjective()
    {
        return (AbstractCategoryObjective)this;
    }

    public AbstractNameObjective asNameObjective()
    {
        return (AbstractNameObjective)this;
    }

    public AbstractPoolObjective asPoolObjective()
    {
        return (AbstractPoolObjective)this;
    }

    public AbstractStatObjective asStatObjective()
    {
        return (AbstractStatObjective)this;
    }

    public AbstractTypeObjective asTypeObjective()
    {
        return (AbstractTypeObjective)this;
    }

    public AbstractPowerObjective asPowerObjective()
    {
        return (AbstractPowerObjective)this;
    }

    public AbstractAccuracyObjective asAccuracyObjective()
    {
        return (AbstractAccuracyObjective)this;
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
