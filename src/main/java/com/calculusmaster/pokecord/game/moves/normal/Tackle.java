package com.calculusmaster.pokecord.game.moves.normal;

import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.moves.Move;

public class Tackle extends Move
{
    public Tackle()
    {
        super("Tackle");
    }

    @Override
    public String logic(Pokemon user, Pokemon opponent)
    {
        int damage = this.getDamage(user, opponent);
        opponent.changeHealth(damage * -1);

        return this.getMoveResults(user, opponent, damage);
    }
}
