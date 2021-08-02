package com.calculusmaster.pokecord.game.player.leveltasks;

import com.calculusmaster.pokecord.mongo.PlayerDataQuery;

public class ExperienceLevelTask extends AbstractLevelTask
{
    private final int exp;

    public ExperienceLevelTask(int exp)
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
    public String getDesc()
    {
        return "Experience: " + this.exp;
    }
}
