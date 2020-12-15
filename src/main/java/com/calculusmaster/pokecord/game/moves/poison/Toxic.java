package com.calculusmaster.pokecord.game.moves.poison;

import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.enums.elements.StatusCondition;
import com.calculusmaster.pokecord.game.moves.Move;

public class Toxic extends Move
{
    //TODO: This is a custom implemented class (normally does Badly Poisoned)
    public Toxic()
    {
        super("Toxic");
    }

    @Override
    public String logic(Pokemon user, Pokemon opponent)
    {
        if(opponent.getStatusCondition().equals(StatusCondition.NORMAL)) opponent.setStatusCondition(StatusCondition.POISONED);
        return opponent.getName() + " was inflicted with Poison!";
    }
}
