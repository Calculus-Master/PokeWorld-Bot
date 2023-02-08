package com.calculusmaster.pokecord.game.player.level.pmltasks;

import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.util.enums.PlayerStatistic;

public class ElitePMLTask extends AbstractPMLTask
{
    private final int amount;

    public ElitePMLTask(int amount)
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
