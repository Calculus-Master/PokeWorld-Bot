package com.calculusmaster.pokecord.game;

import com.calculusmaster.pokecord.game.moves.Move;
import com.calculusmaster.pokecord.game.moves.Tackle;

public class MoveList
{
    public static Move Tackle;

    public static void init()
    {
        Tackle = new Tackle();
    }
}
