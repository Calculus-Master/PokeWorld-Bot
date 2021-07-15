package com.calculusmaster.pokecord.game.bounties.objectives.core;

import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;
import org.bson.Document;

public abstract class AbstractNameObjective extends Objective
{
    protected String name;

    public AbstractNameObjective(ObjectiveType type)
    {
        super(type);
        this.setRandomName();
    }

    protected abstract void setRandomName();

    @Override
    public Document addObjectiveData(Document document)
    {
        return super.addObjectiveData(document)
                .append("name", this.name);
    }

    public String getName()
    {
        return this.name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}
