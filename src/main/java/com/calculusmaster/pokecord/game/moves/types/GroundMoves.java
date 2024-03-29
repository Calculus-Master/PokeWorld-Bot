package com.calculusmaster.pokecord.game.moves.types;

import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.enums.elements.*;
import com.calculusmaster.pokecord.game.moves.Move;
import com.calculusmaster.pokecord.game.moves.builder.MoveEffectBuilder;
import com.calculusmaster.pokecord.game.moves.builder.StatChangeEffect;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;

import java.util.Random;

public class GroundMoves
{
    public String Earthquake(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(duel.data(opponent.getUUID()).digUsed) move.setPower(2 * move.getPower());
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String EarthPower(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.statChangeDamage(user, opponent, duel, move, Stat.SPDEF, -1, 20, false);
    }

    public String Dig(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(duel.data(user.getUUID()).digUsed)
        {
            duel.data(user.getUUID()).digUsed = false;
            return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
        }
        else
        {
            duel.data(user.getUUID()).digUsed = true;
            return user.getName() + " burrowed underground!";
        }
    }

    public String Bulldoze(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.statChangeDamage(user, opponent, duel, move, Stat.SPD, -1, 100, false);
    }

    public String Fissure(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addOHKOEffect()
                .execute();
    }

    public String PrecipiceBlades(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String ThousandArrows(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        duel.data(opponent.getUUID()).isRaised = false;
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String ThousandWaves(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        duel.data(opponent.getUUID()).canSwap = false;
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move) + " " + opponent.getName() + " cannot flee!";
    }

    public String Magnitude(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int r = new Random().nextInt(100);
        int magnitude;

        if(r < 5) magnitude = 4;
        else if(r < 5 + 10) magnitude = 5;
        else if(r < 5 + 10 + 20) magnitude = 6;
        else if(r < 5 + 10 + 20 + 30) magnitude = 7;
        else if(r < 5 + 10 + 20 + 30 + 20) magnitude = 8;
        else if(r < 5 + 10 + 20 + 30 + 20 + 10) magnitude = 9;
        else magnitude = 10;

        move.setPower(switch(magnitude) {
            case 4 -> 10;
            case 5 -> 30;
            case 6 -> 50;
            case 7 -> 70;
            case 8 -> 90;
            case 9 -> 110;
            case 10 -> 150;
            default -> 70;
        });

        if(duel.data(opponent.getUUID()).digUsed) move.setPower(2 * move.getPower());

        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move) + " Magnitude " + magnitude + "!";
    }

    public String Spikes(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addEntryHazardEffect(EntryHazard.SPIKES)
                .execute();
    }

    public String SandAttack(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addAccuracyChangeEffect(-1, 100, false)
                .execute();
    }

    public String MudSlap(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addAccuracyChangeEffect(-1, 100, false)
                .execute();
    }

    public String BoneRush(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.multiDamage(user, opponent, duel, move);
    }

    public String MudBomb(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addAccuracyChangeEffect(-1, 30, false)
                .execute();
    }

    public String DrillRun(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addCritDamageEffect()
                .execute();
    }

    public String BoneClub(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatusEffect(StatusCondition.FLINCHED, 10)
                .execute();
    }

    public String ShoreUp(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addFractionHealEffect(duel.weather.get().equals(Weather.SANDSTORM) ? 2 / 3D : 1 / 2D)
                .execute();
    }

    public String LandsWrath(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String Bonemerang(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.multiDamage(user, opponent, duel, move, 2);
    }

    public String MudSport(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addCustomEffect(() -> {
                    duel.data(user.getUUID()).mudSportUsed = true;
                    return "Electric-Type Moves have become less effective!";
                })
                .execute();
    }

    public String SandTomb(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatusEffect(StatusCondition.BOUND)
                .execute();
    }

    public String Rototiller(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addConditionalEffect(user.isType(Type.GRASS), b -> b.addStatChangeEffect(
                        new StatChangeEffect(Stat.ATK, 1, 100, true)
                                .add(Stat.SPATK, 1)))
                .addConditionalEffect(opponent.isType(Type.GRASS), b -> b.addStatChangeEffect(
                        new StatChangeEffect(Stat.ATK, 1, 100, false)
                                .add(Stat.SPATK, 1)))
                .execute();
    }

    public String StompingTantrum(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        //TODO: If previous move failed, does double damage
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .execute();
    }

    public String ScorchingSands(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatusEffect(StatusCondition.BURNED, 30)
                .addConditionalCustomEffect(opponent.hasStatusCondition(StatusCondition.FROZEN), () -> {
                    opponent.removeStatusCondition(StatusCondition.FROZEN);
                    return "";
                }, () -> "")
                .execute();
    }

    public String HighHorsepower(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .execute();
    }

    public String MudShot(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatChangeEffect(Stat.SPD, -1, 100, false)
                .execute();
    }

    public String SandsearStorm(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatusEffect(StatusCondition.BURNED, 20, false)
                .execute();
    }

    public String HeadlongRush(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatChangeEffect(new StatChangeEffect(Stat.DEF, -1, 100, true)
                        .add(Stat.SPDEF, -1))
                .execute();
    }
}
