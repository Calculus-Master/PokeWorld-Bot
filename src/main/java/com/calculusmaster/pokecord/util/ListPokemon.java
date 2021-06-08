package com.calculusmaster.pokecord.util;

import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.elements.Type;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ListPokemon
{
    private JSONObject genericJSON;
    private JSONObject specificJSON;

    private String UUID;
    private String name;
    private int number;
    private int level;
    private boolean shiny;
    private Type[] type;
    private Map<Stat, Integer> IVs = new HashMap<>();

    public ListPokemon(String UUID, int num)
    {
        Document specific = Mongo.PokemonData.find(Filters.eq("UUID", UUID)).first();
        if(specific == null) return;

        Document generic = Mongo.PokemonInfo.find(Filters.eq("name", specific.getString("name"))).first();

        this.UUID = UUID;
        this.name = specific.getString("name");
        this.number = num + 1;
        this.level = specific.getInteger("level");
        this.shiny = specific.getBoolean("shiny");
        this.type = new Type[]{Type.cast(generic.getList("type", String.class).get(0)), Type.cast(generic.getList("type", String.class).get(0))};
        for(int i = 0; i < 6; i++) this.IVs.put(Stat.values()[i], Integer.parseInt(specific.getString("ivs").split("-")[i]));
    }

    public static void main(String[] args) {
        long totalOld = 0;
        long totalNew = 0;
        for(int i = 0; i < 50; i++)
        {
            totalOld += oldObject();
            totalNew += newObject();
        }

        System.out.println("Old: " + totalOld);
        System.out.println("New: " + totalNew);
    }

    private static long oldObject()
    {
        long i = System.currentTimeMillis();
        Pokemon.buildCore("mctg-tvx8-v0vf-m0vs-6uhq-4wsa", -1);
        return System.currentTimeMillis() - i;
    }

    private static long newObject()
    {
        long i = System.currentTimeMillis();
        new ListPokemon("mctg-tvx8-v0vf-m0vs-6uhq-4wsa", -1);
        return System.currentTimeMillis() - i;
    }
}
