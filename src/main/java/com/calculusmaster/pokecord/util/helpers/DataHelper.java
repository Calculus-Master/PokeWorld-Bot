package com.calculusmaster.pokecord.util.helpers;

import com.calculusmaster.pokecord.game.enums.elements.Type;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.util.Mongo;
import com.mongodb.client.model.Filters;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DataHelper
{
    public static final Map<String, List<String>> SERVER_PLAYERS = new HashMap<>();
    public static final List<List<String>> EV_LISTS = new ArrayList<>();
    public static final Map<Type, List<String>> TYPE_LISTS = new HashMap<>();

    //Server Players
    public static void updateServerPlayers(Guild g)
    {
        SERVER_PLAYERS.put(g.getId(), new ArrayList<>());

        g.loadMembers();

        for(Member m : g.getMembers()) if(PlayerDataQuery.isRegistered(m.getId())) SERVER_PLAYERS.get(g.getId()).add(m.getId());
    }

    public static void removeServer(String ID)
    {
        SERVER_PLAYERS.remove(ID);
    }

    //EV Lists
    public static void createEVLists()
    {
        for(int i = 0; i < 6; i++) EV_LISTS.add(new ArrayList<>());

        Mongo.PokemonInfo.find(Filters.exists("ev")).forEach(d -> {
            List<Integer> j = d.getList("ev", Integer.class);
            for(int i = 0; i < 6; i++) if(j.get(i) > 0) EV_LISTS.get(i).add(d.getString("name"));
        });
    }

    //Type Lists
    public static void createTypeLists()
    {
        for(Type t : Type.values()) TYPE_LISTS.put(t, new ArrayList<>());

        Mongo.PokemonInfo.find().forEach(d -> {
            List<Type> types = d.getList("type", String.class).stream().distinct().map(Type::cast).collect(Collectors.toList());
            for(Type t : types) TYPE_LISTS.get(t).add(d.getString("name"));
        });
    }
}
