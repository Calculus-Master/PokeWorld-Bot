package com.calculusmaster.pokecord.game.moves.types;

import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.elements.StatusCondition;
import com.calculusmaster.pokecord.game.enums.elements.Type;
import com.calculusmaster.pokecord.game.enums.items.Item;
import com.calculusmaster.pokecord.game.moves.Move;
import com.calculusmaster.pokecord.game.moves.builder.MoveEffectBuilder;
import com.calculusmaster.pokecord.game.moves.builder.StatChangeEffect;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.pokemon.augments.PokemonAugment;

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
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
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
            return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
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

        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String Lick(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.statusDamage(user, opponent, duel, move, StatusCondition.PARALYZED, 30);
    }

    public String ShadowPunch(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
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
            if(opponent.changes().get(s) > 0)
            {
                int stage = opponent.changes().get(s);
                user.changes().change(s, stage);
                opponent.changes().change(s, stage * -1);
            }
        }

        String augment = "";
        if(user.hasAugment(PokemonAugment.SPECTRAL_SUPERCHARGE))
        {
            move.setPower(1.5);
            augment = " Its power was supercharged by the %s Augment!".formatted(PokemonAugment.SPECTRAL_SUPERCHARGE.getAugmentName());
        }

        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move) + augment + " " + user.getName() + " stole all of " + opponent.getName() + "'s Stat Boosts!";
    }

    public String MoongeistBeam(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String ConfuseRay(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatusEffect(StatusCondition.CONFUSED)
                .execute();
    }

    public String Astonish(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.statusDamage(user, opponent, duel, move, StatusCondition.FLINCHED, 30);
    }

    public String SpiritShackle(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String PhantomForce(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(duel.data(user.getUUID()).phantomForceUsed)
        {
            duel.data(user.getUUID()).phantomForceUsed = false;
            return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
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
        return MoveEffectBuilder.defaultDamage(user, opponent, duel, move);
    }

    public String Poltergeist(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return !opponent.getItem().equals(Item.NONE) ? MoveEffectBuilder.defaultDamage(user, opponent, duel, move) : move.getNothingResult();
    }

    public String ShadowBone(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatChangeEffect(Stat.DEF, -1, 20, false)
                .execute();
    }

    public String TrickOrTreat(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(opponent.isType(Type.GHOST)) return move.getNoEffectResult(opponent);
        else
        {
            opponent.addType(Type.GHOST);
            return opponent.getName() + " is now partially a Ghost Type!";
        }
    }
}
