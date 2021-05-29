package com.calculusmaster.pokecord.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandInvalid extends Command
{
    public CommandInvalid(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        this.embed.setDescription(CommandInvalid.getFull());
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
