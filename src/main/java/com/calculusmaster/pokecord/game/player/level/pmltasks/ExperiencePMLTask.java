package com.calculusmaster.pokecord.game.player.level.pmltasks;

import com.calculusmaster.pokecord.mongo.PlayerDataQuery;

public class ExperiencePMLTask extends AbstractPMLTask
{
    private final int exp;

    public ExperiencePMLTask(int exp)
    {
        super(LevelTaskType.EXPERIENCE);
        this.exp = exp;
    }

    @Override
    public boolean isCompleted(PlayerDataQuery p)
    {
        return p.getExp() >= this.exp;
    }

    @Override
    public String getProgressOverview(PlayerDataQuery p)
    {
        return p.getExp() + " / " + this.exp + " XP";
    }
}
