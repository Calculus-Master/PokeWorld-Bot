package com.calculusmaster.pokecord.util.custom;

import com.calculusmaster.pokecord.game.enums.elements.Stat;
import org.json.JSONArray;

import java.util.Map;

public class StatIntMap extends ExtendedHashMap<Stat, Integer>
{
    public StatIntMap()
    {
        for(Stat s : Stat.values()) this.put(s, 0);
    }

    public static StatIntMap from(String condensed)
    {
        StatIntMap map = new StatIntMap();
        for(Stat s : Stat.values()) map.put(s, Integer.parseInt(condensed.split("-")[s.ordinal()]));
        return map;
    }

    public static <K extends Stat, V extends Integer> StatIntMap from(Map<K, V> map)
    {
        StatIntMap result = new StatIntMap();
        for(K s : map.keySet()) result.put(s, map.get(s));
        return result;
    }

    //For EV Yield primarily
    public static StatIntMap from(JSONArray json)
    {
        StatIntMap map = new StatIntMap();
        for(int i = 0; i < json.length(); i++) map.put(Stat.values()[i], json.getInt(i));
        return map;
    }

    public static String to(StatIntMap map)
    {
        StringBuilder condensed = new StringBuilder();
        for(Stat s : Stat.values()) condensed.append(map.get(s)).append("-");
        return condensed.deleteCharAt(condensed.length() - 1).toString();
    }

    public StatIntMap increase(Stat s)
    {
        this.put(s, this.getOrDefault(s, 0) + 1);
        return this;
    }
}
