package com.calculusmaster.pokecord.game.enums.functional;

import java.util.Random;

public enum Tips
{
    POKEMON("Use the `p!pokemon` command to view your Pokemon!"),
    POKEMON_ARG_NAME("Use `p!pokemon --name <input>` to search for Pokemon by name! Replace <input> with the name you want to search for."),
    POKEMON_ARG_ORDER("You can sort your Pokemon by using the `p!pokemon --order <orderType>` command! <orderType> can either be `name`, `number`, `level`, `iv` or `random`."),
    POKEMON_NICKNAME("You can nickname your Pokemon! Use `p!nickname <name>` to rename your selected Pokemon"),
    DUEL_PLAYERS("You can duel other players! Use `p!duel @player`, where @player is a mention."),
    DUEL_WILD("You can duel Wild Pokemon! Use `p!wildduel` to start one."),
    DUEL_ZMOVE("You can use Z-Moves in duels! While in a duel, type `p!use z <number>`, provided that you have a valid Z Crystal equipped."),
    DUEL_DYNAMAX("You can Dynamax your Pokemon in duels! While in a duel, type `p!use d <number>`, where <number> is the move number."),
    DUEL_MOVES_COMMAND("Using p!moves while in a duel will show you the effectiveness of each of your Pokemon's moves against your opponent!"),
    MARKET("You can buy Pokemon on the market! Use `p!market` to see what is available."),
    MARKET_SELL("You can sell Pokemon on the market! Use `p!market sell <number> <price>`."),
    MARKET_INFO("See a Pokemon you like on the market? Use `p!market info <id>` to see its stats in detail!"),
    MARKET_ARGS("`p!market` also has arguments, very similar to `p!pokemon`! You can search by name, IV, etc, and also price!"),
    ACHIEVEMENT("There are achievements that you can earn! Use `p!achievements` to see your completion progress, along with a small list of Achievements that you have not earned yet!"),
    BOUNTIES("You can acquire Bounties with `p!bounty`! Bounties are short tasks with small rewards. To see a more in-depth explanation, type `p!bounty info`");

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
