package com.calculusmaster.pokecord.util.helpers;

import com.calculusmaster.pokecord.Pokecord;
import com.calculusmaster.pokecord.commands.economy.CommandShop;
import org.json.JSONObject;
import org.json.JSONTokener;

public class ConfigHelper
{
    public static void init()
    {
        try
        {
            JSONObject config = new JSONObject(new JSONTokener(Pokecord.class.getResourceAsStream("/config.json")));

            ThreadPoolHandler.THREAD_POOL_TYPE = config.getInt("thread_pool_type");
            CacheHelper.DYNAMIC_CACHING_ACTIVE = config.getBoolean("dynamic_caching");
            CommandShop.ITEM_COUNT_MIN = config.getJSONArray("item_counts").getInt(0);
            CommandShop.ITEM_COUNT_MAX = config.getJSONArray("item_counts").getInt(1);
            CommandShop.TM_COUNT = config.getInt("tm_count");
            CommandShop.TR_COUNT = config.getInt("tr_count");
            CommandShop.ZCRYSTAL_COUNT_MIN = config.getJSONArray("zcrystal_counts").getInt(0);
            CommandShop.ZCRYSTAL_COUNT_MAX = config.getJSONArray("zcrystal_counts").getInt(1);

            LoggerHelper.info(ConfigHelper.class, "Loaded config.json files!");
        }
        catch (Exception e)
        {
            LoggerHelper.warn(ConfigHelper.class, "Could not load config.json! Setting default values...");
            e.printStackTrace();

            ThreadPoolHandler.THREAD_POOL_TYPE = 1;
            CacheHelper.DYNAMIC_CACHING_ACTIVE = false;
            CommandShop.ITEM_COUNT_MAX = 10;
            CommandShop.ITEM_COUNT_MIN = 5;
            CommandShop.TM_COUNT = 10;
            CommandShop.TR_COUNT = 10;
            CommandShop.ZCRYSTAL_COUNT_MAX = 10;
            CommandShop.ZCRYSTAL_COUNT_MIN = 5;
        }
    }
}
