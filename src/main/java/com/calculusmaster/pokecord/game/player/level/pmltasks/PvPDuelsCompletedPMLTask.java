package com.calculusmaster.pokecord.game.player.level.pmltasks;

import com.calculusmaster.pokecord.mongo.PlayerData;
import com.calculusmaster.pokecord.util.enums.StatisticType;

public class PvPDuelsCompletedPMLTask extends AbstractPMLTask
{
    private final int amount;

    public PvPDuelsCompletedPMLTask(int amount)
    {
        super(LevelTaskType.PVP_DUELS);
        this.amount = amount;
    }

    @Override
    public boolean isCompleted(PlayerData p)
    {
        return p.getStatistics().get(StatisticType.PVP_DUELS_COMPLETED) >= this.amount;
    }

    @Override
    public String getProgressOverview(PlayerData p)
    {
        return p.getStatistics().get(StatisticType.PVP_DUELS_COMPLETED) + " / " + this.amount + " Players dueled";
    }
}
