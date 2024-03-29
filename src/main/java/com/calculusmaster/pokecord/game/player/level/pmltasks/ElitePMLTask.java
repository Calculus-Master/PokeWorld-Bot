package com.calculusmaster.pokecord.game.player.level.pmltasks;

import com.calculusmaster.pokecord.mongo.PlayerData;
import com.calculusmaster.pokecord.util.enums.StatisticType;

public class ElitePMLTask extends AbstractPMLTask
{
    private final int amount;

    public ElitePMLTask(int amount)
    {
        super(LevelTaskType.ELITE_DUELS);
        this.amount = amount;
    }

    @Override
    public boolean isCompleted(PlayerData p)
    {
        return p.getStatistics().get(StatisticType.ELITE_DUELS_WON) >= this.amount;
    }

    @Override
    public String getProgressOverview(PlayerData p)
    {
        return p.getStatistics().get(StatisticType.ELITE_DUELS_WON) + " / " + this.amount + " Elite Trainers defeated";
    }
}
