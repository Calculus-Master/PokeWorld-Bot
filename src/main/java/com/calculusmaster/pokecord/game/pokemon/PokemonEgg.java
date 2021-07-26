package com.calculusmaster.pokecord.game.pokemon;

import com.calculusmaster.pokecord.game.enums.elements.EggGroup;
import com.calculusmaster.pokecord.game.enums.elements.Gender;
import com.calculusmaster.pokecord.util.Mongo;
import com.calculusmaster.pokecord.util.helpers.DataHelper;
import com.mongodb.client.model.Filters;
import org.bson.Document;

public class PokemonEgg
{
    private String target;
    private int exp;
    private int max;

    public static PokemonEgg create(Pokemon parent1, Pokemon parent2)
    {
        //Assumes breeding conditions have been satisfied

        String target;
        if(parent1.getEggGroup().contains(EggGroup.DITTO)) target = parent2.getName();
        else if(parent2.getEggGroup().contains(EggGroup.DITTO)) target = parent1.getName();
        else target = parent1.getGender().equals(Gender.FEMALE) ? parent1.getName() : parent2.getName();

        PokemonEgg egg = new PokemonEgg();

        egg.setTarget(target);
        egg.setExp(0);
        egg.setMaxExp((int)(DataHelper.POKEMON_BASE_HATCH_TARGETS.get(DataHelper.dex(target)) * (Math.random() + 1) * 3));

        return egg;
    }

    public static PokemonEgg fromDB(String eggID)
    {
        Document d = Mongo.EggData.find(Filters.eq("eggID", eggID)).first();

        PokemonEgg egg = new PokemonEgg();

        egg.setTarget(d.getString("target"));
        egg.setExp(d.getInteger("exp"));
        egg.setMaxExp(d.getInteger("max"));

        return egg;
    }

    public static void toDB(PokemonEgg egg)
    {
        Document eggData = new Document()
                .append("target", egg.getTarget())
                .append("exp", egg.getExp())
                .append("max", egg.getMaxExp());

        Mongo.EggData.insertOne(eggData);
    }

    public static Pokemon hatch(String eggID)
    {
        PokemonEgg egg = PokemonEgg.fromDB(eggID);

        Pokemon hatched = Pokemon.create(egg.getTarget());

        return hatched;
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
}
