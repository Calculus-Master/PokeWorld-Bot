package com.calculusmaster.pokecord.game.player.level.pmltasks;

import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.util.enums.PlayerStatistic;

public class WildPMLTask extends AbstractPMLTask
{
    private final int amount;

    public WildPMLTask(int amount)
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
