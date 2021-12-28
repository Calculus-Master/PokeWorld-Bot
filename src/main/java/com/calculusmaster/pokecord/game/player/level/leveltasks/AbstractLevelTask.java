package com.calculusmaster.pokecord.game.player.level.leveltasks;

import com.calculusmaster.pokecord.mongo.PlayerDataQuery;

public abstract class AbstractLevelTask
{
    private LevelTaskType type;

    public AbstractLevelTask(LevelTaskType type)
    {
        this.type = type;
    }

    public abstract boolean isCompleted(PlayerDataQuery p);

    public abstract String getProgressOverview(PlayerDataQuery p);

    public String getDesc()
    {
        return this.type.desc;
    }

    protected enum LevelTaskType
    {
        EXPERIENCE("Earn Pokemon Mastery Level Experience"),
        POKEMON("Collect Pokemon"),
        POKEMON_LEVEL("Have Pokemon at a Certain Level"),
        CREDITS("Earn Credits"),
        SHOP_PURCHASED("Buy Items from the Shop"),
        REDEEMS("Earn Redeems"),
        PVP_DUELS("Complete PvP Duels"),
        WILD_DUELS("Win Wild Pokemon Duels"),
        TRAINER_DUELS("Win Trainer Duels"),
        RAIDS("Win Raids"),
        ELITE_DUELS("Win Elite Trainer Duels"),
        BOUNTIES("Complete Bounties"),
        POKEMON_EVOLVED("Evolve Pokemon");

        private String desc;
        LevelTaskType(String desc)
        {
            this.desc = desc;
        }
    }
}
