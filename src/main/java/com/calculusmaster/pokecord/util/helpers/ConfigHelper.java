package com.calculusmaster.pokecord.util.helpers;

import com.calculusmaster.pokecord.commandslegacy.CommandsLegacy;
import com.calculusmaster.pokecord.game.objectives.ResearchTask;
import com.calculusmaster.pokecord.game.player.PlayerResearchTasks;
import com.calculusmaster.pokecord.game.player.PlayerTeam;
import com.calculusmaster.pokecord.game.player.level.MasteryLevelManager;
import com.calculusmaster.pokecord.game.pokemon.evolution.PokemonEgg;
import com.calculusmaster.pokecord.game.world.PokeWorldShop;
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
            PokeWorldShop.ITEM_COUNTS = new int[]{config.getList("item_counts", Integer.class).get(0), config.getList("item_counts", Integer.class).get(1)};
            PokeWorldShop.TM_COUNTS = new int[]{config.getList("tm_counts", Integer.class).get(0), config.getList("tm_counts", Integer.class).get(1)};
            PokeWorldShop.Z_CRYSTAL_COUNTS = new int[]{config.getList("zcrystal_counts", Integer.class).get(0), config.getList("zcrystal_counts", Integer.class).get(1)};
            CommandsLegacy.COMMAND_THREAD_POOL = config.getBoolean("command_thread_pool");
            PlayerResearchTasks.MAX_TASKS = config.getInteger("bounty_max_held");
            ResearchTask.TASK_REWARD_MIN = config.getList("bounty_rewards", Integer.class).get(0);
            ResearchTask.TASK_REWARD_MAX = config.getList("bounty_rewards", Integer.class).get(1);
            SpawnEventHelper.SPAWN_INTERVAL = config.getInteger("spawn_event_interval");
            SpawnEventHelper.RAID_CHANCE = config.getInteger("spawn_event_raid_chance");
            PokemonEgg.MAX_EGGS = config.getInteger("egg_limit");
            PlayerTeam.MAX_TEAM_SIZE = config.getInteger("team_limit");
            PlayerTeam.MAX_SLOTS = config.getInteger("team_slots");
            MasteryLevelManager.ACTIVE = config.getBoolean("mastery_levels");

            LoggerHelper.info(ConfigHelper.class, "Loaded config values!");
        }
        catch (Exception e)
        {
            LoggerHelper.reportError(ConfigHelper.class, "Could not access Config Database! Setting default values.", e);

            ThreadPoolHandler.THREAD_POOL_TYPE = 1;
            CacheHelper.DYNAMIC_CACHING_ACTIVE = false;
            PokeWorldShop.ITEM_COUNTS = new int[]{5, 10};
            PokeWorldShop.TM_COUNTS = new int[]{10, 12};
            PokeWorldShop.Z_CRYSTAL_COUNTS = new int[]{5, 10};
            CommandsLegacy.COMMAND_THREAD_POOL = false;
            PlayerResearchTasks.MAX_TASKS = 6;
            ResearchTask.TASK_REWARD_MIN = 50;
            ResearchTask.TASK_REWARD_MAX = 250;
            SpawnEventHelper.SPAWN_INTERVAL = 450;
            SpawnEventHelper.RAID_CHANCE = 1;
            PokemonEgg.MAX_EGGS = 9;
            PlayerTeam.MAX_TEAM_SIZE = 6;
            PlayerTeam.MAX_SLOTS = 5;
        }
    }
}
