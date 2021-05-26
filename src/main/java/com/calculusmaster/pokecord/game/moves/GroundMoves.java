package com.calculusmaster.pokecord.game.moves;

import com.calculusmaster.pokecord.game.Duel;
import com.calculusmaster.pokecord.game.Move;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.elements.Type;

import java.util.Random;

public class GroundMoves
{
    public String Earthquake(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        //TODO: Double damage if opponent has used Dig
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String EarthPower(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        boolean lower = new Random().nextInt(100) < 10;

        if(lower) opponent.changeStatMultiplier(Stat.SPDEF, -1);

        return Move.simpleDamageMove(user, opponent, duel, move) + (lower ? " " + opponent.getName() + "'s Special Defense was lowered by 1 stage!" : "");
    }

    public String Dig(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        //TODO: Dig invulnerability
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String Bulldoze(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        opponent.changeStatMultiplier(Stat.SPD, -1);

        return Move.simpleDamageMove(user, opponent, duel, move) + opponent.getName() + "'s Speed was lowered by 1 stage!";
    }

    public String Fissure(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        if(opponent.getLevel() > user.getLevel()) return move.getNoEffectResult(opponent);
        else
        {
            int damage = opponent.getHealth();
            opponent.damage(damage);

            return move.getDamageResult(opponent, damage);
        }
    }
}
