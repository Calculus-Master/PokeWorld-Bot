package com.calculusmaster.pokecord.game.moves.types;

import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.enums.elements.*;
import com.calculusmaster.pokecord.game.moves.Move;
import com.calculusmaster.pokecord.game.moves.builder.MoveEffectBuilder;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;

public class WaterMoves
{
    public String WaterGun(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
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
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String RainDance(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addWeatherEffect(Weather.RAIN)
                .execute();
    }

    public String HydroPump(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String HydroCannon(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String AquaRing(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        duel.data(user.getUUID()).aquaRingUsed = true;

        return user.getName() + " set up an Aqua Ring veil!";
    }

    public String MuddyWater(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String OriginPulse(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String WaterSpout(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        move.setPower(150 * user.getHealth() / (double)user.getStat(Stat.HP));
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String Dive(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(duel.data(user.getUUID()).diveUsed)
        {
            duel.data(user.getUUID()).diveUsed = false;
            return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
        }
        else
        {
            duel.data(user.getUUID()).diveUsed = true;
            return user.getName() + " hid underwater!";
        }
    }

    public String Surf(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
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
        return MoveEffectBuilder.multiDamage(user, opponent, duel, move);
    }

    public String Scald(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.statusDamage(user, opponent, duel, move, StatusCondition.BURNED, 30);
    }

    public String Octazooka(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addAccuracyChangeEffect(-1, 50, false)
                .execute();
    }

    public String SnipeShot(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addCritDamageEffect()
                .execute();
    }

    public String FishiousRend(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(duel.first.equals(user.getUUID())) move.setPower(move.getPower() * 2);

        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String Soak(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(!opponent.hasAbility(Ability.MULTITYPE))
        {
            opponent.setType(Type.WATER);

            return opponent.getName() + " is now a Water type!";
        }
        else return move.getNothingResult();
    }

    public String AquaJet(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String SurgingStrikes(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        move.critChance = 24;
        return MoveEffectBuilder.multiDamage(user, opponent, duel, move, 3);
    }

    public String LifeDew(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addFractionHealEffect(1 / 4D)
                .execute();
    }

    public String SparklingAria(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(opponent.hasStatusCondition(StatusCondition.BURNED)) opponent.removeStatusCondition(StatusCondition.BURNED);

        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String BubbleBeam(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatChangeEffect(Stat.SPD, -1, 10, false)
                .execute();
    }

    public String Clamp(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatusEffect(StatusCondition.BOUND)
                .execute();
    }

    public String WaterSport(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        duel.data(user.getUUID()).waterSportUsed = true;

        return "Fire Type moves now have 50% less power!";
    }

    public String Waterfall(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatusEffect(StatusCondition.FLINCHED, 20)
                .execute();
    }

    public String Crabhammer(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addCritDamageEffect()
                .execute();
    }

    public String Liquidation(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatChangeEffect(Stat.DEF, -1, 20, false)
                .execute();
    }

    public String Brine(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addCustomRunnableEffect(() -> move.setPower(opponent.getHealth() < opponent.getMaxHealth() / 2 ? 2.0 : 1.0))
                .addDamageEffect()
                .execute();
    }

    public String RazorShell(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatChangeEffect(Stat.DEF, -1, 50, false)
                .execute();
    }

    public String SteamEruption(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatusEffect(StatusCondition.BURNED, 30)
                .execute();
    }

    public String WaterPledge(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        //TODO: Deals extra damage (160 base power) if teammate (2v2/3v3) uses Water/Grass Pledge
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .execute();
    }

    public String HydroSteam(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }
}
