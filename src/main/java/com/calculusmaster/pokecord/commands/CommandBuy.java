package com.calculusmaster.pokecord.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandBuy extends Command
{
    //TODO: WIP (buy)
    public CommandBuy(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg, "buy <item>");
    }

    @Override
    public Command runCommand()
    {
        return null;
    }
}
