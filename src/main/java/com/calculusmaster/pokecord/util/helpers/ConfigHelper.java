package com.calculusmaster.pokecord.util.helpers;

import com.calculusmaster.pokecord.commandslegacy.CommandsLegacy;
import com.calculusmaster.pokecord.commandslegacy.economy.CommandLegacyShop;
import com.calculusmaster.pokecord.commandslegacy.pokemon.CommandLegacyTeam;
import com.calculusmaster.pokecord.game.bounties.Bounty;
import com.calculusmaster.pokecord.game.player.level.MasteryLevelManager;
import com.calculusmaster.pokecord.game.pokemon.evolution.PokemonEgg;
import com.calculusmaster.pokecord.mongo.Mongo;
import com.calculusmaster.pokecord.util.helpers.event.SpawnEventHelper;
import org.bson.Document;

import java.util.List;
import java.util.Objects;

public class ConfigHelper
{
    public static boolean DEV_MODE;
    public static List<String> DEV_PLAYERS;

    public static void init()
    {
        try
        {
            Document config = Objects.requireNonNull(Mongo.ConfigData.find().first());

            ThreadPoolHandler.THREAD_POOL_TYPE = config.getInteger("thread_pool_type");
            CacheHelper.DYNAMIC_CACHING_ACTIVE = config.getBoolean("dynamic_caching");
            CommandLegacyShop.ITEM_COUNT_MIN = config.getList("item_counts", Integer.class).get(0);
            CommandLegacyShop.ITEM_COUNT_MAX = config.getList("item_counts", Integer.class).get(1);
            CommandLegacyShop.TM_COUNT = config.getInteger("tm_count");
            CommandLegacyShop.ZCRYSTAL_COUNT_MIN = config.getList("zcrystal_counts", Integer.class).get(0);
            CommandLegacyShop.ZCRYSTAL_COUNT_MAX = config.getList("zcrystal_counts", Integer.class).get(1);
            CommandsLegacy.COMMAND_THREAD_POOL = config.getBoolean("command_thread_pool");
            Bounty.MAX_BOUNTIES_HELD = config.getInteger("bounty_max_held");
            Bounty.BOUNTY_REWARD_MIN = config.getList("bounty_rewards", Integer.class).get(0);
            Bounty.BOUNTY_REWARD_MAX = config.getList("bounty_rewards", Integer.class).get(1);
            SpawnEventHelper.SPAWN_INTERVAL = config.getInteger("spawn_event_interval");
            SpawnEventHelper.RAID_CHANCE = config.getInteger("spawn_event_raid_chance");
            PokemonEgg.MAX_EGGS = config.getInteger("egg_limit");
            CommandLegacyTeam.MAX_TEAM_SIZE = config.getInteger("team_limit");
            CommandLegacyTeam.MAX_SLOTS = config.getInteger("team_slots");
            MasteryLevelManager.ACTIVE = config.getBoolean("mastery_levels");

            LoggerHelper.info(ConfigHelper.class, "Loaded config values!");
        }
        catch (Exception e)
        {
            LoggerHelper.reportError(ConfigHelper.class, "Could not access Config Database! Setting default values.", e);

            ThreadPoolHandler.THREAD_POOL_TYPE = 1;
            CacheHelper.DYNAMIC_CACHING_ACTIVE = false;
            CommandLegacyShop.ITEM_COUNT_MAX = 10;
            CommandLegacyShop.ITEM_COUNT_MIN = 5;
            CommandLegacyShop.TM_COUNT = 10;
            CommandLegacyShop.ZCRYSTAL_COUNT_MAX = 10;
            CommandLegacyShop.ZCRYSTAL_COUNT_MIN = 5;
            CommandsLegacy.COMMAND_THREAD_POOL = false;
            Bounty.MAX_BOUNTIES_HELD = 3;
            Bounty.BOUNTY_REWARD_MIN = 50;
            Bounty.BOUNTY_REWARD_MAX = 250;
            SpawnEventHelper.SPAWN_INTERVAL = 450;
            SpawnEventHelper.RAID_CHANCE = 1;
            PokemonEgg.MAX_EGGS = 9;
            CommandLegacyTeam.MAX_TEAM_SIZE = 6;
            CommandLegacyTeam.MAX_SLOTS = 5;
        }
    }
}
