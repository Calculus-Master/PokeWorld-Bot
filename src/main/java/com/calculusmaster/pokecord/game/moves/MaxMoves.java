package com.calculusmaster.pokecord.game.moves;

import com.calculusmaster.pokecord.game.Move;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.duel.DuelHelper;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.elements.StatusCondition;
import com.calculusmaster.pokecord.game.enums.elements.Weather;

import java.util.Arrays;
import java.util.Random;

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
    //TODO: Damage Over 4 Turns on Non-Fire Opponents
    public String GMaxWildfire(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String GMaxBefuddle(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        StatusCondition status = Arrays.asList(StatusCondition.POISONED, StatusCondition.PARALYZED, StatusCondition.ASLEEP).get(new Random().nextInt(3));
        return Move.statusDamageMove(user, opponent, duel, move, status, 100);
    }

    public String GMaxVoltCrash(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statusDamageMove(user, opponent, duel, move, StatusCondition.PARALYZED, 100);
    }

    public String GMaxGoldRush(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int coins = (int)(user.getLevel() * 1.25);
        duel.getPlayers()[duel.playerIndexFromUUID(user.getUUID())].data.changeCredits(coins);

        return Move.statusDamageMove(user, opponent, duel, move, StatusCondition.CONFUSED, 100) + " Meowth found " + coins + " credits!";
    }

    //TODO: Raises crit chance by one stage
    public String GMaxChiStrike(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    //TODO: Prevents swapping
    public String GMaxTerror(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String GMaxFoamBurst(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statChangeDamageMove(user, opponent, duel, move, Stat.SPD, -2, 100, false);
    }

    //TODO: Halves damage from physical and special sources
    public String GMaxResonance(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    //TODO: Opposite Gender Infatuation
    public String GMaxCuddle(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    //TODO: 50% chance to restore berries
    public String GMaxReplenish(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String GMaxMalodor(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statusDamageMove(user, opponent, duel, move, StatusCondition.POISONED, 100);
    }

    //TODO: Opponent can't use same move twice in a row
    public String GMaxMeltdown(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    //TODO: Defog
    public String GMaxWindRage(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    //TODO: Gravity
    public String GMaxGravitas(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String GMaxStonesurge(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        duel.hazardData(opponent.getUUID()).addHazard(DuelHelper.EntryHazard.STEALTH_ROCK);
        return Move.simpleDamageMove(user, opponent, duel, move) + " " + user.getName() + " laid a Stealth Rock trap!";
    }

    //TODO: Damage Over 4 Turns on Non-Rock Opponents
    public String GMaxVolcalith(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    //TODO: Lower Evasiveness by 1 stage
    public String GMaxTartness(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String GMaxSweetness(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        user.clearStatusConditions();
        return Move.simpleDamageMove(user, opponent, duel, move) + " " + user.getName() + "'s Status Conditions were removed!";
    }

    //TODO: Trap in Sand Tomb
    public String GMaxSandblast(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String GMaxStunshock(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        StatusCondition status = Arrays.asList(StatusCondition.POISONED, StatusCondition.PARALYZED).get(new Random().nextInt(2));
        return Move.statusDamageMove(user, opponent, duel, move, status, 100);
    }

    //TODO: Trap in Fire Spin
    public String GMaxCentiferno(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String GMaxSmite(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statusDamageMove(user, opponent, duel, move, StatusCondition.CONFUSED, 100);
    }

    //TODO: Cause target to feel drowzy (like Yawn)
    public String GMaxSnooze(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String GMaxFinale(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int heal = user.getStat(Stat.HP) / 6;
        user.heal(heal);
        return Move.simpleDamageMove(user, opponent, duel, move) + " " + user.getName() + " healed for " + heal + " HP!";
    }

    //TODO: Steel Spikes (like Stealth Rock)
    public String GMaxSteelsurge(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    //TODO: Takes away 2 PP (won't be added)
    public String GMaxDepletion(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    //TODO: Damage Over 4 Turns on Non-Grass Opponents
    public String GMaxVineLash(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    //TODO: Damage Over 4 Turns on Non-Water Opponents
    public String GMaxCannonade(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    //TODO: Ignore Abilities
    public String GMaxDrumSolo(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        move.setPower(160);
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    //TODO: Ignore Abilities
    public String GMaxFireball(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        move.setPower(160);
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    //TODO: Ignore Abilities
    public String GMaxHydroSnipe(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        move.setPower(160);
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    //TODO: Bypasses Protection and Max Guard
    public String GMaxOneBlow(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    //TODO: Bypasses Protection and Max Guard
    public String GMaxRapidFlow(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    //Custom
    //TODO: Changes weather to Delta Stream
    public String GMaxStratoblast(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    //TODO: Changes weather to Primordial Sea
    public String GMaxOceanize(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    //TODO: Changes weather to Desolate Land
    public String GMaxEvaporation(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }
}
