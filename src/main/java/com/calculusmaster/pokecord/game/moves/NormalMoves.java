package com.calculusmaster.pokecord.game.moves;

import com.calculusmaster.pokecord.game.Duel;
import com.calculusmaster.pokecord.game.MoveNew;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.enums.elements.Stat;

public class NormalMoves
{
    public String Tackle(Pokemon user, Pokemon opponent, Duel duel, MoveNew move)
    {
        //Temporary
        if(2 > 1) return "Hi!";

        int damage = move.getDamage(user, opponent);
        opponent.changeHealth(damage * -1);

        return move.getDamageResult(user, opponent, damage);
    }

    public String Growl(Pokemon user, Pokemon opponent, Duel duel, MoveNew move)
    {
        opponent.changeStatMultiplier(Stat.ATK, -1);
        return "It lowered " + opponent.getName() + "'s Attack stat by one stage!";
    }
}
