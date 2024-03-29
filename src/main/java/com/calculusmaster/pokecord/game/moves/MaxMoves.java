package com.calculusmaster.pokecord.game.moves;

import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.duel.component.GMaxDoTType;
import com.calculusmaster.pokecord.game.duel.players.UserPlayer;
import com.calculusmaster.pokecord.game.enums.elements.*;
import com.calculusmaster.pokecord.game.moves.builder.MoveEffectBuilder;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;

import java.util.Arrays;
import java.util.Random;
import java.util.SplittableRandom;

public class MaxMoves
{
    //Generic Max Moves
    public String MaxFlutterby(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.statChangeDamage(user, opponent, duel, move, Stat.SPATK, -1, 100, false);
    }

    public String MaxDarkness(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.statChangeDamage(user, opponent, duel, move, Stat.SPDEF, -1, 100, false);
    }

    public String MaxWyrmwind(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.statChangeDamage(user, opponent, duel, move, Stat.ATK, -1, 100, false);
    }

    public String MaxLightning(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addTerrainEffect(Terrain.ELECTRIC_TERRAIN)
                .execute();
    }

    public String MaxStarfall(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addTerrainEffect(Terrain.MISTY_TERRAIN)
                .execute();
    }

    public String MaxKnuckle(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.statChangeDamage(user, opponent, duel, move, Stat.ATK, 1, 100, true);
    }

    public String MaxFlare(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addWeatherEffect(Weather.HARSH_SUNLIGHT)
                .execute();
    }

    public String MaxAirstream(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.statChangeDamage(user, opponent, duel, move, Stat.SPD, 1, 100, true);
    }

    public String MaxPhantasm(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.statChangeDamage(user, opponent, duel, move, Stat.DEF, -1, 100, false);
    }

    public String MaxOvergrowth(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addTerrainEffect(Terrain.GRASSY_TERRAIN)
                .execute();
    }

    public String MaxQuake(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.statChangeDamage(user, opponent, duel, move, Stat.SPDEF, 1, 100, true);
    }

    public String MaxHailstorm(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addWeatherEffect(Weather.HAIL)
                .execute();
    }

    public String MaxStrike(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.statChangeDamage(user, opponent, duel, move, Stat.SPD, -1, 100, false);
    }

    public String MaxGuard(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(!duel.first.equals(user.getUUID())) return move.getNothingResult();
        else
        {
            duel.data(user.getUUID()).maxGuardUsed = true;
            return user.getName() + " is heavily protected!";
        }
    }

    public String MaxMindstorm(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addTerrainEffect(Terrain.PSYCHIC_TERRAIN)
                .execute();
    }

    public String MaxRockfall(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addWeatherEffect(Weather.SANDSTORM)
                .execute();
    }

    public String MaxSteelspike(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.statChangeDamage(user, opponent, duel, move, Stat.DEF, 1, 100, true);
    }

    public String MaxGeyser(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addWeatherEffect(Weather.RAIN)
                .execute();
    }

    //G-Max Moves
    public String GMaxWildfire(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addGMaxDoTEffect(GMaxDoTType.WILDFIRE)
                .execute();
    }

    public String GMaxBefuddle(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        StatusCondition status = Arrays.asList(StatusCondition.POISONED, StatusCondition.PARALYZED, StatusCondition.ASLEEP).get(new Random().nextInt(3));
        return MoveEffectBuilder.statusDamage(user, opponent, duel, move, status, 100);
    }

    public String GMaxVoltCrash(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.statusDamage(user, opponent, duel, move, StatusCondition.PARALYZED, 100);
    }

    public String GMaxGoldRush(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int coins = new SplittableRandom().nextInt((int)(user.getLevel() * 1.25), (int)(user.getLevel() * 2.0));

        if(duel.getPlayers()[duel.playerIndexFromUUID(user.getUUID())] instanceof UserPlayer u)
            u.data.changeCredits(coins);

        return MoveEffectBuilder.statusDamage(user, opponent, duel, move, StatusCondition.CONFUSED, 100) + " Meowth found " + coins + " credits!";
    }

    //TODO: Raises crit chance by one stage
    public String GMaxChiStrike(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String GMaxTerror(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        duel.data(opponent.getUUID()).canSwap = false;

        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move) + " " + opponent.getName() + " is terrified in place!";
    }

    public String GMaxFoamBurst(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.statChangeDamage(user, opponent, duel, move, Stat.SPD, -2, 100, false);
    }

    //TODO: Halves damage from physical and special sources
    public String GMaxResonance(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String GMaxCuddle(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatusEffect(StatusCondition.INFATUATED)
                .execute();
    }

    //TODO: 50% chance to restore berries
    public String GMaxReplenish(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String GMaxMalodor(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.statusDamage(user, opponent, duel, move, StatusCondition.POISONED, 100);
    }

    //TODO: Opponent can't use same move twice in a row
    public String GMaxMeltdown(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    //TODO: Defog
    public String GMaxWindRage(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    //TODO: Gravity
    public String GMaxGravitas(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String GMaxStonesurge(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        duel.hazardData(opponent.getUUID()).addHazard(EntryHazard.STEALTH_ROCK);
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move) + " " + user.getName() + " laid a Stealth Rock trap!";
    }

    public String GMaxVolcalith(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addGMaxDoTEffect(GMaxDoTType.VOLCALITH)
                .execute();
    }

    public String GMaxTartness(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addEvasionChangeEffect(-1, 100, false)
                .execute();
    }

    public String GMaxSweetness(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        user.clearStatusConditions();
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move) + " " + user.getName() + "'s Status Conditions were removed!";
    }

    public String GMaxSandblast(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatusEffect(StatusCondition.BOUND)
                .execute();
    }

    public String GMaxStunshock(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        StatusCondition status = Arrays.asList(StatusCondition.POISONED, StatusCondition.PARALYZED).get(new Random().nextInt(2));
        return MoveEffectBuilder.statusDamage(user, opponent, duel, move, status, 100);
    }

    public String GMaxCentiferno(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatusEffect(StatusCondition.BOUND)
                .execute();
    }

    public String GMaxSmite(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.statusDamage(user, opponent, duel, move, StatusCondition.CONFUSED, 100);
    }

    //TODO: Cause target to feel drowzy (like Yawn)
    public String GMaxSnooze(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String GMaxFinale(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int heal = user.getStat(Stat.HP) / 6;
        user.heal(heal);
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move) + " " + user.getName() + " healed for " + heal + " HP!";
    }

    //TODO: Steel Spikes (like Stealth Rock)
    public String GMaxSteelsurge(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String GMaxDepletion(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String GMaxVineLash(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addGMaxDoTEffect(GMaxDoTType.VINE_LASH)
                .execute();
    }

    public String GMaxCannonade(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addGMaxDoTEffect(GMaxDoTType.CANNONADE)
                .execute();
    }

    public String GMaxDrumSolo(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        move.setPower(160);
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String GMaxFireball(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        move.setPower(160);
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String GMaxHydrosnipe(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        move.setPower(160);
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String GMaxOneBlow(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String GMaxRapidFlow(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    //Custom
    //TODO: Changes weather to Delta Stream
    public String GMaxStratoblast(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    //TODO: Changes weather to Primordial Sea
    public String GMaxOceanize(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    //TODO: Changes weather to Desolate Land
    public String GMaxEvaporation(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }
}
