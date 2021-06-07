package com.calculusmaster.pokecord.game.pokepass;

import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class PokemonReward extends TierReward
{
    private List<String> names;
    private int minIV;
    private int level;

    public PokemonReward(int level, int minIV, String... names)
    {
        this.names = new ArrayList<>(Arrays.asList(names));
        this.level = level;
        this.minIV = minIV;
    }

    private Pokemon createNew()
    {
        Pokemon p = Pokemon.create(this.names.get(new Random().nextInt(this.names.size())));
        p.setLevel(this.level);

        while(p.getTotalIVRounded() < this.minIV) p.setIVs();

        return p;
    }

    @Override
    public String grantReward(PlayerDataQuery player)
    {
        Pokemon p = this.createNew();

        Pokemon.uploadPokemon(p);
        player.addPokemon(p.getUUID());

        return "You acquired a Level " + p.getLevel() + " " + p.getName() + " (IV: " + p.getTotalIVRounded() + ")!";
    }

    @Override
    public String getTierDescription()
    {
        return "Pokemon Reward: Level " + this.level + " " + this.names.toString() + "!";
    }
}
