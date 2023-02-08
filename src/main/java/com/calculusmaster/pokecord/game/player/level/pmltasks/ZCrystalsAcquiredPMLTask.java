package com.calculusmaster.pokecord.game.player.level.pmltasks;

import com.calculusmaster.pokecord.mongo.PlayerDataQuery;

public class ZCrystalsAcquiredPMLTask extends AbstractPMLTask
{
    private final int amount;

    public ZCrystalsAcquiredPMLTask(int amount)
    {
        super(LevelTaskType.ZCRYSTALS_ACQUIRED);
        this.amount = amount;
    }

    @Override
    public boolean isCompleted(PlayerDataQuery p)
    {
        return p.getZCrystalList().size() >= this.amount;
    }

    @Override
    public String getProgressOverview(PlayerDataQuery p)
    {
        return p.getZCrystalList().size() + " / " + this.amount + " Z-Crystals Acquired";
    }
}
