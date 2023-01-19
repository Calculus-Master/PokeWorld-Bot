package com.calculusmaster.pokecord.game.bounties.objectives.core;

import com.calculusmaster.pokecord.game.bounties.ObjectiveType;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractPoolObjective extends Objective
{
    protected List<String> pool;

    public AbstractPoolObjective(ObjectiveType type)
    {
        super(type);
        this.pool = new ArrayList<>();
        this.setRandomPool();
    }

    protected abstract void setRandomPool();

    @Override
    public Document addObjectiveData(Document document)
    {
        return super.addObjectiveData(document)
                .append("pool", this.pool);
    }

    public List<String> getPool()
    {
        return this.pool;
    }

    public void setPool(List<String> pool)
    {
        this.pool = pool;
    }
}
