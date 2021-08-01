package com.calculusmaster.pokecord.game.moves.types;

import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.elements.StatusCondition;
import com.calculusmaster.pokecord.game.enums.elements.Weather;
import com.calculusmaster.pokecord.game.moves.Move;
import com.calculusmaster.pokecord.game.moves.builder.MoveEffectBuilder;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;

public class IceMoves
{
    public String Hail(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addWeatherEffect(Weather.HAIL)
                .execute();
    }

    public String IceBall(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String IcicleCrash(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statusDamageMove(user, opponent, duel, move, StatusCondition.FLINCHED, 30);
    }

    public String IcicleSpear(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.multihitDamageMove(user, opponent, duel, move);
    }

    public String IceBeam(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statusDamageMove(user, opponent, duel, move, StatusCondition.FROZEN, 10);
    }

    public String Blizzard(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statusDamageMove(user, opponent, duel, move, StatusCondition.FROZEN, 10);
    }

    public String SheerCold(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addOHKOEffect()
                .execute();
    }

    public String PowderSnow(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statusDamageMove(user, opponent, duel, move, StatusCondition.FROZEN, 10);
    }

    public String Mist(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        duel.data(user.getUUID()).statImmuneTurns = 3;

        return user.getName() + " is immune to any stat changes for 3 turns!";
    }

    public String IceShard(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String FreezeDry(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String IceFang(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatusEffect(StatusCondition.FROZEN, 10)
                .addStatusEffect(StatusCondition.FLINCHED, 10)
                .execute();
    }

    public String IcePunch(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statusDamageMove(user, opponent, duel, move, StatusCondition.FROZEN, 10);
    }

    public String Glaciate(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statChangeDamageMove(user, opponent, duel, move, Stat.SPD, -1, 100, false);
    }

    public String FreezeShock(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statusDamageMove(user, opponent, duel, move, StatusCondition.PARALYZED, 30);
    }

    public String IceBurn(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statusDamageMove(user, opponent, duel, move, StatusCondition.BURNED, 30);
    }

    public String IcyWind(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statChangeDamageMove(user, opponent, duel, move, Stat.SPD, -1, 100, false);
    }

    public String GlacialLance(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String Avalanche(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(duel.first.equals(opponent.getUUID()) && duel.data(user.getUUID()).lastDamageTaken > 0) move.setPower(2.0);

        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String AuroraVeil(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        duel.data(user.getUUID()).auroraVeilUsed = true;
        duel.data(user.getUUID()).auroraVeilTurns = 5;

        return user.getName() + " is defended from Physical and Special attacks!";
    }
}
