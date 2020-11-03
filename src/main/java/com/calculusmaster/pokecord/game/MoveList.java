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
    //Moves
    public static Move Tackle;
    public static Move Growl;
    public static Move VineWhip;
    public static Move Growth;
    public static Move LeechSeed;
    public static Move RazorLeaf;

    //TMs
    //TODO: Initialize these (and add to database)
    public static Move WorkUp;
    public static Move DragonClaw;
    public static Move Psyshock;
    public static Move CalmMind;
    public static Move Roar;
    public static Move Toxic;
    public static Move Hail;
    public static Move BulkUp;
    public static Move Venoshock;
    public static Move HiddenPower;

    //TRs

    public static void init()
    {
        //Moves
        Tackle = new Tackle();
        Growl = new Growl();
        VineWhip = new VineWhip();
        Growth = new Growth();
        LeechSeed = new LeechSeed();
        RazorLeaf = new RazorLeaf();

        //TMs


        //TRs

    }
}
