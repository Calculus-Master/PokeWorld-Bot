package com.calculusmaster.pokecord.game.moves;

import com.calculusmaster.pokecord.game.Duel;
import com.calculusmaster.pokecord.game.Move;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.enums.elements.StatusCondition;
import com.calculusmaster.pokecord.game.enums.elements.Type;

import java.util.Random;

public class FireMoves
{
    public String Ember(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int damage = move.getDamage(user, opponent);
        opponent.damage(damage);

        if((!opponent.getType()[0].equals(Type.FIRE) && !opponent.getType()[1].equals(Type.FIRE)) && new Random().nextInt(100) < 10)
        {
            opponent.setStatusCondition(StatusCondition.BURNED);
            return move.getDamageResult(opponent, damage) + " " + opponent.getName() + " is burned!";
        }

        return move.getDamageResult(opponent, damage);
    }

    public String FireFang(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Ember(user, opponent, duel, move);
        //TODO: Flinching
    }

    public String FlameBurst(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int damage = move.getDamage(user, opponent);
        opponent.damage(damage);

        return move.getDamageResult(opponent, damage);
    }

    public String Flamethrower(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Ember(user, opponent, duel, move);
    }

    public String FireSpin(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        //TODO: Does damage per turn
        int damage = move.getDamage(user, opponent);
        opponent.damage(damage);

        return move.getDamageResult(opponent, damage);
    }

    public String Inferno(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int damage = move.getDamage(user, opponent);
        opponent.damage(damage);

        opponent.setStatusCondition(StatusCondition.BURNED);

        return move.getDamageResult(opponent, damage) + " " + opponent.getName() + " is burned!";
    }
}
