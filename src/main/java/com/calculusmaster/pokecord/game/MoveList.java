package com.calculusmaster.pokecord.game;

import com.calculusmaster.pokecord.game.moves.Move;
import com.calculusmaster.pokecord.game.moves.grass.LeechSeed;
import com.calculusmaster.pokecord.game.moves.grass.RazorLeaf;
import com.calculusmaster.pokecord.game.moves.grass.VineWhip;
import com.calculusmaster.pokecord.game.moves.normal.Growl;
import com.calculusmaster.pokecord.game.moves.normal.Growth;
import com.calculusmaster.pokecord.game.moves.normal.Tackle;

public class MoveList
{
    public static Move Tackle;
    public static Move Growl;
    public static Move VineWhip;
    public static Move Growth;
    public static Move LeechSeed;
    public static Move RazorLeaf;

    public static void init()
    {
        Tackle = new Tackle();
        Growl = new Growl();
        VineWhip = new VineWhip();
        Growth = new Growth();
        LeechSeed = new LeechSeed();
        RazorLeaf = new RazorLeaf();
    }
}
