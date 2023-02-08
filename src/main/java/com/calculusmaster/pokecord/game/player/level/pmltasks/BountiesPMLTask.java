package com.calculusmaster.pokecord.game.player.level.pmltasks;

import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.util.enums.PlayerStatistic;

public class BountiesPMLTask extends AbstractPMLTask
{
    private final int amount;

    public BountiesPMLTask(int amount)
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
