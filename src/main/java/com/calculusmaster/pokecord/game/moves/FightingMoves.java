package com.calculusmaster.pokecord.game.moves;

import com.calculusmaster.pokecord.game.Duel;
import com.calculusmaster.pokecord.game.MoveNew;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.enums.elements.Stat;

public class FightingMoves
{
    public String BulkUp(Pokemon user, Pokemon opponent, Duel duel, MoveNew move)
    {
        user.changeStatMultiplier(Stat.ATK, 1);
        user.changeStatMultiplier(Stat.DEF, 1);
        return "It raised " + user.getName() + "'s Attack and Defense by 1 stage!";
    }
}
