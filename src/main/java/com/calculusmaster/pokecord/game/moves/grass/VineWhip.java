package com.calculusmaster.pokecord.game.moves.grass;

import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.moves.Move;

public class VineWhip extends Move
{
    public VineWhip()
    {
        super("Vine Whip");
    }

    @Override
    public String logic(Pokemon user, Pokemon opponent)
    {
        int damage = this.getDamage(user, opponent);
        opponent.changeHealth(damage);

        return this.getMoveResults(user, opponent, damage);
    }
}
