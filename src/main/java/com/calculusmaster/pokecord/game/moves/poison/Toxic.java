package com.calculusmaster.pokecord.game.moves.poison;

import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.enums.elements.StatusCondition;
import com.calculusmaster.pokecord.game.moves.Move;

public class Toxic extends Move
{
    public Toxic()
    {
        super("Toxic");
        this.setCustom();
    }

    @Override
    public String logic(Pokemon user, Pokemon opponent)
    {
        if(opponent.getStatusCondition().equals(StatusCondition.NORMAL)) opponent.setStatusCondition(StatusCondition.POISONED);
        return opponent.getName() + " was inflicted with Poison!";
    }
}
