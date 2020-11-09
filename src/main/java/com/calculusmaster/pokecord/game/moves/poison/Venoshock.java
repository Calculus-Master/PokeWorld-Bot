package com.calculusmaster.pokecord.game.moves.poison;

import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.moves.Move;

public class Venoshock extends Move
{
    public Venoshock()
    {
        super("Venoshock");
    }

    @Override
    public String logic(Pokemon user, Pokemon opponent)
    {
        int damage = this.getDamage(user, opponent);
        opponent.changeHealth(-1 * damage);

        //TODO: Double damage if poisoned
        return this.getMoveResults(user, opponent, damage);
    }
}
