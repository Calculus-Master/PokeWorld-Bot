package com.calculusmaster.pokecord.game.player.level.leveltasks;

import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.util.enums.PlayerStatistic;

public class BountiesLevelTask extends AbstractLevelTask
{
    private final int amount;

    public BountiesLevelTask(int amount)
    {
        super(LevelTaskType.BOUNTIES);
        this.amount = amount;
    }

    @Override
    public boolean isCompleted(PlayerDataQuery p)
    {
        return p.getStatistics().get(PlayerStatistic.BOUNTIES_COMPLETED) >= this.amount;
    }

    @Override
    public String getProgressOverview(PlayerDataQuery p)
    {
        return p.getStatistics().get(PlayerStatistic.BOUNTIES_COMPLETED) + " / " + this.amount + " Bounties completed";
    }
}
