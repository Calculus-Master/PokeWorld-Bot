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

    public String GuardSwap(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return move.getNotImplementedResult();
    }

    public String PowerSwap(Pokemon user, Pokemon opponent, Duel duel, Move move)
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
}
