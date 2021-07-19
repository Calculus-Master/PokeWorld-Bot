package com.calculusmaster.pokecord.util;

import com.calculusmaster.pokecord.game.enums.elements.GrowthRate;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.elements.Type;
import com.calculusmaster.pokecord.game.enums.items.TM;
import com.calculusmaster.pokecord.game.enums.items.TR;
import org.bson.Document;

import java.util.*;

public final class PokemonData
{
    public final Document document;

    public final String name;

    public final String species;
    public final double height;
    public final double weight;

    public final int dex;

    public final List<Type> types;

    public final Map<String, Integer> evolutions;

    public final List<String> forms;
    public final List<String> megas;

    public final Map<Stat, Integer> baseStats;

    public final Map<Stat, Integer> yield;

    public final Map<String, Integer> moves;
    //public final List<String> moves;
    //public final List<Integer> movesLVL;

    public final List<TM> validTMs;
    public final List<TR> validTRs;

    public final List<String> abilities;

    public final GrowthRate growthRate;
    public final int baseEXP;

    public final String normalURL;
    public final String shinyURL;

    public PokemonData(Document d)
    {
        this.document = d;

        this.name = d.getString("name");

        this.species = d.getString("fillerinfo").split("-")[0];
        this.height = Double.parseDouble(d.getString("fillerinfo").split("-")[1]);
        this.weight = this.name.equals("Eternamax Eternatus") ? Double.MAX_VALUE : Double.parseDouble(d.getString("fillerinfo").split("-")[2]);

        this.dex = d.getInteger("dex");

        this.types = List.of(Type.cast(d.getList("type", String.class).get(0)), Type.cast(d.getList("type", String.class).get(1)));

        this.evolutions = new LinkedHashMap<>();
        for(int i = 0; i < d.getList("evolutions", String.class).size(); i++) this.evolutions.put(d.getList("evolutions", String.class).get(i), d.getList("evolutionsLVL", Integer.class).get(i));

        this.forms = d.getList("forms", String.class, new ArrayList<>());
        this.megas = d.getList("mega", String.class, new ArrayList<>());

        this.baseStats = new HashMap<>();
        for(Stat s : Stat.values()) this.baseStats.put(s, d.getList("stats", Integer.class).get(s.ordinal()));

        this.yield = new HashMap<>();
        for(Stat s : Stat.values()) this.yield.put(s, d.getList("ev", Integer.class).get(s.ordinal()));

        this.moves = new LinkedHashMap<>();
        for(int i = 0; i < d.getList("moves", String.class).size(); i++) this.moves.put(d.getList("moves", String.class).get(i), d.getList("movesLVL", Integer.class).get(i));

        this.validTMs = d.getList("movesTM", Integer.class).stream().map(TM::get).toList();
        this.validTRs = d.getList("movesTR", Integer.class).stream().map(TR::get).toList();

        this.abilities = d.getList("abilities", String.class);

        this.growthRate = GrowthRate.cast(d.getString("growthrate"));
        this.baseEXP = d.getInteger("exp");
        
        this.normalURL = d.getString("normalURL");
        this.shinyURL = d.getString("shinyURL");
    }

    public PokemonData copy()
    {
        return new PokemonData(this.document);
    }
}
