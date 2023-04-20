package com.calculusmaster.pokecord.util.helpers;

import com.calculusmaster.pokecord.game.enums.functional.Achievement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CacheHelper
{
    //If true, pokemon list caching will be done the first time the player uses p!p, rather than all at the bot init (to lower load times)
    public static boolean DYNAMIC_CACHING_ACTIVE;

    //Stored Data Type: Player IDs
    public static final Map<Achievement, List<String>> ACHIEVEMENT_CACHE = new HashMap<>();

    public static void initAchievementCache()
    {
        for(Achievement a : Achievement.values()) ACHIEVEMENT_CACHE.put(a, new ArrayList<>());
    }
}
