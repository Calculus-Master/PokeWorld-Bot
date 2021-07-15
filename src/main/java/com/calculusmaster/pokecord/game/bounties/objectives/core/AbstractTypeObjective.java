package com.calculusmaster.pokecord.game.bounties.objectives.core;

import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;
import com.calculusmaster.pokecord.game.enums.elements.Type;
import org.bson.Document;

import java.util.Random;

public abstract class AbstractTypeObjective extends Objective
{
    protected Type type;

    public AbstractTypeObjective(ObjectiveType type)
    {
        super(type);
        this.type = Type.values()[new Random().nextInt(Type.values().length)];
    }

    @Override
    public Document addObjectiveData(Document document)
    {
        return super.addObjectiveData(document)
                .append("type", this.type.toString());
    }

    public Type getType()
    {
        return this.type;
    }

    public void setType(String type)
    {
        this.type = Type.cast(type);
    }
}
