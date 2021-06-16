package com.calculusmaster.pokecord.game.moves;

import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.Move;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.elements.StatusCondition;
import com.calculusmaster.pokecord.game.enums.elements.Type;

public class GrassMoves
{
    public String RazorLeaf(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        user.setCrit(3);

        int damage = move.getDamage(user, opponent);
        opponent.damage(damage);

        user.setCrit(1);

        return move.getDamageResult(opponent, damage);
    }

    public String VineWhip(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int damage = move.getDamage(user, opponent);
        opponent.damage(damage);

        return move.getDamageResult(opponent, damage);
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
        opponent.addStatusCondition(StatusCondition.ASLEEP);

        return opponent.getName() + " is asleep!";
    }

    public String SeedBomb(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int damage = move.getDamage(user, opponent);
        opponent.damage(damage);

        return move.getDamageResult(opponent, damage);
    }

    public String Synthesis(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int healAmount = user.getStat(Stat.HP);

        switch (duel.weather)
        {
            case CLEAR -> healAmount /= 2;
            case HARSH_SUNLIGHT -> healAmount = healAmount * 2 / 3;
            default -> healAmount /= 4;
        }

        user.heal(healAmount);

        return user.getName() + " healed " + healAmount + " HP!";
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
        int damage = move.getDamage(user, opponent);
        opponent.damage(damage);

        return move.getDamageResult(opponent, damage);
    }

    public String PetalDance(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int damage = move.getDamage(user, opponent);
        opponent.damage(damage);

        opponent.addStatusCondition(StatusCondition.CONFUSED);

        return move.getDamageResult(opponent, damage) + " " + opponent.getName() + " is now confused!";
    }

    public String StunSpore(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        opponent.addStatusCondition(StatusCondition.PARALYZED);

        return opponent.getName() + " is paralyzed!";
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
        user.setCrit(3);

        int damage = move.getDamage(user, opponent);
        opponent.damage(damage);

        user.setCrit(1);

        return move.getDamageResult(opponent, damage);
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
        int damage = move.getDamage(user, opponent);
        opponent.damage(damage);
        user.damage(damage / 3);

        return move.getDamageResult(opponent, damage) + " " + move.getRecoilDamageResult(user, damage / 3);
    }

    public String CottonGuard(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        user.changeStatMultiplier(Stat.DEF, 3);

        return user.getName() + "'s Defense rose by 3 stages!";
    }

    public String HornLeech(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int damage = move.getDamage(user, opponent);

        opponent.damage(damage);
        user.heal(damage / 2);

        return move.getDamageResult(opponent, damage) + " " + user.getName() + " healed for " + (damage / 2) + " HP!";
    }

    public String SolarBlade(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String GravApple(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statChangeDamageMove(user, opponent, duel, move, Stat.DEF, -1, 100, false);
    }
}
