package com.calculusmaster.pokecord.game.player.level.pmltasks;

import com.calculusmaster.pokecord.mongo.PlayerData;

public class ZCrystalsAcquiredPMLTask extends AbstractPMLTask
{
    private final int amount;

    public ZCrystalsAcquiredPMLTask(int amount)
    {
        super(LevelTaskType.ZCRYSTALS_ACQUIRED);
        this.amount = amount;
    }

    @Override
    public boolean isCompleted(PlayerData p)
    {
        return p.getInventory().getZCrystals().size() >= this.amount;
    }

    @Override
    public String getProgressOverview(PlayerData p)
    {
        return p.getInventory().getZCrystals().size() + " / " + this.amount + " Z-Crystals Acquired";
    }
}
