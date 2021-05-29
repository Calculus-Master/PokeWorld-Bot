package com.calculusmaster.pokecord.util;

import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Global
{
    public static final List<String> STARTERS = Arrays.asList("bulbasaur", "charmander", "squirtle", "chikorita", "quilava", "totodile", "treecko", "torchic", "mudkip", "turtwig", "chimchar", "piplup", "snivy", "tepig", "oshawott", "chespin", "fennekin", "froakie", "rowlet", "litten", "popplio"); //TODO: Add the remaining starters and update this list
    public static final List<String> POKEMON = new ArrayList<>();

    public static final Map<String, List<Pokemon>> POKEMON_LISTS = new HashMap<>();

    public static List<Pokemon> getPokemonList(String playerID)
    {
        PlayerDataQuery p = new PlayerDataQuery(playerID);
        List<Pokemon> list = new LinkedList<>();
        for(int i = 0; i < p.getPokemonList().length(); i++) list.add(Pokemon.buildCore(p.getPokemonList().getString(i), i));
        return list;
    }

    public static List<Pokemon.Base> getBaseList(String playerID)
    {
        PlayerDataQuery p = new PlayerDataQuery(playerID);
        List<Pokemon.Base> list = new LinkedList<>();
        for(int i = 0; i < p.getPokemonList().length(); i++) list.add(Pokemon.build(p.getPokemonList().getString(i), i));
        return list;
    }

    public static void updatePokemonList(String playerID)
    {
        if(playerID.equals("")) return;
        List<Pokemon> list = Global.getPokemonList(playerID);
        POKEMON_LISTS.put(playerID, list);
    }

    public static void updatePokemonInList(String UUID)
    {
        String playerID = "";
        //System.out.println(POKEMON_LISTS);
        for(String s : POKEMON_LISTS.keySet()) if(POKEMON_LISTS.get(s).stream().anyMatch(p -> p.getUUID().equals(UUID))) playerID = s;
        updatePokemonList(playerID);
    }

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
