package com.calculusmaster.pokecord.game.moves;

import com.calculusmaster.pokecord.game.Duel;
import com.calculusmaster.pokecord.game.Move;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.enums.elements.Stat;

public class FairyMoves
{
    public String Moonlight(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int HP = switch(duel.getDuelWeather())
                {
                    case CLEAR -> user.getStat(Stat.HP) / 2;
                    case HARSH_SUNLIGHT -> user.getStat(Stat.HP) * 2 / 3;
                    default -> user.getStat(Stat.HP) / 4;
                };

        user.heal(HP);

        return user.getName() + " healed for " + HP + " HP!";
    }
}
