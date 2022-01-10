package com.calculusmaster.pokecord.game.player.level.leveltasks;

import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.util.enums.PlayerStatistic;

public class WildLevelTask extends AbstractLevelTask
{
    private final int amount;

    public WildLevelTask(int amount)
    {
        super(LevelTaskType.WILD_DUELS);
        this.amount = amount;
    }

    @Override
    public boolean isCompleted(PlayerDataQuery p)
    {
        return p.getStatistics().get(PlayerStatistic.WILD_DUELS_WON) >= this.amount;
    }

    @Override
    public String getProgressOverview(PlayerDataQuery p)
    {
        return p.getStatistics().get(PlayerStatistic.WILD_DUELS_WON) + " / " + this.amount + " Wild Pokemon defeated";
    }
}
