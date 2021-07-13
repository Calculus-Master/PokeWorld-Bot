package com.calculusmaster.pokecord.game.bounties.objectives;

import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import org.bson.Document;

import java.util.Random;

public class EarnEVsStatObjective extends Objective
{
    private Stat stat;

    public EarnEVsStatObjective()
    {
        super(ObjectiveType.EARN_EVS_STAT, Objective.randomTargetAmount(6, 30));
        this.stat = Stat.values()[new Random().nextInt(Stat.values().length)];
    }

    @Override
    public String getDesc()
    {
        return "Earn " + this.target + " EVs in " + this.stat.name;
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

    public EarnEVsStatObjective setStat(String stat)
    {
        this.stat = Stat.cast(stat);
        return this;
    }
}
