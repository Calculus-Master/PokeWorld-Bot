package com.calculusmaster.pokecord.game.moves;

import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.Move;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.duel.DuelHelper;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.elements.StatusCondition;
import com.calculusmaster.pokecord.game.enums.elements.Type;

import java.util.Random;

public class PsychicMoves
{
    public String CalmMind(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        user.changeStatMultiplier(Stat.SPATK, 1);
        user.changeStatMultiplier(Stat.SPDEF, 1);

        return "It raised " + user.getName() + "'s Special Attack and Special Defense by 1 stage!";
    }

    public String Psyshock(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int damage = move.getDamage(user, opponent);
        opponent.damage(damage);

        return move.getDamageResult(opponent, damage);
    }

    public String Confusion(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int damage = move.getDamage(user, opponent);
        opponent.damage(damage);

        if(new Random().nextInt(100) < 10)
        {
            opponent.addStatusCondition(StatusCondition.CONFUSED);

            return move.getDamageResult(opponent, damage) + " " + opponent.getName() + " is confused!";
        }

        return move.getDamageResult(opponent, damage);
    }

    public String Psybeam(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Confusion(user, opponent, duel, move);
    }

    public String Agility(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        user.changeStatMultiplier(Stat.SPD, 2);

        return user.getName() + "'s Speed rose by 2 stages!";
    }

    public String LightScreen(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return move.getNotImplementedResult();
    }

    public String Psychic(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        boolean lower = new Random().nextInt(100) < 10;

        if(lower) opponent.changeStatMultiplier(Stat.SPDEF, -1);

        return Move.simpleDamageMove(user, opponent, duel, move) + (lower ? " " + opponent.getName() + "'s Special Defense lowered by 1 stage!" : "");
    }

    public String PsychoShift(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(user.getActiveStatusConditions().isEmpty()) return move.getNothingResult();
        else
        {
            for(StatusCondition s : user.getStatusConditionMap().keySet())
            {
                if(user.hasStatusCondition(s))
                {
                    opponent.addStatusCondition(s);
                    user.removeStatusCondition(s);
                }
            }

            return user.getName() + " transferred all Status Conditions to " + opponent.getName() + "!";
        }
    }

    public String Hypnosis(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        opponent.addStatusCondition(StatusCondition.ASLEEP);

        return opponent.getName() + " is asleep!";
    }

    public String PsychoCut(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        user.setCrit(3);

        int damage = move.getDamage(user, opponent);
        opponent.damage(damage);

        user.setCrit(1);

        return move.getDamageResult(opponent, damage);
    }

    public String FreezingGlare(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statusDamageMove(user, opponent, duel, move, StatusCondition.FROZEN, 10);
    }

    public String DreamEater(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(opponent.hasStatusCondition(StatusCondition.ASLEEP))
        {
            int damage = move.getDamage(user, opponent);

            opponent.damage(damage);
            user.heal(damage / 2);

            return move.getDamageResult(opponent, damage) + " " + user.getName() + " healed for " + (damage / 2) + " HP!";
        }
        else return move.getNoEffectResult(opponent);
    }

    public String FutureSight(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        duel.data(user.getUUID()).futureSightUsed = true;
        duel.data(user.getUUID()).futureSightTurns = 2;
        return "The move will strike in 2 turns!";
    }

    public String Psywave(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int damage = (int)(user.getLevel() * (new Random().nextInt(101) + 50) / 100D);

        if(opponent.isType(Type.DARK)) return move.getNoEffectResult(opponent);
        else
        {
            opponent.damage(damage);

            return move.getDamageResult(opponent, damage);
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
        user.changeStatMultiplier(Stat.DEF, 2);

        return user.getName() + "'s Defense rose by 2 stages!";
    }

    public String Amnesia(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        user.changeStatMultiplier(Stat.SPDEF, 2);

        return user.getName() + "'s Special Defense rose by 2 stages!";
    }

    public String Psystrike(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String ZenHeadbutt(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statusDamageMove(user, opponent, duel, move, StatusCondition.FLINCHED, 20);
    }

    public String HealPulse(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        user.heal(user.getStat(Stat.HP) / 2);
        return user.getName() + " healed for " + (user.getStat(Stat.HP) / 2) + " HP!";
    }

    public String Gravity(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return move.getNotImplementedResult();
    }

    public String PhotonGeyser(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String PrismaticLaser(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String Extrasensory(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statusDamageMove(user, opponent, duel, move, StatusCondition.FLINCHED, 10);
    }

    public String Imprison(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        duel.data(user.getUUID()).imprisonUsed = true;
        return opponent.getName() + " can no longer use moves that " + user.getName() + " knows!";
    }

    public String HyperspaceHole(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String TrickRoom(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        duel.room = DuelHelper.Room.TRICK_ROOM;
        duel.roomTurns = 5;

        return user.getName() + " created a strange area!";
    }

    public String WonderRoom(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        duel.room = DuelHelper.Room.WONDER_ROOM;
        duel.roomTurns = 5;

        return user.getName() + " created a strange area! (WIP)";
    }

    public String MagicRoom(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        duel.room = DuelHelper.Room.MAGIC_ROOM;
        duel.roomTurns = 5;

        return user.getName() + " created a strange area!";
    }

    public String StoredPower(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int power = 20;

        for(Stat s : Stat.values()) if(user.getStatMultiplier(s) > 0) power += 20 * user.getStatMultiplier(s);

        move.setPower(power);

        return Move.simpleDamageMove(user, opponent, duel, move);
    }
}
