package com.calculusmaster.pokecord.commandslegacy;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandLegacyInvalid extends CommandLegacy
{
    public static final String ALREADY_IN_DUEL = "You are already in a duel! You cannot start another one until your current duel is complete!";

    public CommandLegacyInvalid(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public CommandLegacy runCommand()
    {
        this.embed.setDescription(CommandLegacyInvalid.getFull());
        return this;
    }

    public static String getFull()
    {
        return "Invalid Command! Use the help command if you are unsure of the correct syntax.";
    }

    public static String getShort()
    {
        return "Invalid Command and/or Arguments!";
    }
}
