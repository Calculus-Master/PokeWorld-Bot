package com.calculusmaster.pokecord.game.player.level.pmltasks;

import com.calculusmaster.pokecord.mongo.PlayerDataQuery;

public class CreditsPMLTask extends AbstractPMLTask
{
    private final int amount;

    public CreditsPMLTask(int amount)
    {
        super(LevelTaskType.CREDITS);
        this.amount = amount;
    }

    @Override
    public boolean isCompleted(PlayerDataQuery p)
    {
        return p.getCredits() >= this.amount;
    }

    @Override
    public String getProgressOverview(PlayerDataQuery p)
    {
        return p.getCredits() + " / " + this.amount + " Credits";
    }
}
