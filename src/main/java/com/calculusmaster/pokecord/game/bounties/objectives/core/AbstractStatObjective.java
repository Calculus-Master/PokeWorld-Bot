package com.calculusmaster.pokecord.game.bounties.objectives.core;

import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import org.bson.Document;

import java.util.Random;

public abstract class AbstractStatObjective extends Objective
{
    protected Stat stat;
    
    public AbstractStatObjective(ObjectiveType type)
    {
        super(type);
        this.stat = Stat.values()[new Random().nextInt(Stat.values().length)];
    }

    @Override
    public Document addObjectiveData(Document document)
    {
        return super.addObjectiveData(document)
                .append("stat", this.stat.toString());
    }

    public Stat getStat()
    {
        return this.stat;
    }

    public void setStat(String stat)
    {
        this.stat = Stat.cast(stat);
    }
}
