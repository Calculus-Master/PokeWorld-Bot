package com.calculusmaster.pokecord.util.helpers;

import com.calculusmaster.pokecord.commands.Commands;
import com.calculusmaster.pokecord.commands.economy.CommandShop;
import com.calculusmaster.pokecord.commands.pokemon.CommandTeam;
import com.calculusmaster.pokecord.game.bounties.components.Bounty;
import com.calculusmaster.pokecord.game.duel.players.Trainer;
import com.calculusmaster.pokecord.game.pokemon.PokemonEgg;
import com.calculusmaster.pokecord.util.Mongo;
import com.calculusmaster.pokecord.util.helpers.event.SpawnEventHelper;
import org.json.JSONObject;

public class ConfigHelper
{
    public static void init()
    {
        try
        {
            //JSONObject config = new JSONObject(new JSONTokener(Pokecord.class.getResourceAsStream("/config.json")));
            JSONObject config = new JSONObject(Mongo.ConfigData.find().first().toJson());

            ThreadPoolHandler.THREAD_POOL_TYPE = config.getInt("thread_pool_type");
            CacheHelper.DYNAMIC_CACHING_ACTIVE = config.getBoolean("dynamic_caching");
            CommandShop.ITEM_COUNT_MIN = config.getJSONArray("item_counts").getInt(0);
            CommandShop.ITEM_COUNT_MAX = config.getJSONArray("item_counts").getInt(1);
            CommandShop.TM_COUNT = config.getInt("tm_count");
            CommandShop.TR_COUNT = config.getInt("tr_count");
            CommandShop.ZCRYSTAL_COUNT_MIN = config.getJSONArray("zcrystal_counts").getInt(0);
            CommandShop.ZCRYSTAL_COUNT_MAX = config.getJSONArray("zcrystal_counts").getInt(1);
            Commands.COMMAND_THREAD_POOL = config.getBoolean("command_thread_pool");
            Bounty.MAX_BOUNTIES_HELD = config.getInt("bounty_max_held");
            Bounty.POKEPASS_EXP_YIELD = config.getInt("bounty_reward_exp");
            Bounty.BOUNTY_REWARD_MIN = config.getJSONArray("bounty_rewards").getInt(0);
            Bounty.BOUNTY_REWARD_MAX = config.getJSONArray("bounty_rewards").getInt(1);
            SpawnEventHelper.SPAWN_INTERVAL = config.getInt("spawn_event_interval");
            SpawnEventHelper.RAID_CHANCE = config.getInt("spawn_event_raid_chance");
            PokemonEgg.MAX_EGGS = config.getInt("egg_limit");
            CommandTeam.MAX_TEAM_SIZE = config.getInt("team_limit");
            Trainer.BASE_COUNT = config.getInt("base_daily_trainer_count");
            Trainer.COUNT_DEVIATION = config.getInt("daily_trainer_deviation");

            LoggerHelper.info(ConfigHelper.class, "Loaded config values!");
        }
        catch (Exception e)
        {
            LoggerHelper.reportError(ConfigHelper.class, "Could not access Config Database! Setting default values.", e);

            ThreadPoolHandler.THREAD_POOL_TYPE = 1;
            CacheHelper.DYNAMIC_CACHING_ACTIVE = false;
            CommandShop.ITEM_COUNT_MAX = 10;
            CommandShop.ITEM_COUNT_MIN = 5;
            CommandShop.TM_COUNT = 10;
            CommandShop.TR_COUNT = 10;
            CommandShop.ZCRYSTAL_COUNT_MAX = 10;
            CommandShop.ZCRYSTAL_COUNT_MIN = 5;
            Commands.COMMAND_THREAD_POOL = false;
            Bounty.MAX_BOUNTIES_HELD = 3;
            Bounty.POKEPASS_EXP_YIELD = 200;
            Bounty.BOUNTY_REWARD_MIN = 50;
            Bounty.BOUNTY_REWARD_MAX = 250;
            SpawnEventHelper.SPAWN_INTERVAL = 450;
            SpawnEventHelper.RAID_CHANCE = 1;
            PokemonEgg.MAX_EGGS = 9;
            CommandTeam.MAX_TEAM_SIZE = 12;
            Trainer.BASE_COUNT = 4;
            Trainer.COUNT_DEVIATION = 2;
        }
    }
}
