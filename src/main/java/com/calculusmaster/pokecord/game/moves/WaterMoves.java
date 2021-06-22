package com.calculusmaster.pokecord.game.moves;

import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.Move;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.elements.StatusCondition;
import com.calculusmaster.pokecord.game.enums.elements.Weather;
import com.calculusmaster.pokecord.game.moves.builder.MoveEffectBuilder;

import java.util.Random;

public class WaterMoves
{
    public String WaterGun(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String Withdraw(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatChangeEffect(Stat.DEF, 1, 100, true)
                .execute();
    }

    public String Bubble(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatChangeEffect(Stat.SPD, -1, 10, false)
                .execute();
    }

    public String WaterPulse(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatusEffect(StatusCondition.CONFUSED, 20)
                .execute();
    }

    public String AquaTail(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String RainDance(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addWeatherEffect(Weather.RAIN)
                .execute();
    }

    public String HydroPump(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String HydroCannon(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    //TODO: Heals 1/16 each turn
    public String AquaRing(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addFractionHealEffect(1 / 16D)
                .execute();
    }

    public String MuddyWater(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String OriginPulse(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String WaterSpout(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        move.setPower(150 * user.getHealth() / (double)user.getStat(Stat.HP));
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String Dive(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(duel.data(user.getUUID()).diveUsed)
        {
            duel.data(user.getUUID()).diveUsed = false;
            return Move.simpleDamageMove(user, opponent, duel, move);
        }
        else
        {
            duel.data(user.getUUID()).diveUsed = true;
            return user.getName() + " hid underwater!";
        }
    }

    public String Surf(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String Whirlpool(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatusEffect(StatusCondition.BOUND)
                .execute();
    }

    public String WaterShuriken(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.multihitDamageMove(user, opponent, duel, move);
    }

    public String Scald(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statusDamageMove(user, opponent, duel, move, StatusCondition.BURNED, 30);
    }

    //TODO: 50% chance to lower accuracy
    public String Octazooka(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String SnipeShot(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addCritDamageEffect()
                .execute();
    }
}
