package com.calculusmaster.pokecord.game.player.level.leveltasks;

import com.calculusmaster.pokecord.mongo.PlayerDataQuery;

public class CreditsLevelTask extends AbstractLevelTask
{
    private final int amount;

    public CreditsLevelTask(int amount)
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
