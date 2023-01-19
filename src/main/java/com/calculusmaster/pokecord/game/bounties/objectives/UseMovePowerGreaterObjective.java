package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.ObjectiveType;
import com.calculusmaster.pokecord.game.bounties.objectives.core.AbstractPowerObjective;

import java.util.Random;

public class UseMovePowerGreaterObjective extends AbstractPowerObjective
{
    public UseMovePowerGreaterObjective()
    {
        super(ObjectiveType.USE_MOVES_POWER_GREATER);
    }

    @Override
    protected void setRandomPower()
    {
        this.power = 10 * ((40 + new Random().nextInt(100)) / 10);
    }

    @Override
    public String getDesc()
    {
        return "Use any " + this.target + " Moves with base power greater than or equal to " + this.power;
    }
}
