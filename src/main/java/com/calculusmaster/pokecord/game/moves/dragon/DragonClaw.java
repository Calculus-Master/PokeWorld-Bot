package com.calculusmaster.pokecord.game.moves.dragon;

import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.moves.Move;

public class DragonClaw extends Move
{
    public DragonClaw()
    {
        super("Dragon Claw");
    }

    @Override
    public String logic(Pokemon user, Pokemon opponent)
    {
        int damage = this.getDamage(user, opponent);
        opponent.changeHealth(damage);
        return this.getMoveResults(user, opponent, damage);
    }
}
