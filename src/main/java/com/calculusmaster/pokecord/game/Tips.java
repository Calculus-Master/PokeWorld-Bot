package com.calculusmaster.pokecord.game;

import java.util.Random;

public enum Tips
{
    POKEMON("Use the `p!pokemon` command to view your Pokemon!"),
    POKEMON_ARG_NAME("Use `p!pokemon --name <input>` to search for Pokemon by name! Replace <input> with the name you want to search for."),
    DUEL_PLAYERS("You can duel other players! Use `p!duel @player`, where @player is a mention."),
    DUEL_WILD("You can duel Wild Pokemon! Use `p!wildduel` to start one."),
    DUEL_ZMOVE("You can use Z-Moves in duels! While in a duel, type `p!use z <number>`, provided that you have a valid Z Crystal equipped."),
    DUEL_DYNAMAX("You can Dynamax your Pokemon in duels! While in a duel, type `p!use d <number>`, where <number> is the move number."),
    DUEL_MOVES_COMMAND("Using p!moves while in a duel will show you the effectiveness of each of your Pokemon's moves against your opponent!");

    public String tip;
    Tips(String tip)
    {
        this.tip = tip;
    }

    public static Tips get()
    {
        return values()[new Random().nextInt(values().length)];
    }
}
