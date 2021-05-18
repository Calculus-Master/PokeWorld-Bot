package com.calculusmaster.pokecord.game.moves;

import com.calculusmaster.pokecord.game.Duel;
import com.calculusmaster.pokecord.game.MoveNew;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.enums.elements.Stat;

public class PsychicMoves
{
    public String CalmMind(Pokemon user, Pokemon opponent, Duel duel, MoveNew move)
    {
        user.changeStatMultiplier(Stat.SPATK, 1);
        user.changeStatMultiplier(Stat.SPDEF, 1);

        return "It raised " + user.getName() + "'s Special Attack and Special Defense by 1 stage!";
    }

    public String Psyshock(Pokemon user, Pokemon opponent, Duel duel, MoveNew move)
    {
        int damage = move.getDamage(user, opponent);
        opponent.damage(damage);

        return move.getDamageResult(opponent, damage);
    }
}