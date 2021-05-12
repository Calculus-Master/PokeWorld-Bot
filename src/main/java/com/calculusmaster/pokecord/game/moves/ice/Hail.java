package com.calculusmaster.pokecord.game.moves.ice;

import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.moves.Move;

public class Hail extends Move
{
    public Hail()
    {
        super("Hail");
        this.setWIP();
    }

    @Override
    public String logic(Pokemon user, Pokemon opponent)
    {
        //TODO: Weather - pass Duel object to a cloned version of this????
        return "ERROR";
    }
}
