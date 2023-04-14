package com.calculusmaster.pokecord.game.objectives.types;

import org.bson.Document;

import java.util.Random;

public class PowerObjective extends AbstractObjective
{
    private int power;

    @Override
    public void read(Document data)
    {
        super.read(data);
        this.power = data.getInteger("power");
    }

    @Override
    public Document serialize()
    {
        return super.serialize().append("power", this.power);
    }

    @Override
    public AbstractObjective generate()
    {
        int[] options = {50, 60, 70, 80, 90, 100};
        this.power = options[new Random().nextInt(options.length)];
        return this;
    }

    @Override
    protected String getSpecificDescription()
    {
        return "Power: " + this.power + ".";
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
