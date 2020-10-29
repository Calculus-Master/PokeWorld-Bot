package com.calculusmaster.pokecord.game.moves;

import com.calculusmaster.pokecord.game.Pokemon;

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

        return this.getMoveResults(user, opponent, this.getName(), damage);
    }
}
