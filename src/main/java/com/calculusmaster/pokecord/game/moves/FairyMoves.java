package com.calculusmaster.pokecord.game.moves;

import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.Move;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.enums.elements.StatusCondition;

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

    public String DisarmingVoice(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.simpleDamageMove(user, opponent, duel, move);
    }

    public String NaturesMadness(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int damage = opponent.getHealth() / 2;
        opponent.damage(damage);

        return move.getDamageResult(opponent, damage);
    }

    public String StrangeSteam(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        return Move.statusDamageMove(user, opponent, duel, move, StatusCondition.CONFUSED, 20);
    }

    public String DrainingKiss(Pokemon user, Pokemon opponent, Duel duel, Move move)
    {
        int damage = move.getDamage(user, opponent);

        opponent.damage(damage);
        user.heal(damage * 3 / 4);

        return move.getDamageResult(opponent, damage) + " " + user.getName() + " healed for " + (damage * 3 / 4) + " HP!";
    }
}
