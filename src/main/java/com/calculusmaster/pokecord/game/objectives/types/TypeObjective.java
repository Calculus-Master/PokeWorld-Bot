package com.calculusmaster.pokecord.game.objectives.types;

import com.calculusmaster.pokecord.game.enums.elements.Type;
import org.bson.Document;

import java.util.Random;

public class TypeObjective extends AbstractObjective
{
    private Type type;

    @Override
    public void read(Document data)
    {
        super.read(data);
        this.type = Type.valueOf(data.getString("type"));
    }

    @Override
    public Document serialize()
    {
        return super.serialize().append("type", this.type.toString());
    }

    @Override
    public AbstractObjective generate()
    {
        this.type = Type.values()[new Random().nextInt(Type.values().length)];
        return this;
    }

    @Override
    protected String getSpecificDescription()
    {
        return "Type: " + this.type.getStyledName() + ".";
    }

    public Type getType()
    {
        return this.type;
    }
}
