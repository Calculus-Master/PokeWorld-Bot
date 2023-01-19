package com.calculusmaster.pokecord.game.bounties.objectives.core;

import com.calculusmaster.pokecord.game.bounties.ObjectiveType;
import org.bson.Document;

public abstract class AbstractPowerObjective extends Objective
{
    protected int power;

    public AbstractPowerObjective(ObjectiveType type)
    {
        super(type);
    }

    protected abstract void setRandomPower();

    @Override
    public Document addObjectiveData(Document document)
    {
        return super.addObjectiveData(document)
                .append("power", this.power);
    }

    public int getPower()
    {
        return this.power;
    }

    public void setPower(int power)
    {
        this.power = power;
    }
}
