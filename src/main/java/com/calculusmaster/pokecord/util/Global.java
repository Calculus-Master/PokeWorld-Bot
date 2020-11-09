package com.calculusmaster.pokecord.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Global
{
    public static final List<String> STARTERS = Arrays.asList("bulbasaur", "charmander", "squirtle"); //TODO: Add the remaining starters and update this list
    public static final List<String> POKEMON = new ArrayList<>();

    public static void logInfo(Class<?> clazz, String method, String msg)
    {
        LoggerFactory.getLogger(clazz).info("#" + method + ": " + msg);
    }

    public static boolean isStarter(String s)
    {
        return STARTERS.contains(s.toLowerCase());
    }

    public static Object getEnumFromString(Object[] enumValues, String s)
    {
        for(Object o : enumValues) if(s.toLowerCase().equals(o.toString().toLowerCase())) return o;
        return null;
    }

    public static String normalCase(String s)
    {
        StringBuilder sb = new StringBuilder();
        for(String str : s.replaceAll("-", " ").split("\\s+")) sb.append(str.substring(0, 1).toUpperCase()).append(str.substring(1).toLowerCase()).append(" ");
        return s.contains("-") ? sb.toString().replaceAll("\\s", "-").trim() : sb.toString().trim();
    }
}
