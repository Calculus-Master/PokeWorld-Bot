package com.calculusmaster.pokecord.game.player.components;

import com.calculusmaster.pokecord.game.pokemon.data.PokemonEntity;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;

public class PlayerPokedex
{
    private final Map<PokemonEntity, Integer> pokedex;

    public PlayerPokedex()
    {
        this.pokedex = new HashMap<>();
    }

    public PlayerPokedex(Document data)
    {
        this();

        data.forEach((s, o) -> this.pokedex.put(PokemonEntity.cast(s), (int)o));
    }

    public Document serialize()
    {
        Document data = new Document();

        this.pokedex.forEach((e, i) -> data.append(e.toString(), i));

        return data;
    }

    //Accessors
    public boolean hasCollected(PokemonEntity entity)
    {
        return this.pokedex.containsKey(entity);
    }

    public int getCollectedAmount(PokemonEntity entity)
    {
        return this.pokedex.getOrDefault(entity, 0);
    }

    public int add(PokemonEntity entity)
    {
        int newAmount = this.getCollectedAmount(entity) + 1;

        this.pokedex.put(entity, newAmount);

        return newAmount;
    }

    public int getSize()
    {
        return this.pokedex.size();
    }

    //Rewards
    public int getCollectionReward(PokemonEntity entity)
    {
        //{<new collection>, <base per milestone>, <multiplier per milestone>}
        int[] rewards = switch(entity.getRarity()) {
            case COPPER -> new int[]        {150, 200, 150};
            case SILVER -> new int[]        {175, 215, 165};
            case GOLD -> new int[]          {200, 240, 175};
            case DIAMOND -> new int[]       {225, 260, 190};
            case PLATINUM -> new int[]      {250, 300, 200};
            case MYTHICAL -> new int[]      {300, 350, 250};
            case ULTRA_BEAST -> new int[]   {325, 375, 275};
            case LEGENDARY -> new int[]     {500, 400, 750};
        };

        int amount = this.getCollectedAmount(entity);

        if(amount == 1) return rewards[0];
        else if(amount % 5 == 0) return rewards[1] + rewards[2] * (amount / 5 - 1);
        else return 0;
    }
}
