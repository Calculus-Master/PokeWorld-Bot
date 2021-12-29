package com.calculusmaster.pokecord.game.player;

import java.util.Arrays;

public enum Settings {
    //Client
    CLIENT_DETAILED("detailed", "Toggles the display of IVs and EVs throughout the bot."),
    CLIENT_CATCH_AUTO_INFO("autoinfo", "Toggle automatically sending `p!info latest` after catching a Pokemon."),
    CLIENT_DEFAULT_ORDER("order", "Set the default ordering of `p!pokemon`. Examples: `name`, `iv`, `level`, `number`, `random`"),
    CLIENT_POKEMON_LIST_FIELDS("listfields", "Toggle p!pokemon view between Fields and a Text-based List"),
    //Server
    SERVER_PREFIX("prefix", "Changes the bot prefix (default `p!`)"),
    SERVER_SPAWNCHANNEL("spawnchannel", "Toggles if spawns are enabled in a specific channel."),
    SERVER_ZCRYSTAL_DUEL_EQUIP("equipzcrystal_duel", "Toggles if players can equip Z Crystals while in a duel."),
    SERVER_DYNAMAX("dynamax", "Toggles if players can dynamax in duels."),
    SERVER_ZMOVE("zmoves", "Toggles if players can use Z-Moves in duels."),
    SERVER_DUELCHANNEL("duelchannel", "Restricts dueling to certain channels."),
    SERVER_BOTCHANNEL("botchannel", "Restricts all bot commands to certain channels.");

    private String command;
    private String desc;

    Settings(String command, String desc) {
        this.command = command;
        this.desc = desc;
    }

    public static boolean isValid(String command) {
        return Arrays.stream(.values()).anyMatch(s -> s.getCommand().equals(command));
    }

    public boolean matches(String input) {
        return this.command.equals(input);
    }

    public boolean isClient() {
        return this.toString().contains("CLIENT");
    }

    public boolean isServer() {
        return this.toString().contains("SERVER");
    }

    public String getCommand() {
        return this.command;
    }

    public String getDesc() {
        return this.desc;
    }
}
