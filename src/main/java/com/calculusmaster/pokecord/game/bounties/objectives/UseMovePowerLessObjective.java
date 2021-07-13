package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;
import org.bson.Document;

import java.util.Random;

public class UseMovePowerLessObjective extends Objective
{
    private int power;

    public UseMovePowerLessObjective()
    {
        super(ObjectiveType.USE_MOVES_POWER_LESS, Objective.randomTargetAmount(10, 30));
        this.power = 10 * ((40 + new Random().nextInt(100)) / 10);
    }

    @Override
    public String getDesc()
    {
        return "Use any " + this.target + " Moves with base power less than or equal to " + this.power;
    }

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

    public UseMovePowerLessObjective setPower(int power)
    {
        this.power = power;
        return this;
    }
}
