package com.calculusmaster.pokecord.game.pokemon;

import com.calculusmaster.pokecord.game.enums.elements.EggGroup;
import com.calculusmaster.pokecord.game.enums.elements.Gender;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.items.Item;
import com.calculusmaster.pokecord.game.pokemon.component.PokemonStats;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonData;
import com.calculusmaster.pokecord.util.Global;
import com.calculusmaster.pokecord.util.Mongo;
import com.calculusmaster.pokecord.util.helpers.IDHelper;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;

import java.util.*;

public class PokemonEgg
{
    public static int MAX_EGGS;

    private String eggID;
    private String target;
    private int exp;
    private int max;
    private String ivs;

    public static PokemonEgg create(Pokemon parent1, Pokemon parent2)
    {
        //Assumes breeding conditions have been satisfied

        String target;
        if(parent1.getEggGroups().contains(EggGroup.DITTO)) target = parent2.getName().equals("Manaphy") ? "Phione" : (parent2.getName().equals("Phione") ? "Manaphy" : parent2.getName());
        else if(parent2.getEggGroups().contains(EggGroup.DITTO)) target = parent1.getName().equals("Manaphy") ? "Phione" : (parent1.getName().equals("Phione") ? "Manaphy" : parent1.getName());
        else target = parent1.getGender().equals(Gender.FEMALE) ? parent1.getName() : parent2.getName();

        PokemonEgg egg = new PokemonEgg();

        egg.setEggID();
        egg.setTarget(target);
        egg.setExp(0);
        egg.setMaxExp((int)(PokemonData.get(target).hatchTarget * (Math.random() + 1)));
        egg.setIVs(parent1, parent2);

        return egg;
    }

    public static PokemonEgg fromDB(String eggID)
    {
        Document d = Mongo.EggData.find(Filters.eq("eggID", eggID)).first();

        PokemonEgg egg = new PokemonEgg();

        egg.setEggID(d.getString("eggID"));
        egg.setTarget(d.getString("target"));
        egg.setExp(d.getInteger("exp"));
        egg.setMaxExp(d.getInteger("max"));
        egg.setIVs(d.getString("ivs"));

        return egg;
    }

    public static void toDB(PokemonEgg egg)
    {
        Document eggData = new Document()
                .append("eggID", egg.getEggID())
                .append("target", egg.getTarget())
                .append("exp", egg.getExp())
                .append("max", egg.getMaxExp())
                .append("ivs", egg.getIVs());

        Mongo.EggData.insertOne(eggData);
    }

    public Pokemon hatch()
    {
        Pokemon hatched = Pokemon.create(this.getTarget());

        PokemonStats ivOverride = new PokemonStats(this.ivs);
        for(Stat s : Stat.values()) if(ivOverride.get().get(s) == 0) ivOverride.get().put(s, hatched.getIVs().get(s));
        hatched.setIVs(ivOverride.get());

        Mongo.EggData.deleteOne(Filters.eq("eggID", this.getEggID()));
        return hatched;
    }

    public boolean canHatch()
    {
        return this.exp >= this.max;
    }

    public String getOverview()
    {
        return "ID: " + this.eggID + "\nEXP: `" + this.exp + " / " + this.max + "` XP";
    }

    public String getIVs()
    {
        return this.ivs;
    }

    public void setIVs(Pokemon parent1, Pokemon parent2)
    {
        PokemonStats ivs = new PokemonStats();

        int number = parent1.getItem().equals(Item.DESTINY_KNOT) || parent2.getItem().equals(Item.DESTINY_KNOT) ? 5 : 3;

        List<Stat> stats = new ArrayList<>(Arrays.asList(Stat.values()));
        Collections.shuffle(stats);
        stats.subList(0, 6 - number).clear();

        for(Stat s : stats) ivs.get().put(s, (new Random().nextInt(10) < 5 ? parent1 : parent2).getIVs().get(s));

        this.ivs = ivs.condense();
    }

    public void setIVs(String ivs)
    {
        this.ivs = ivs;
    }

    public void addExp(int amount)
    {
        if(this.exp >= this.max) return;

        this.exp = Global.clamp(this.exp + amount, 0, this.max);

        Mongo.EggData.updateOne(Filters.eq("eggID", this.getEggID()), Updates.set("exp", this.exp));
    }

    public void setExp(int exp)
    {
        this.exp = exp;
    }

    public int getExp()
    {
        return this.exp;
    }

    public void setMaxExp(int max)
    {
        this.max = max;
    }

    public int getMaxExp()
    {
        return this.max;
    }

    public void setTarget(String target)
    {
        this.target = target;
    }

    public String getTarget()
    {
        return this.target;
    }

    public void setEggID(String eggID)
    {
        this.eggID = eggID;
    }

    public void setEggID()
    {
        this.setEggID(IDHelper.numeric(6));
    }

    public String getEggID()
    {
        return this.eggID;
    }
}
