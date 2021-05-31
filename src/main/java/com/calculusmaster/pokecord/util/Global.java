package com.calculusmaster.pokecord.util;

import org.bson.Document;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Global
{
    public static final List<String> STARTERS = Arrays.asList("bulbasaur", "charmander", "squirtle", "chikorita", "quilava", "totodile", "treecko", "torchic", "mudkip", "turtwig", "chimchar", "piplup", "snivy", "tepig", "oshawott", "chespin", "fennekin", "froakie", "rowlet", "litten", "popplio"); //TODO: Add the remaining starters and update this list
    public static final List<String> POKEMON = new ArrayList<>();

    public static void logInfo(Class<?> clazz, String method, String msg)
    {
        LoggerFactory.getLogger(clazz).info("#" + method + ": " + msg);
    }

    public static void logTime(Class<?> clazz, String command, long timeI, long timeF, OffsetDateTime timestamp)
    {
        LoggerFactory.getLogger(clazz).info(command + " took " + (timeF - timeI) + " ms to complete!");
        //addPerformanceEntry(command, timeI, timeF, timestamp);
    }

    private static void addPerformanceEntry(String command, long timeI, long timeF, OffsetDateTime timestamp)
    {
        Document data = new Document("command", command).append("time", timeF - timeI).append("timestamp", timestamp.format(DateTimeFormatter.RFC_1123_DATE_TIME));
        Mongo.PerformanceData.insertOne(data);
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
        if(s.toLowerCase().startsWith("nidoran"))
        {
            return s.toLowerCase().contains("f") ? "NidoranF" : "NidoranM";
        }

        StringBuilder sb = new StringBuilder();
        for(String str : s.replaceAll("-", " ").split("\\s+")) sb.append(str.substring(0, 1).toUpperCase()).append(str.substring(1).toLowerCase()).append(" ");
        return s.contains("-") ? sb.toString().replaceAll("\\s", "-").trim() : sb.toString().trim();
    }
}
