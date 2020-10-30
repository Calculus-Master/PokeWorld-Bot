package com.calculusmaster.pokecord.game.moves.grass;

import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.moves.Move;

public class RazorLeaf extends Move
{
    public RazorLeaf()
    {
        super("Razor Leaf");
    }

    @Override
    public String logic(Pokemon user, Pokemon opponent)
    {
        //TODO: Add increase crit hit ratio
        int damage = this.getDamage(user, opponent);
        opponent.changeHealth(-1 * damage);

        return this.getMoveResults(user, opponent, damage);
    }
}
