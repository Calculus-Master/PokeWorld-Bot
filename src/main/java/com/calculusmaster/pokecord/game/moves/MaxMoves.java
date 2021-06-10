package com.calculusmaster.pokecord.game.moves;

import com.calculusmaster.pokecord.game.Move;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.duel.DuelHelper;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.elements.Weather;

public class MaxMoves
{
    //Generic Max Moves
    public String MaxFlutterby(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statChangeDamageMove(user, opponent, duel, move, Stat.SPATK, -1, 100, false);
    }

    public String MaxDarkness(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statChangeDamageMove(user, opponent, duel, move, Stat.SPDEF, -1, 100, false);
    }

    public String MaxWyrmwind(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statChangeDamageMove(user, opponent, duel, move, Stat.ATK, -1, 100, false);
    }

    public String MaxLightning(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        duel.terrain = DuelHelper.Terrain.ELECRIC_TERRAIN;
        duel.terrainTurns = 5;

        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String MaxStarfall(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        duel.terrain = DuelHelper.Terrain.MISTY_TERRAIN;
        duel.terrainTurns = 5;

        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String MaxKnuckle(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statChangeDamageMove(user, opponent, duel, move, Stat.ATK, 1, 100, true);
    }

    public String MaxFlare(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        duel.weather = Weather.HARSH_SUNLIGHT;
        duel.weatherTurns = 5;

        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String MaxAirstream(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statChangeDamageMove(user, opponent, duel, move, Stat.SPD, 1, 100, true);
    }

    public String MaxPhantasm(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statChangeDamageMove(user, opponent, duel, move, Stat.DEF, -1, 100, false);
    }

    public String MaxOvergrowth(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        duel.terrain = DuelHelper.Terrain.GRASSY_TERRAIN;
        duel.terrainTurns = 5;

        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String MaxQuake(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statChangeDamageMove(user, opponent, duel, move, Stat.SPDEF, 1, 100, true);
    }

    public String MaxHailstorm(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        duel.weather = Weather.HAIL;
        duel.weatherTurns = 5;

        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String MaxStrike(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statChangeDamageMove(user, opponent, duel, move, Stat.SPD, -1, 100, false);
    }

    //TODO: Max Guard
    public String MaxGuard(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return move.getNotImplementedResult();
    }

    public String MaxMindstorm(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        duel.terrain = DuelHelper.Terrain.PSYCHIC_TERRAIN;
        duel.terrainTurns = 5;

        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String MaxRockfall(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        duel.weather = Weather.SANDSTORM;
        duel.weatherTurns = 5;

        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String MaxSteelspike(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statChangeDamageMove(user, opponent, duel, move, Stat.DEF, 1, 100, true);
    }

    public String MaxGeyser(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        duel.weather = Weather.RAIN;
        duel.weatherTurns = 5;

        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    //G-Max Moves
}
