package com.calculusmaster.pokecord.game.moves;

import com.calculusmaster.pokecord.game.Duel;
import com.calculusmaster.pokecord.game.Move;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.enums.elements.Stat;

public class FairyMoves
{
    public String Moonlight(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int HP = switch(duel.weather)
                {
                    case CLEAR -> user.getStat(Stat.HP) / 2;
                    case HARSH_SUNLIGHT -> user.getStat(Stat.HP) * 2 / 3;
                    default -> user.getStat(Stat.HP) / 4;
                };

        user.heal(HP);

        return user.getName() + " healed for " + HP + " HP!";
    }

    public String Moonblast(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statChangeDamageMove(user, opponent, duel, move, Stat.SPATK, -1, 30, false);
    }
}
