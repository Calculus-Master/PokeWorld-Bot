package com.calculusmaster.pokecord.game;

import com.calculusmaster.pokecord.game.moves.Move;
import com.calculusmaster.pokecord.game.moves.normal.Growl;
import com.calculusmaster.pokecord.game.moves.normal.Tackle;

public class MoveList
{
    public static Move Tackle;
    public static Move Growl;

    public static void init()
    {
        Tackle = new Tackle();
        Growl = new Growl();
    }
}
