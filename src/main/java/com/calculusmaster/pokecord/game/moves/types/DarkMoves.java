package com.calculusmaster.pokecord.game.moves.types;

import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.enums.elements.Ability;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.elements.StatusCondition;
import com.calculusmaster.pokecord.game.enums.items.Item;
import com.calculusmaster.pokecord.game.moves.Move;
import com.calculusmaster.pokecord.game.moves.builder.MoveEffectBuilder;
import com.calculusmaster.pokecord.game.moves.builder.StatChangeEffect;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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
        if(opponent.hasAbility(Ability.AROMA_VEIL)) return Ability.AROMA_VEIL.formatActivation(opponent.getName(), move.getName() + " has no effect on " + opponent.getName() + "!");

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
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addSelfFaintEffect()
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

        for(Stat s : Stat.values()) if(opponent.changes().get(s) > 0) statIncreases += opponent.changes().get(s);
        if(opponent.changes().getAccuracy() > 0) statIncreases += opponent.changes().getAccuracy();
        if(opponent.changes().getEvasion() > 0) statIncreases += opponent.changes().getEvasion();

        move.setPower(Math.min(statIncreases * 20 + 60, 200));

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
        opponent.setStatChangesIgnored(true);
        String result = MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
        opponent.setStatChangesIgnored(false);
        return result;
    }

    public String FalseSurrender(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String DarkestLariat(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        opponent.setStatChangesIgnored(true);
        String result = MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
        opponent.setStatChangesIgnored(false);
        return result;
    }

    public String Thief(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        String stolenItem = "";

        if(!user.hasItem() && opponent.hasItem())
        {
            Item item = opponent.getItem();
            user.setItem(item);
            opponent.removeItem();

            stolenItem = user.getName() + " stole " + opponent.getName() + "'s " + item.getName() + "! ";
        }

        return stolenItem + MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String HoneClaws(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatChangeEffect(Stat.ATK, 1, 100, true)
                .addAccuracyChangeEffect(1, 100, true)
                .execute();
    }

    public String FoulPlay(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return user.getName() + " is using " + opponent.getName() + "'s Attack power! " + MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String BrutalSwing(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String Snarl(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.statChangeDamage(user, opponent, duel, move, Stat.SPATK, -1, 100, false);
    }

    public String PartingShot(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        //TODO: User switches out of battle
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatChangeEffect(
                        new StatChangeEffect(Stat.ATK, -1, 100, false)
                        .add(Stat.SPATK, -1))
                .execute();
    }

    public String ThroatChop(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        duel.data(opponent.getUUID()).unableToUseSoundMoves = true;
        duel.data(opponent.getUUID()).unableToUseSoundMovesTurns = 2;

        return opponent.getName() + " cannot use Sound-based Moves for 2 turns! " + MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String PowerTrip(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        AtomicInteger power = new AtomicInteger(20);

        Arrays.stream(Stat.values()).filter(s -> user.changes().get(s) > 0).map(s -> user.changes().get(s)).forEach(i -> power.getAndAdd(20 * i));
        if(user.changes().getAccuracy() > 0) power.getAndAdd(user.changes().getAccuracy() * 20);
        if(user.changes().getEvasion() > 0) power.getAndAdd(user.changes().getEvasion() * 20);

        int effectivePower = power.get();
        move.setPower(effectivePower);

        return "The move power is " + effectivePower + "! " + MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String JawLock(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        duel.data(user.getUUID()).canSwap = false;
        duel.data(opponent.getUUID()).canSwap = false;

        return user.getName() + " and " + opponent.getName() + " are prevented from swapping out! " + MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String Torment(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(opponent.hasAbility(Ability.AROMA_VEIL)) return Ability.AROMA_VEIL.formatActivation(opponent.getName(), move.getName() + " has no effect on " + opponent.getName() + "!");

        duel.data(opponent.getUUID()).isTormented = true;
        return opponent.getName() + " was tormented! " + opponent.getName() + " can't use the same move twice in a row!";
    }

    public String Obstruct(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(duel.first.equals(user.getUUID())) return move.getNothingResult();
        else
        {
            duel.data(user.getUUID()).obstructUsed = true;
            return user.getName() + " set up an obstruction!";
        }
    }

    public String Comeuppance(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(duel.first.equals(user.getUUID())) return move.getNothingResult();
        else
        {
            int damage = duel.data(user.getUUID()).lastDamageTaken;

            return MoveEffectBuilder.make(user, opponent, duel, move)
                    .addFixedDamageEffect((int)(damage * 1.5))
                    .execute();
        }
    }
}
