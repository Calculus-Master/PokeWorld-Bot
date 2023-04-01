package com.calculusmaster.pokecord.game.bounties.objectives.core;

import com.calculusmaster.pokecord.game.bounties.ObjectiveType;
import org.bson.Document;

public abstract class AbstractNameObjective extends Objective
{
    protected String entityName;

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
                .append("name", this.entityName);
    }

    public String getName()
    {
        return this.entityName;
    }

    public void setName(String name)
    {
        this.entityName = name;
    }
}
