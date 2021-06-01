package com.calculusmaster.pokecord.game.moves;

import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.Move;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.elements.StatusCondition;

import java.util.Random;

public class FlyingMoves
{
    public String AirSlash(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int damage = move.getDamage(user, opponent);
        opponent.damage(damage);

        if(new Random().nextInt(100) < 30)
        {
            opponent.addStatusCondition(StatusCondition.FLINCHED);
            return move.getDamageResult(opponent, damage) + " " + opponent.getName() + " flinched!";
        }

        return move.getDamageResult(opponent, damage);
    }

    public String Gust(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(duel.data(opponent.getUUID()).flyUsed || duel.data(opponent.getUUID()).bounceUsed) move.setPower(2 * move.getPower());
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String Tailwind(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return move.getNotImplementedResult();
    }

    public String Roost(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        user.heal(user.getStat(Stat.HP) / 2);

        return user.getName() + " healed " + (user.getStat(Stat.HP) / 2) + " HP!";
    }

    public String Hurricane(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statusDamageMove(user, opponent, duel, move, StatusCondition.CONFUSED, 30);
    }

    public String Peck(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String DrillPeck(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String Pluck(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String WingAttack(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String SkyAttack(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        user.setCrit(3);
        return Move.statusDamageMove(user, opponent, duel, move, StatusCondition.FLINCHED, 30);
    }

    public String FeatherDance(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        opponent.changeStatMultiplier(Stat.ATK, -2);

        return opponent.getName() + "'s Attack was lowered by 2 stages!";
    }

    public String DragonAscent(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        user.changeStatMultiplier(Stat.DEF, -1);
        user.changeStatMultiplier(Stat.SPDEF, -1);

        return Move.simpleDamageMove(user, opponent, duel, move) + " " + user.getName() + "'s Defense and Special Defense were lowered by 1 stage!";
    }

    public String AerialAce(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String Fly(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(duel.data(user.getUUID()).flyUsed)
        {
            duel.data(user.getUUID()).flyUsed = false;
            return Move.simpleDamageMove(user, opponent, duel, move);
        }
        else
        {
            duel.data(user.getUUID()).flyUsed = true;
            return user.getName() + " flew high up!";
        }
    }

    public String Bounce(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(duel.data(user.getUUID()).bounceUsed)
        {
            duel.data(user.getUUID()).bounceUsed = false;
            return Move.statusDamageMove(user, opponent, duel, move, StatusCondition.PARALYZED, 30);
        }
        else
        {
            duel.data(user.getUUID()).bounceUsed = true;
            return user.getName() + " sprung up!";
        }
    }

    public String RazorLeaf(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int damage = move.getDamage(user, opponent);
        opponent.damage(damage);
        user.damage(damage / 3);

        return move.getDamageResult(opponent, damage) + " " + move.getRecoilDamageResult(user, damage / 3);
    }
}
