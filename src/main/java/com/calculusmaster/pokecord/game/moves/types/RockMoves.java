package com.calculusmaster.pokecord.game.moves.types;

import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.enums.elements.EntryHazard;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.elements.StatusCondition;
import com.calculusmaster.pokecord.game.enums.elements.Weather;
import com.calculusmaster.pokecord.game.moves.Move;
import com.calculusmaster.pokecord.game.moves.builder.MoveEffectBuilder;
import com.calculusmaster.pokecord.game.moves.builder.StatChangeEffect;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;

public class RockMoves
{
    public String Rollout(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String Sandstorm(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addWeatherEffect(Weather.SANDSTORM)
                .execute();
    }

    public String SmackDown(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        duel.data(opponent.getUUID()).isRaised = false;

        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String AncientPower(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatChangeEffect(
                        new StatChangeEffect(Stat.ATK, 1, 10, true)
                                .add(Stat.DEF, 1)
                                .add(Stat.SPATK, 1)
                                .add(Stat.SPDEF, 1)
                                .add(Stat.SPD, 1))
                .execute();
    }

    public String PowerGem(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String RockBlast(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.multihitDamageMove(user, opponent, duel, move);
    }

    public String StealthRock(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addEntryHazardEffect(EntryHazard.STEALTH_ROCK)
                .execute();
    }

    public String RockThrow(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String RockTomb(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatChangeEffect(Stat.SPD, -1, 100, false)
                .execute();
    }

    public String RockSlide(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statusDamageMove(user, opponent, duel, move, StatusCondition.FLINCHED, 30);
    }

    public String StoneEdge(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addCritDamageEffect()
                .execute();
    }

    public String DiamondStorm(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statChangeDamageMove(user, opponent, duel, move, Stat.DEF, 2, 50, true);
    }

    public String WideGuard(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return move.getNotImplementedResult();
    }

    public String HeadSmash(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addRecoilEffect(1 / 2D)
                .execute();
    }

    public String Accelerock(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String RockWrecker(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }
}
