package com.calculusmaster.pokecord.util;

import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.elements.Type;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
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

        List<String> types = generic.getList("type", String.class);
        this.type = new Type[]{Type.cast(types.get(0)), Type.cast(types.get(1))};

        for(int i = 0; i < 6; i++) this.IVs.put(Stat.values()[i], Integer.parseInt(specific.getString("ivs").split("-")[i]));
    }

    public String getUUID()
    {
        return this.UUID;
    }

    public String getName()
    {
        return this.name;
    }

    public int getNumber()
    {
        return this.number;
    }

    public int getLevel()
    {
        return this.level;
    }

    public boolean isShiny()
    {
        return this.shiny;
    }

    public Type[] getType()
    {
        return this.type;
    }

    public boolean isType(Type t)
    {
        return this.type[0].equals(t) || this.type[1].equals(t);
    }

    public Map<Stat, Integer> getIVs()
    {
        return this.IVs;
    }

    private double getAverageIV()
    {
        return this.getIVs().values().stream().mapToDouble(iv -> iv / 31D).sum() * 100 / 6D;
    }

    public String getTotalIV()
    {
        return String.format("%.2f", this.getAverageIV()) + "%";
    }

    public double getTotalIVRounded()
    {
        String iv = this.getTotalIV();
        return Double.parseDouble(iv.substring(0, iv.indexOf("%")));
    }
}
