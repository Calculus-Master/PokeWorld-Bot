package com.calculusmaster.pokecord.game.player.level.pmltasks;

import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.util.enums.StatisticType;

public class ResearchTasksPMLTask extends AbstractPMLTask
{
    private final int amount;

    public ResearchTasksPMLTask(int amount)
    {
        super(LevelTaskType.BOUNTIES);
        this.amount = amount;
    }

    @Override
    public boolean isCompleted(PlayerDataQuery p)
    {
        return p.getStatistics().get(StatisticType.TASKS_COMPLETED) >= this.amount;
    }

    @Override
    public String getProgressOverview(PlayerDataQuery p)
    {
        return p.getStatistics().get(StatisticType.TASKS_COMPLETED) + " / " + this.amount + " Research Tasks completed";
    }
}
