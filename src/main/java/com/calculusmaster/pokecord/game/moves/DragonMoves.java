package com.calculusmaster.pokecord.game.moves;

import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.Move;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.elements.StatusCondition;
import com.calculusmaster.pokecord.game.enums.elements.Type;

import java.util.Random;

public class DragonMoves
{
    public String DragonClaw(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int damage = move.getDamage(user, opponent);
        opponent.damage(damage);
        return move.getDamageResult(opponent, damage);
    }

    public String DragonRage(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        opponent.damage(40);

        return move.getDamageResult(opponent, 40);
    }

    public String DragonBreath(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int damage = move.getDamage(user, opponent);
        opponent.damage(damage);

        if(!opponent.getType()[0].equals(Type.ELECTRIC) && !opponent.getType()[1].equals(Type.ELECTRIC) && new Random().nextInt(100) < 30)
        {
            opponent.addStatusCondition(StatusCondition.PARALYZED);
            return move.getDamageResult(opponent, damage) + " " + opponent.getName() + " is paralyzed!";
        }

        return move.getDamageResult(opponent, damage);
    }

    public String DragonDance(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        user.changeStatMultiplier(Stat.ATK, 1);
        user.changeStatMultiplier(Stat.SPD, 1);

        return user.getName() + "'s Attack and Speed rose by 1 stage!";
    }

    public String Outrage(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        user.addStatusCondition(StatusCondition.CONFUSED);

        return Move.simpleDamageMove(user, opponent, duel, move) + " " + user.getName() + " is confused!";
    }

    public String Twister(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(duel.data(opponent.getUUID()).flyUsed || duel.data(opponent.getUUID()).bounceUsed) move.setPower(2 * move.getPower());
        return Move.statusDamageMove(user, opponent, duel, move, StatusCondition.FLINCHED, 30);
    }

    public String DracoMeteor(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        String results = Move.simpleDamageMove(user, opponent, duel, move);
        user.changeStatMultiplier(Stat.SPATK, -2);
        return results + " " + user.getName() + "'s Special Attack was lowered by 2 stages!";
    }

    public String RoarOfTime(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String SpacialRend(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        user.setCrit(3);
        int damage = move.getDamage(user, opponent);
        user.setCrit(1);
        opponent.damage(damage);

        return move.getDamageResult(opponent, damage);
    }

    public String DragonEnergy(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        move.setPower((int)(150 * user.getHealth() / (double)user.getStat(Stat.HP)));

        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String Eternabeam(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String DragonPulse(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String CoreEnforcer(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }
}
