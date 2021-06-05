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
    private int level;

    public PokemonReward(int level, String... names)
    {
        this.names = new ArrayList<>(Arrays.asList(names));
        this.level = level;
    }

    private Pokemon createNew()
    {
        Pokemon p = Pokemon.create(this.names.get(new Random().nextInt(this.names.size())));
        p.setLevel(this.level);

        return p;
    }

    @Override
    public String grantReward(PlayerDataQuery player)
    {
        return "";
    }
}
