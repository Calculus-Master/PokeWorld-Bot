package com.calculusmaster.pokecord.game.player.level.pmltasks;

import com.calculusmaster.pokecord.mongo.PlayerData;
import com.calculusmaster.pokecord.util.enums.StatisticType;

public class WildPMLTask extends AbstractPMLTask
{
    private final int amount;

    public WildPMLTask(int amount)
    {
        super(LevelTaskType.WILD_DUELS);
        this.amount = amount;
    }

    @Override
    public boolean isCompleted(PlayerData p)
    {
        return p.getStatistics().get(StatisticType.WILD_DUELS_WON) >= this.amount;
    }

    @Override
    public String getProgressOverview(PlayerData p)
    {
        return p.getStatistics().get(StatisticType.WILD_DUELS_WON) + " / " + this.amount + " Wild Pokemon defeated";
    }
}
