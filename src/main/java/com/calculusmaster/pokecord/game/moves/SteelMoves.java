package com.calculusmaster.pokecord.game.moves;

import com.calculusmaster.pokecord.game.Duel;
import com.calculusmaster.pokecord.game.Move;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.enums.elements.Stat;

import java.util.Random;

public class SteelMoves
{
    public String IronDefense(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        user.changeStatMultiplier(Stat.DEF, 2);

        return user.getName() + "'s Defense rose by 2 stages!";
    }

    public String FlashCannon(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int damage = move.getDamage(user, opponent);
        opponent.damage(damage, duel);

        if(new Random().nextInt(100) < 10)
        {
            opponent.changeStatMultiplier(Stat.SPDEF, -1);
            return move.getDamageResult(opponent, damage) + " " + opponent.getName() + "'s Special Defense was lowered by 1 stage!";
        }

        return move.getDamageResult(opponent, damage);
    }

    public String MetalBurst(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int damage = (int)(duel.lastDamage * 1.5);
        opponent.damage(damage, duel);

        return move.getDamageResult(opponent, damage);
    }

    public String MetalClaw(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int damage = move.getDamage(user, opponent);
        opponent.damage(damage, duel);

        if(new Random().nextInt(100) < 10)
        {
            user.changeStatMultiplier(Stat.ATK, 1);

            return move.getDamageResult(opponent, damage) + " " + user.getName() + "'s Attack rose by 1 stage!";
        }

        return move.getDamageResult(opponent, damage);
    }

    public String MagnetBomb(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String MirrorShot(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String MetalSound(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        opponent.changeStatMultiplier(Stat.SPDEF, -2);
        return opponent.getName() + "'s Special Defense was lowered by 2 stages!";
    }

    public String GyroBall(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        move.setPower((int)(25 * (double)opponent.getStat(Stat.SPD) / user.getStat(Stat.SPD)));

        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String IronTail(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        boolean lower = new Random().nextInt(100) < 30;

        if(lower) opponent.changeStatMultiplier(Stat.DEF, -1);

        return Move.simpleDamageMove(user, opponent, duel, move) + (lower ? " " + opponent.getName() + "'s Defense was lowered by 1 stage!" : "");
    }
}
