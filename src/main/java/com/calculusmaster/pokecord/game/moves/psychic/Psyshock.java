package com.calculusmaster.pokecord.game.moves.psychic;

import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.enums.elements.Category;
import com.calculusmaster.pokecord.game.moves.Move;

public class Psyshock extends Move
{
    public Psyshock()
    {
        super("Psyshock");
    }

    @Override
    public String logic(Pokemon user, Pokemon opponent)
    {
        int damage = this.getDamage(user, opponent);
        opponent.changeHealth(-1 * damage);
        return this.getMoveResults(user, opponent, damage);
    }

    @Override
    public Category getCategory()
    {
        return Category.PHYSICAL;
    }
}
