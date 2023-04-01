package com.calculusmaster.pokecord.util.interfaces;

import com.calculusmaster.pokecord.game.moves.Move;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonEntity;

public interface ZCrystalValidator
{
    boolean check(PokemonEntity pokemonEntity, Move move);
}
