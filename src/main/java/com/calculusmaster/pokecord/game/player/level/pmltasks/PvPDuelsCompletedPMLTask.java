package com.calculusmaster.pokecord.game.player.level.pmltasks;

import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.util.enums.PlayerStatistic;

public class PvPDuelsCompletedPMLTask extends AbstractPMLTask
{
    private final int amount;

    public PvPDuelsCompletedPMLTask(int amount)
    {
        super(LevelTaskType.PVP_DUELS);
        this.amount = amount;
    }

    @Override
    public boolean isCompleted(PlayerDataQuery p)
    {
        return p.getStatistics().get(PlayerStatistic.PVP_DUELS_COMPLETED) >= this.amount;
    }

    @Override
    public String getProgressOverview(PlayerDataQuery p)
    {
        return p.getStatistics().get(PlayerStatistic.PVP_DUELS_COMPLETED) + " / " + this.amount + " Players dueled";
    }
}
