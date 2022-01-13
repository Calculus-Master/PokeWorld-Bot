package com.calculusmaster.pokecord.game.player.level.leveltasks;

import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.util.enums.PlayerStatistic;

public class EliteLevelTask extends AbstractLevelTask
{
    private final int amount;

    public EliteLevelTask(int amount)
    {
        super(LevelTaskType.ELITE_DUELS);
        this.amount = amount;
    }

    @Override
    public boolean isCompleted(PlayerDataQuery p)
    {
        return p.getStatistics().get(PlayerStatistic.ELITE_DUELS_WON) >= this.amount;
    }

    @Override
    public String getProgressOverview(PlayerDataQuery p)
    {
        return p.getStatistics().get(PlayerStatistic.ELITE_DUELS_WON) + " / " + this.amount + " Elite Trainers defeated";
    }
}
