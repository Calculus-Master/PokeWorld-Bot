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
        opponent.damage(damage, duel);

        if((!opponent.getType()[0].equals(Type.FIRE) && !opponent.getType()[1].equals(Type.FIRE)) && new Random().nextInt(100) < 10)
        {
            opponent.addStatusCondition(StatusCondition.BURNED);
            return move.getDamageResult(opponent, damage) + " " + opponent.getName() + " is burned!";
        }

        return move.getDamageResult(opponent, damage);
    }

    public String FireFang(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int damage = move.getDamage(user, opponent);
        opponent.damage(damage, duel);

        String burned = "";
        String flinched = "";

        if((!opponent.getType()[0].equals(Type.FIRE) && !opponent.getType()[1].equals(Type.FIRE)) && new Random().nextInt(100) < 10)
        {
            opponent.addStatusCondition(StatusCondition.BURNED);
            burned = " " + opponent.getName() + " is burned!";
        }

        if(new Random().nextInt(100) < 10)
        {
            opponent.addStatusCondition(StatusCondition.FLINCHED);
            flinched = " " + opponent.getName() + " flinched!";
        }

        return move.getDamageResult(opponent, damage) + burned + flinched;
    }

    public String FlameBurst(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int damage = move.getDamage(user, opponent);
        opponent.damage(damage, duel);

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
        opponent.damage(damage, duel);

        return move.getDamageResult(opponent, damage);
    }

    public String Inferno(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int damage = move.getDamage(user, opponent);
        opponent.damage(damage, duel);

        opponent.addStatusCondition(StatusCondition.BURNED);

        return move.getDamageResult(opponent, damage) + " " + opponent.getName() + " is burned!";
    }

    public String HeatWave(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Ember(user, opponent, duel, move);
    }

    public String FlareBlitz(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int damage = move.getDamage(user, opponent);
        opponent.damage(damage, duel);

        user.damage(damage / 3, duel);

        if((!opponent.getType()[0].equals(Type.FIRE) && !opponent.getType()[1].equals(Type.FIRE)) && new Random().nextInt(100) < 10)
        {
            opponent.addStatusCondition(StatusCondition.BURNED);
            return move.getDamageResult(opponent, damage) + move.getRecoilDamageResult(user, damage / 3) + " " + opponent.getName() + " is burned!";
        }

        return move.getDamageResult(opponent, damage) + " " + move.getRecoilDamageResult(user, damage / 3);
    }

    public String SunnyDay(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return user.getName() + " caused harsh sunlight!";
    }
}
