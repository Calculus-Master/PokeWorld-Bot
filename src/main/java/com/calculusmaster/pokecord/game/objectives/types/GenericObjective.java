package com.calculusmaster.pokecord.game.objectives.types;

import org.bson.Document;

public class GenericObjective extends AbstractObjective
{
    @Override
    public void read(Document data)
    {
        super.read(data);
    }

    @Override
    public Document serialize()
    {
        return super.serialize();
    }

    @Override
    public AbstractObjective generate()
    {
        return this;
    }

    @Override
    protected String getSpecificDescription()
    {
        return "";
    }
}
