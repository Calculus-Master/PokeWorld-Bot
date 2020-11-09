package com.calculusmaster.pokecord.game.moves.poison;

import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.moves.Move;

public class Toxic extends Move
{
    public Toxic()
    {
        super("Toxic");
        this.setWIP();
    }

    @Override
    public String logic(Pokemon user, Pokemon opponent)
    {
        //TODO: Special Conditions
        return "ERROR";
    }
}
