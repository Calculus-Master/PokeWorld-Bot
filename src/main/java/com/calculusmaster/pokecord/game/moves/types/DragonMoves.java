package com.calculusmaster.pokecord.game.moves.types;

import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.elements.StatusCondition;
import com.calculusmaster.pokecord.game.moves.Move;
import com.calculusmaster.pokecord.game.moves.builder.MoveEffectBuilder;
import com.calculusmaster.pokecord.game.moves.builder.StatChangeEffect;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;

import java.util.Arrays;

public class DragonMoves
{
    public String DragonClaw(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String DragonRage(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addFixedDamageEffect(40)
                .execute();
    }

    public String DragonBreath(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.statusDamage(user, opponent, duel, move, StatusCondition.PARALYZED, 30);
    }

    public String DragonDance(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatChangeEffect(
                        new StatChangeEffect(Stat.ATK, 1, 100, true)
                                .add(Stat.SPD, 1))
                .execute();
    }

    public String Outrage(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatusEffect(StatusCondition.CONFUSED, 100, true)
                .execute();
    }

    public String Twister(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(duel.data(opponent.getUUID()).flyUsed || duel.data(opponent.getUUID()).bounceUsed) move.setPower(2 * move.getPower());
        return MoveEffectBuilder.statusDamage(user, opponent, duel, move, StatusCondition.FLINCHED, 30);
    }

    public String DracoMeteor(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatChangeEffect(Stat.SPATK, -2, 100, true)
                .execute();
    }

    public String RoarOfTime(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String SpacialRend(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addCritDamageEffect()
                .execute();
    }

    public String DragonEnergy(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        move.setPower((int)(150 * user.getHealth() / (double)user.getStat(Stat.HP)));

        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String Eternabeam(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String DragonPulse(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String CoreEnforcer(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String ClangingScales(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.statChangeDamage(user, opponent, duel, move, Stat.DEF, -1, 100, true);
    }

    public String DragonTail(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String DragonDarts(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addFixedMultiStrikeEffect(2)
                .execute();
    }

    public String DragonRush(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.statusDamage(user, opponent, duel, move, StatusCondition.FLINCHED, 20);
    }

    public String ScaleShot(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addFixedMultiStrikeEffect(2)
                .addStatChangeEffect(Stat.SPD, 1, 100, true)
                .addStatChangeEffect(Stat.DEF, -1, 100, true)
                .execute();
    }

    public String DynamaxCannon(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(opponent.isDynamaxed()) move.setPower(move.getPower() * 2);

        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String DualChop(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.multiDamage(user, opponent, duel, move, 2);
    }

    public String DragonHammer(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String ClangorousSoul(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(Arrays.stream(Stat.values()).allMatch(s -> user.changes().get(s) == 6) || user.getHealth() < user.getMaxHealth() / 3) return move.getNoEffectResult(opponent);
        else
        {
            return MoveEffectBuilder.make(user, opponent, duel, move)
                    .addFractionSelfDamageEffect(1 / 3D)
                    .addStatChangeEffect(new StatChangeEffect(1, 100, true))
                    .execute();
        }
    }

    public String BreakingSwipe(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.statChangeDamage(user, opponent, duel, move, Stat.ATK, -1, 100, false);
    }
}
