package com.calculusmaster.pokecord.game.objectives.types;

import com.calculusmaster.pokecord.game.enums.elements.Stat;
import org.bson.Document;

import java.util.Random;

public abstract class StatObjective extends AbstractObjective
{
    private Stat stat;

    @Override
    public void read(Document data)
    {
        super.read(data);
        this.stat = Stat.valueOf(data.getString("stat"));
    }

    @Override
    public Document serialize()
    {
        return super.serialize().append("stat", this.stat.toString());
    }

    @Override
    public AbstractObjective generate()
    {
        this.stat = Stat.values()[new Random().nextInt(Stat.values().length)];
        return this;
    }

    public Stat getStat()
    {
        return this.stat;
    }

    public void setStat(Stat stat)
    {
        this.stat = stat;
    }
}
