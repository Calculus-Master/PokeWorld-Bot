package com.calculusmaster.pokecord.util.helpers;

import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.util.Mongo;
import com.mongodb.client.model.Filters;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataHelper
{
    public static final Map<String, List<String>> SERVER_PLAYERS = new HashMap<>();
    public static final List<List<String>> EV_LISTS = new ArrayList<>();

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
}
