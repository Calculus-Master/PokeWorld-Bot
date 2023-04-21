package com.calculusmaster.pokecord.util.helpers;

import com.calculusmaster.pokecord.game.enums.elements.Type;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonEntity;
import com.calculusmaster.pokecord.mongo.PlayerData;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import java.util.*;

public class DataHelper
{
    public static final Map<String, List<String>> SERVER_PLAYERS = new HashMap<>();
    public static final List<EnumSet<PokemonEntity>> EV_LISTS = new ArrayList<>();
    public static final Map<Type, EnumSet<PokemonEntity>> TYPE_LISTS = new HashMap<>();

    //Server Players
    public static void updateServerPlayers(Guild g)
    {
        SERVER_PLAYERS.put(g.getId(), new ArrayList<>());

        for(Member m : g.loadMembers().get()) if(PlayerData.isRegistered(m.getId())) SERVER_PLAYERS.get(g.getId()).add(m.getId());
    }

    public static void addServerPlayer(Guild g, User u)
    {
        if(SERVER_PLAYERS.containsKey(g.getId())) SERVER_PLAYERS.get(g.getId()).add(u.getId());
    }

    public static void removeServer(String ID)
    {
        SERVER_PLAYERS.remove(ID);
    }

    //EV Lists
    public static void createEVLists()
    {
        for(int i = 0; i < 6; i++) EV_LISTS.add(EnumSet.noneOf(PokemonEntity.class));

        Arrays.stream(PokemonEntity.values()).map(PokemonEntity::data).forEach(data -> data.getEVYield().get().forEach((key, value) -> {
            if(value > 0) EV_LISTS.get(key.ordinal()).add(data.getEntity());
        }));
    }

    //Type Lists
    public static void createTypeLists()
    {
        for(Type t : Type.values()) TYPE_LISTS.put(t, EnumSet.noneOf(PokemonEntity.class));
        Arrays.stream(PokemonEntity.values()).map(PokemonEntity::data).forEach(data -> data.getTypes().stream().distinct().forEach(t -> TYPE_LISTS.get(t).add(data.getEntity())));
    }
}
