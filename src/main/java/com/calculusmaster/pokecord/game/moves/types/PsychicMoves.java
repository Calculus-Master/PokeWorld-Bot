package com.calculusmaster.pokecord.game.moves.types;

import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.enums.elements.*;
import com.calculusmaster.pokecord.game.enums.items.Item;
import com.calculusmaster.pokecord.game.moves.Move;
import com.calculusmaster.pokecord.game.moves.builder.MoveEffectBuilder;
import com.calculusmaster.pokecord.game.moves.builder.StatChangeEffect;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class PsychicMoves
{
    public String CalmMind(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatChangeEffect(
                        new StatChangeEffect(Stat.SPATK, 1, 100, true)
                                .add(Stat.SPDEF, 1))
                .execute();
    }

    public String Psyshock(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String Confusion(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatusEffect(StatusCondition.CONFUSED, 10)
                .execute();
    }

    public String Psybeam(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatusEffect(StatusCondition.CONFUSED, 10)
                .execute();
    }

    public String Agility(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatChangeEffect(Stat.SPD, 2, 100, true)
                .execute();
    }

    public String Psychic(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatChangeEffect(Stat.SPDEF, -1, 10, false)
                .execute();
    }

    public String PsychoShift(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(!user.hasAnyStatusCondition()) return move.getNothingResult();
        else
        {
            for(StatusCondition s : user.getStatusConditions())
            {
                opponent.addStatusCondition(s);
                user.removeStatusCondition(s);
            }

            return user.getName() + " transferred all Status Conditions to " + opponent.getName() + "!";
        }
    }

    public String Hypnosis(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatusEffect(StatusCondition.ASLEEP)
                .execute();
    }

    public String PsychoCut(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addCritDamageEffect()
                .execute();
    }

    public String FreezingGlare(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.statusDamage(user, opponent, duel, move, StatusCondition.FROZEN, 10);
    }

    public String DreamEater(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(opponent.hasStatusCondition(StatusCondition.ASLEEP))
        {
            return MoveEffectBuilder.make(user, opponent, duel, move)
                    .addDamageEffect()
                    .addDamageHealEffect(1 / 2D)
                    .execute();
        }
        else return move.getNoEffectResult(opponent);
    }

    public String FutureSight(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        duel.data(user.getUUID()).futureSightUsed = true;
        duel.data(user.getUUID()).futureSightTurns = 2;
        return "It will strike in 2 turns!";
    }

    public String Psywave(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int damage = (int)(user.getLevel() * (new Random().nextInt(101) + 50) / 100D);

        if(opponent.isType(Type.DARK)) return move.getNoEffectResult(opponent);
        else
        {
            return MoveEffectBuilder.make(user, opponent, duel, move)
                    .addFixedDamageEffect(damage)
                    .execute();
        }
    }

    public String MiracleEye(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return move.getNotImplementedResult();
    }

    public String Barrier(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatChangeEffect(Stat.DEF, 2, 100, true)
                .execute();
    }

    public String Amnesia(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatChangeEffect(Stat.SPDEF, 2, 100, true)
                .execute();
    }

    public String Psystrike(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String ZenHeadbutt(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.statusDamage(user, opponent, duel, move, StatusCondition.FLINCHED, 20);
    }

    public String HealPulse(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addFractionHealEffect(1 / 2D)
                .execute();
    }

    public String Gravity(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return move.getNotImplementedResult();
    }

    public String PhotonGeyser(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String PrismaticLaser(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String Extrasensory(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.statusDamage(user, opponent, duel, move, StatusCondition.FLINCHED, 10);
    }

    public String Imprison(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        duel.data(user.getUUID()).imprisonUsed = true;
        return opponent.getName() + " can no longer use moves that " + user.getName() + " knows!";
    }

    public String HyperspaceHole(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String TrickRoom(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addRoomEffect(Room.TRICK_ROOM)
                .execute();
    }

    public String WonderRoom(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addRoomEffect(Room.WONDER_ROOM)
                .execute() + " (WIP!) ";
    }

    public String MagicRoom(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addRoomEffect(Room.MAGIC_ROOM)
                .execute();
    }

    public String StoredPower(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int increases = Arrays.stream(Stat.values()).mapToInt(s -> user.changes().get(s)).filter(stage -> stage > 0).sum();
        move.setPower(20 + increases * 20);

        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String Teleport(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return move.getNotImplementedResult();
    }

    public String Meditate(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatChangeEffect(Stat.ATK, 1, 100, true)
                .execute();
    }

    public String Rest(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addFractionHealEffect(1)
                .addStatusEffect(StatusCondition.ASLEEP, 100, true)
                .execute();
    }

    public String MagicPowder(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        opponent.setType(Type.PSYCHIC);

        return opponent.getName() + " is now a Psychic Type!";
    }

    public String Telekinesis(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return move.getNotImplementedResult();
    }

    public String HeartSwap(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        Map<Stat, Integer> userStats = new HashMap<>();
        Map<Stat, Integer> oppStats = new HashMap<>();

        for(Stat s : Stat.values())
        {
            userStats.put(s, user.changes().get(s));
            oppStats.put(s, opponent.changes().get(s));
        }

        user.changes().clear();
        opponent.changes().clear();

        for(Stat s : Stat.values())
        {
            user.changes().change(s, oppStats.get(s));
            opponent.changes().change(s, userStats.get(s));
        }

        return user.getName() + " and " + opponent.getName() + "'s Stat Changes were swapped!";
    }

    public String MagicCoat(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return move.getNotImplementedResult();
    }

    public String AllySwitch(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return move.getNotImplementedResult();
    }

    public String CosmicPower(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatChangeEffect(
                        new StatChangeEffect(Stat.DEF, 1, 100, true)
                                .add(Stat.SPDEF, 1))
                .execute();
    }

    public String PsychicFangs(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        //TODO: Breaks Light Screen/Reflect
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String MistBall(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatChangeEffect(Stat.SPATK, -1, 50, false)
                .execute();
    }

    public String Kinesis(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addAccuracyChangeEffect(-1, 100, false)
                .execute();
    }

    public String LusterPurge(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatChangeEffect(Stat.SPDEF, -1, 50, false)
                .execute();
    }

    public String Synchronoise(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return user.getType().stream().anyMatch(opponent::isType) ? MoveEffectBuilder.defaultDamage(user, opponent, duel, move) : move.getNoEffectResult(opponent);
    }

    public String Trick(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        Item temp = user.getItem();

        user.setItem(opponent.getItem());
        opponent.setItem(temp);

        return user.getName() + " swapped items with " + opponent.getName() + "!";
    }

    public String HeartDance(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatusEffect(StatusCondition.FLINCHED, 30)
                .execute();
    }

    public String PsychicTerrain(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addTerrainEffect(Terrain.PSYCHIC_TERRAIN)
                .execute();
    }

    public String EerieSpell(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        //TODO: Lowers target's last used move PP by 3
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .execute();
    }

    public String ExpandingForce(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(duel.terrain.equals(Terrain.PSYCHIC_TERRAIN) && !duel.data(user.getUUID()).isRaised) move.setPower(1.5);

        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .execute();
    }

    public String LightScreen(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        duel.barriers[duel.playerIndexFromUUID(user.getUUID())].addBarrier(FieldBarrier.LIGHT_SCREEN, user.getItem().equals(Item.LIGHT_CLAY));

        return user.getName() + " set up a Light Screen Barrier!";
    }

    public String Reflect(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        duel.barriers[duel.playerIndexFromUUID(user.getUUID())].addBarrier(FieldBarrier.REFLECT, user.getItem().equals(Item.LIGHT_CLAY));

        return user.getName() + " set up a Reflect Barrier!";
    }

    public String GuardSplit(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        double userDEF = user.getStat(Stat.DEF) / user.changes().getModifier(Stat.DEF);
        double opponentDEF = opponent.getStat(Stat.DEF) / opponent.changes().getModifier(Stat.DEF);

        double userSPDEF = user.getStat(Stat.SPDEF) / user.changes().getModifier(Stat.SPDEF);
        double opponentSPDEF = opponent.getStat(Stat.SPDEF) / opponent.changes().getModifier(Stat.SPDEF);

        double avgDEF = (userDEF + opponentDEF) / 2;
        double avgSPDEF = (userSPDEF + opponentSPDEF) / 2;

        int def = (int)(avgDEF);
        int spdef = (int)(avgSPDEF);

        user.overrides().set(Stat.DEF, def);
        opponent.overrides().set(Stat.DEF, def);

        user.overrides().set(Stat.SPDEF, spdef);
        opponent.overrides().set(Stat.SPDEF, spdef);

        return user.getName() + " and " + opponent.getName() + "'s Defense and Special Defense were averaged!";
    }

    public String PowerSplit(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        double userATK = user.getStat(Stat.ATK) / user.changes().getModifier(Stat.ATK);
        double opponentATK = opponent.getStat(Stat.ATK) / opponent.changes().getModifier(Stat.ATK);

        double userSPATK = user.getStat(Stat.SPATK) / user.changes().getModifier(Stat.SPATK);
        double opponentSPATK = opponent.getStat(Stat.SPATK) / opponent.changes().getModifier(Stat.SPATK);

        double avgATK = (userATK + opponentATK) / 2;
        double avgSPATK = (userSPATK + opponentSPATK) / 2;

        int atk = (int)(avgATK);
        int spatk = (int)(avgSPATK);

        user.overrides().set(Stat.DEF, atk);
        opponent.overrides().set(Stat.DEF, atk);

        user.overrides().set(Stat.SPDEF, spatk);
        opponent.overrides().set(Stat.SPDEF, spatk);

        return user.getName() + " and " + opponent.getName() + "'s Attack and Special Attack were averaged!";
    }

    public String GuardSwap(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int userDEF = user.changes().get(Stat.DEF);
        int opponentDEF = opponent.changes().get(Stat.DEF);

        int userSPDEF = user.changes().get(Stat.SPDEF);
        int opponentSPDEF = user.changes().get(Stat.SPDEF);

        user.changes().clear(Stat.DEF);
        opponent.changes().clear(Stat.DEF);

        user.changes().clear(Stat.SPDEF);
        opponent.changes().clear(Stat.SPDEF);

        user.changes().change(Stat.DEF, opponentDEF);
        opponent.changes().change(Stat.DEF, userDEF);

        user.changes().change(Stat.SPDEF, opponentSPDEF);
        opponent.changes().change(Stat.SPDEF, userSPDEF);

        return user.getName() + " and " + opponent.getName() + "'s Defense and Special Defense stages were swapped!";
    }

    public String PowerSwap(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int userATK = user.changes().get(Stat.ATK);
        int opponentATK = opponent.changes().get(Stat.ATK);

        int userSPATK = user.changes().get(Stat.SPATK);
        int opponentSPATK = user.changes().get(Stat.SPATK);

        user.changes().clear(Stat.ATK);
        opponent.changes().clear(Stat.ATK);

        user.changes().clear(Stat.SPATK);
        opponent.changes().clear(Stat.SPATK);

        user.changes().change(Stat.ATK, opponentATK);
        opponent.changes().change(Stat.ATK, userATK);

        user.changes().change(Stat.SPATK, opponentSPATK);
        opponent.changes().change(Stat.SPATK, userSPATK);

        return user.getName() + " and " + opponent.getName() + "'s Attack and Special Attack stages were swapped!";
    }

    public String PsychoBoost(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatChangeEffect(Stat.SPATK, -2, 100, true)
                .execute();
    }

    public String PowerTrick(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        double userATK = user.getStat(Stat.ATK) / user.changes().getModifier(Stat.ATK);
        double userDEF = user.getStat(Stat.DEF) / user.changes().getModifier(Stat.DEF);

        user.overrides().set(Stat.ATK, (int)userDEF);
        user.overrides().set(Stat.DEF, (int)userATK);

        return user.getName() + "'s Attack and Defense were swapped!";
    }

    public String SpeedSwap(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        double userSPD = user.getStat(Stat.SPD) / user.changes().getModifier(Stat.SPD);
        double opponentSPD = opponent.getStat(Stat.SPD) / opponent.changes().getModifier(Stat.SPD);

        user.overrides().set(Stat.SPD, (int)opponentSPD);
        opponent.overrides().set(Stat.SPD, (int)userSPD);

        return user.getName() + " and " + opponent.getName() + "'s Speed stats were swapped!";
    }
}
