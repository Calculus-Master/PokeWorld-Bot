package com.calculusmaster.pokecord.game.moves.types;

import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.elements.StatusCondition;
import com.calculusmaster.pokecord.game.enums.elements.Type;
import com.calculusmaster.pokecord.game.moves.Move;
import com.calculusmaster.pokecord.game.moves.builder.MoveEffectBuilder;
import com.calculusmaster.pokecord.game.moves.builder.StatChangeEffect;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;

public class GhostMoves
{
    public String ShadowBall(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatChangeEffect(Stat.SPDEF, -1, 20, false)
                .execute();
    }

    public String Curse(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(user.isType(Type.GHOST))
        {
            return MoveEffectBuilder.make(user, opponent, duel, move)
                    .addStatusEffect(StatusCondition.CURSED)
                    .execute();
        }
        else
        {
            return MoveEffectBuilder.make(user, opponent, duel, move)
                    .addStatChangeEffect(
                            new StatChangeEffect(Stat.ATK, 1, 100, true)
                                    .add(Stat.DEF, 1)
                                    .add(Stat.SPD, -1))
                    .execute();
        }
    }

    public String OminousWind(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatChangeEffect(
                        new StatChangeEffect(Stat.ATK, 1, 10, true)
                                .add(Stat.DEF, 1)
                                .add(Stat.SPATK, 1)
                                .add(Stat.SPDEF, 1)
                                .add(Stat.SPD, 1))
                .execute();
    }

    public String ShadowSneak(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String ShadowClaw(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addCritDamageEffect()
                .execute();
    }

    public String ShadowForce(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(duel.data(user.getUUID()).shadowForceUsed)
        {
            duel.data(user.getUUID()).shadowForceUsed = false;
            return Move.simpleDamageMove(user, opponent, duel, move);
        }
        else
        {
            duel.data(user.getUUID()).shadowForceUsed = true;
            return user.getName() + " disappeared from sight!";
        }
    }

    public String Hex(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(opponent.hasAnyStatusCondition()) move.setPower(130);

        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String Lick(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statusDamageMove(user, opponent, duel, move, StatusCondition.PARALYZED, 30);
    }

    public String ShadowPunch(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String Spite(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return move.getNotImplementedResult();
    }

    public String NightShade(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addFixedDamageEffect(user.getLevel())
                .execute();
    }

    public String Nightmare(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatusEffect(StatusCondition.NIGHTMARE)
                .execute();
    }

    public String SpectralThief(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        for(Stat s : Stat.values())
        {
            if(opponent.getStageChange(s) > 0)
            {
                user.changeStatMultiplier(s, opponent.getStageChange(s));
                opponent.changeStatMultiplier(s, opponent.getStageChange(s) * -1);
            }
        }

        return Move.simpleDamageMove(user, opponent, duel, move) + " " + user.getName() + " copied all of " + opponent.getName() + "'s Stat Boosts!";
    }

    public String MoongeistBeam(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String ConfuseRay(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatusEffect(StatusCondition.CONFUSED)
                .execute();
    }

    public String Astonish(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statusDamageMove(user, opponent, duel, move, StatusCondition.FLINCHED, 30);
    }

    public String SpiritShackle(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String PhantomForce(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(duel.data(user.getUUID()).phantomForceUsed)
        {
            duel.data(user.getUUID()).phantomForceUsed = false;
            return Move.simpleDamageMove(user, opponent, duel, move);
        }
        else
        {
            duel.data(user.getUUID()).phantomForceUsed = true;
            return user.getName() + " disappeared from sight!";
        }
    }

    public String DestinyBond(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        duel.data(user.getUUID()).destinyBondUsed = true;
        return "";
    }

    public String AstralBarrage(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }
}