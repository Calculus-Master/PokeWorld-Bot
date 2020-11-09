package com.calculusmaster.pokecord.game.moves.normal;

import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.moves.Move;

public class Roar extends Move
{
    public Roar()
    {
        super("Roar");
        this.setWIP();
    }

    @Override
    public String logic(Pokemon user, Pokemon opponent)
    {
        //TODO: Why is a TM, custom code it
        return "WHY WHY WHY";
    }
}
