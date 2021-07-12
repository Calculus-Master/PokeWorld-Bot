package com.calculusmaster.pokecord.util.helpers;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.nio.file.Paths;

public class ConfigHelper
{
    public static void init()
    {
        try
        {
            URL resource = ConfigHelper.class.getResource("config.json");
            File f = Paths.get(resource.toURI()).toFile();
            JSONObject config = new JSONObject(new JSONTokener(new FileInputStream(f)));

            ThreadPoolHandler.THREAD_POOL_TYPE = config.getInt("thread_pool_type");
            CacheHelper.DYNAMIC_CACHING_ACTIVE = config.getBoolean("dynamic_caching");

            LoggerHelper.info(ConfigHelper.class, "Loaded config.json files!");
        }
        catch (Exception e)
        {
            LoggerHelper.warn(ConfigHelper.class, "Could not load config.json! Setting default values...");
            e.printStackTrace();

            ThreadPoolHandler.THREAD_POOL_TYPE = 1;
            CacheHelper.DYNAMIC_CACHING_ACTIVE = false;
        }
    }
}
