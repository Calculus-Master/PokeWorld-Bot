package com.calculusmaster.pokecord.game.player.level.leveltasks;

import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.util.enums.PlayerStatistic;

public class PvPLevelTask extends AbstractLevelTask
{
    private final int amount;

    public PvPLevelTask(int amount)
    {
        super(LevelTaskType.PVP_DUELS);
        this.amount = amount;
    }

    @Override
    public boolean isCompleted(PlayerDataQuery p)
    {
        return p.getStats().get(PlayerStatistic.PVP_DUELS_WON) >= this.amount;
    }

    @Override
    public String getProgressOverview(PlayerDataQuery p)
    {
        return p.getStats().get(PlayerStatistic.PVP_DUELS_WON) + " / " + this.amount + " Players defeated";
    }
}
