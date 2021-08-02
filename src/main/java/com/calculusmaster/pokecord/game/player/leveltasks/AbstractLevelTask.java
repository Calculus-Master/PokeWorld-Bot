package com.calculusmaster.pokecord.game.player.leveltasks;

import com.calculusmaster.pokecord.mongo.PlayerDataQuery;

public abstract class AbstractLevelTask
{
    private LevelTaskType type;

    public AbstractLevelTask(LevelTaskType type)
    {
        this.type = type;
    }

    public abstract boolean isCompleted(PlayerDataQuery p);

    public abstract String getDesc();

    protected enum LevelTaskType
    {
        EXPERIENCE,
        POKEMON,
        CREDITS,
        REDEEMS,
        PVP_DUELS,
        WILD_DUELS,
        TRAINER_DUELS,
        RAIDS,
        ELITE_DUELS,
        BOUNTIES,
        BREEDING;
    }
}
