package com.calculusmaster.pokecord.util.helpers;

import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataHelper
{
    public static final Map<String, List<String>> SERVER_PLAYERS = new HashMap<>();

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
}
