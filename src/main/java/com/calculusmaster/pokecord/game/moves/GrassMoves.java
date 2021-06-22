package com.calculusmaster.pokecord.game.moves;

import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.Move;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.elements.StatusCondition;
import com.calculusmaster.pokecord.game.enums.elements.Type;
import com.calculusmaster.pokecord.game.moves.builder.MoveEffectBuilder;

public class GrassMoves
{
    public String RazorLeaf(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addCritDamageEffect()
                .execute();
    }

    public String VineWhip(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String LeechSeed(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        //TODO: LeechSeed sucks health each turn, right now its coded to steal max health once

        if(!opponent.getType()[0].equals(Type.GRASS) && !opponent.getType()[1].equals(Type.GRASS))
        {
            int hpGain = opponent.getStat(Stat.HP) / 8;

            user.heal(hpGain);
            opponent.damage(hpGain);

            return user.getName() + " leeched " + hpGain + " HP from " + opponent.getName() + "!";
        }
        else return move.getNoEffectResult(opponent);
    }

    public String SleepPowder(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatusEffect(StatusCondition.ASLEEP)
                .execute();
    }

    public String SeedBomb(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String Synthesis(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addFractionHealEffect(switch(duel.weather) {
                    case CLEAR -> 1 / 2D;
                    case HARSH_SUNLIGHT -> 2 / 3D;
                    default -> 1 / 4D;
                })
                .execute();
    }

    public String WorrySeed(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(user.hasStatusCondition(StatusCondition.ASLEEP))
        {
            user.removeStatusCondition(StatusCondition.ASLEEP);
            return user.getName() + " is now awake!";
        }
        else return move.getNothingResult();
    }

    public String SolarBeam(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String PetalBlizzard(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String PetalDance(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addStatusEffect(StatusCondition.CONFUSED, 100, true)
                .execute();
    }

    public String StunSpore(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatusEffect(StatusCondition.PARALYZED)
                .execute();
    }

    public String FrenzyPlant(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String Leafage(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String LeafBlade(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addCritDamageEffect()
                .execute();
    }

    public String LeafStorm(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statChangeDamageMove(user, opponent, duel, move, Stat.SPATK, -2, 100, true);
    }

    public String Ingrain(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return move.getNotImplementedResult();
    }

    public String ForestsCurse(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return move.getNotImplementedResult();
    }

    public String WoodHammer(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addRecoilEffect(1 / 3D)
                .execute();
    }

    public String CottonGuard(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatChangeEffect(Stat.DEF, 3, 100, true)
                .execute();
    }

    public String HornLeech(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addDamageEffect()
                .addDamageHealEffect(1 / 2D)
                .execute();
    }

    public String SolarBlade(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String GravApple(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statChangeDamageMove(user, opponent, duel, move, Stat.DEF, -1, 100, false);
    }

    public String BulletSeed(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.multihitDamageMove(user, opponent, duel, move);
    }

    public String Spore(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return MoveEffectBuilder.make(user, opponent, duel, move)
                .addStatusEffect(StatusCondition.ASLEEP)
                .execute();
    }

    public String GrassKnot(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(opponent.getWeight() < 10) move.setPower(20);
        else if(opponent.getWeight() < 25) move.setPower(40);
        else if(opponent.getWeight() < 50) move.setPower(60);
        else if(opponent.getWeight() < 100) move.setPower(80);
        else if(opponent.getWeight() < 200) move.setPower(100);
        else move.setPower(120);

        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String PowerWhip(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }
}
