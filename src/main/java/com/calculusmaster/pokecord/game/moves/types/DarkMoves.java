package com.calculusmaster.pokecord.game.moves.types;

import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.elements.StatusCondition;
import com.calculusmaster.pokecord.game.enums.items.Item;
import com.calculusmaster.pokecord.game.moves.Move;
import com.calculusmaster.pokecord.game.moves.builder.MoveEffectBuilder;
import com.calculusmaster.pokecord.game.moves.builder.StatChangeEffect;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DarkMoves
{
    public String Bite(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.statusDamage(user, opponent, duel, move, StatusCondition.FLINCHED, 30);
    }

    public String Pursuit(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String Assurance(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(duel.data(user.getUUID()).lastDamageTaken > 0) move.setPower(move.getPower() * 2);

        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String Taunt(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        duel.data(user.getUUID()).tauntTurns = 3;
        return opponent.getName() + " is now unable to use Status moves for 3 turns!";
    }

    public String Payback(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(duel.first.equals(opponent.getUUID())) move.setPower(move.getPower() * 2);

        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String SuckerPunch(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String FieryWrath(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.statusDamage(user, opponent, duel, move, StatusCondition.FLINCHED, 20);
    }

    public String NastyPlot(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.statChangeDamage(user, opponent, duel, move, Stat.SPATK, 2, 100, true);
    }

    public String Memento(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        user.damage(user.getHealth());

        return user.getName() + " fainted and " + MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatChangeEffect(
                        new StatChangeEffect(Stat.ATK, -3, 100, false)
                                .add(Stat.SPATK, -3))
                .execute();
    }

    public String NightSlash(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addCritDamageEffect()
                .execute();
    }

    public String DarkPulse(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.statusDamage(user, opponent, duel, move, StatusCondition.FLINCHED, 20);
    }

    public String Crunch(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.statChangeDamage(user, opponent, duel, move, Stat.DEF, -1, 20, false);
    }

    public String Punishment(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int statIncreases = 0;
        //TODO: Check if this move is correctly implemented

        for(Stat s : Stat.values()) if(opponent.changes().get(s) > 0) statIncreases += (int)(opponent.changes().getModifier(s) * 2 - 2);

        move.setPower(statIncreases * 20 + 60);

        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String KnockOff(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(opponent.hasItem())
        {
            move.setPower(1.5 * move.getPower());
            opponent.removeItem();
        }

        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String HyperspaceFury(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.statChangeDamage(user, opponent, duel, move, Stat.DEF, -1, 100, true);
    }

    public String FeintAttack(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String BeatUp(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        List<Integer> basePowers = new ArrayList<>();
        for(Pokemon p : duel.getPlayers()[duel.playerIndexFromUUID(user.getUUID())].team)
            if(!p.hasAnyStatusCondition() && !p.isFainted()) basePowers.add(p.getBaseStat(Stat.ATK) / 10 + 5);

        int damage = 0;
        for(int p : basePowers)
        {
            move.setPower(p);
            damage += move.getDamage(user, opponent);
        }

        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addFixedDamageEffect(damage)
                .execute();
    }

    public String DarkVoid(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatusEffect(StatusCondition.ASLEEP, 100)
                .execute();
    }

    public String NightDaze(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addAccuracyChangeEffect(-1, 40, false)
                .execute();
    }

    public String FakeTears(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatChangeEffect(Stat.SPDEF, -2, 100, false)
                .execute();
    }

    public String TopsyTurvy(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        for(Stat s : Stat.values())
        {
            opponent.changes().change(s, opponent.changes().get(s) * -1 * 2);
        }

        return opponent.getName() + "'s Stat Changes were reversed!";
    }

    public String Switcheroo(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        Item temp = user.getItem();
        user.setItem(opponent.getItem());
        opponent.setItem(temp);

        return user.getName() + " and " + opponent.getName() + "'s Items were swapped!";
    }

    public String Fling(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return move.getNotImplementedResult();
    }

    public String Quash(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return move.getNotImplementedResult();
    }

    public String Embargo(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return move.getNotImplementedResult();
    }

    public String Flatter(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatusEffect(StatusCondition.CONFUSED, 100, false)
                .addStatChangeEffect(Stat.SPATK, 2, 100, false)
                .execute();
    }

    public String WickedBlow(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        Map<Stat, Integer> statChanges = Map.copyOf(opponent.changes().getAll());
        int accuracy = opponent.changes().getAccuracy();
        int evasion = opponent.changes().getEvasion();

        opponent.changes().clear();
        String result = MoveEffectBuilder.defaultDamage(user, opponent, duel, move);

        opponent.changes().set(statChanges);
        opponent.changes().changeAccuracy(accuracy);
        opponent.changes().changeEvasion(evasion);

        return result;
    }

    public String FalseSurrender(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    //TODO: Ignores changes to stats
    public String DarkestLariat(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }
}
