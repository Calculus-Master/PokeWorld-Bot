package com.calculusmaster.pokecord.game.duel.players;

import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonEntity;

import java.util.Collections;
import java.util.Random;
import java.util.stream.IntStream;

public class WildPlayer extends AIPlayer
{
    public WildPlayer(PokemonEntity pokemonEntity, int level)
    {
        Random r = new Random();
        Pokemon pokemon = Pokemon.create(pokemonEntity);

        pokemon.setLevel(level);
        pokemon.setHealth(pokemon.getMaxHealth());
        IntStream.range(0, 4).forEach(i -> pokemon.learnMove(pokemon.availableMoves().get(r.nextInt(pokemon.availableMoves().size())), i));

        this.setTeam(Collections.singletonList(pokemon));
    }

    public WildPlayer(int level)
    {
        this(PokemonEntity.getRandom(), level);
    }
}
