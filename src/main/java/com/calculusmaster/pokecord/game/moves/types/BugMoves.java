package com.calculusmaster.pokecord.game.moves.types;

import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.enums.elements.EntryHazard;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.elements.StatusCondition;
import com.calculusmaster.pokecord.game.moves.Move;
import com.calculusmaster.pokecord.game.moves.builder.MoveEffectBuilder;
import com.calculusmaster.pokecord.game.moves.builder.StatChangeEffect;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;

import java.util.SplittableRandom;

public class BugMoves
{
    public String SilverWind(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatChangeEffect(
                        new StatChangeEffect(Stat.ATK, 1, 10, true)
                                .add(Stat.DEF, 1)
                                .add(Stat.SPATK, 1)
                                .add(Stat.SPDEF, 1))
                .execute();
    }

    public String BugBuzz(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatChangeEffect(Stat.SPDEF, -1, 10, false)
                .execute();
    }

    public String RagePowder(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return move.getNotImplementedResult();
    }

    public String QuiverDance(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatChangeEffect(
                        new StatChangeEffect(Stat.SPATK, 1, 100, true)
                                .add(Stat.SPDEF, 1)
                                .add(Stat.SPD, 1))
                .execute();
    }

    public String Twineedle(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addFixedMultiStrikeEffect(2)
                .addStatusEffect(StatusCondition.POISONED, 20)
                .execute();
    }

    public String PinMissile(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.multiDamage(user, opponent, duel, move);
    }

    public String FellStinger(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        String result = MoveEffectBuilder.make(user, opponent, duel, move).addDamageEffect().execute();

        if(opponent.isFainted())
        {
            user.changes().change(Stat.ATK, 3);
            result += " " + user.getName() + "'s Attack rose by 3 stages!";
        }

        return result;
    }

    public String LeechLife(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addDamageHealEffect(1 / 2D)
                .execute();
    }

    public String StickyWeb(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        duel.hazardData(opponent.getUUID()).addHazard(EntryHazard.STICKY_WEB);
        return user.getName() + " laid a Sticky Web trap!";
    }

    public String Infestation(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatusEffect(StatusCondition.BOUND, 100)
                .execute();
    }

    //TODO: Switch out immediately after attacking
    public String UTurn(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String Lunge(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.statChangeDamage(user, opponent, duel, move, Stat.ATK, -1, 100, false);
    }

    public String Steamroller(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.statusDamage(user, opponent, duel, move, StatusCondition.FLINCHED, 30);
    }

    public String DefendOrder(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatChangeEffect(
                        new StatChangeEffect(Stat.DEF, 1, 100, true)
                                .add(Stat.SPDEF, 1))
                .execute();
    }

    public String TailGlow(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatChangeEffect(Stat.SPATK, 3, 100, true)
                .execute();
    }

    public String FirstImpression(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return duel.turn == 1 || duel.data(user.getUUID()).firstAfterSwap ? MoveEffectBuilder.defaultDamage(user, opponent, duel, move) : move.getNothingResult();
    }

    public String StruggleBug(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatChangeEffect(Stat.SPATK, -1, 100, false)
                .execute();
    }

    public String XScissor(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String Megahorn(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String StringShot(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatChangeEffect(Stat.SPD, -2, 100, false)
                .execute();
    }

    public String SpiderWeb(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addCustomEffect(() -> {
                    duel.data(opponent.getUUID()).canSwap = false;
                    return opponent.getName() + " is trapped by the web!";
                })
                .execute();
    }

    public String FuryCutter(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        duel.data(user.getUUID()).furyCutterUsed = true;
        duel.data(user.getUUID()).furyCutterTurns++;

        move.setPower(Math.min(40 * duel.data(user.getUUID()).furyCutterTurns, 160));

        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .execute();
    }

    public String SignalBeam(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatusEffect(StatusCondition.CONFUSED, 10)
                .execute();
    }

    public String AttackOrder(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addCritDamageEffect()
                .execute();
    }

    public String HealOrder(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addFractionHealEffect(1 / 2D)
                .execute();
    }

    public String SkitterSmack(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatChangeEffect(Stat.SPDEF, -1, 100, false)
                .execute();
    }

    public String PollenPuff(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addConditionalEffect(new SplittableRandom().nextInt(100) < 50, MoveEffectBuilder::addDamageEffect, b -> b.addFractionHealEffect(1 / 2D))
                .execute();
    }

    public String Powder(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(duel.first.equals(opponent.getUUID())) return  move.getNoEffectResult(opponent);
        else
        {
            duel.data(opponent.getUUID()).isCoveredPowder = true;

            return opponent.getName() + " is covered with Powder!";
        }
    }
}
