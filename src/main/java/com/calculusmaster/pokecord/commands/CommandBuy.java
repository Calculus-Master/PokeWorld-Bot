package com.calculusmaster.pokecord.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandBuy extends Command
{
    //Prices
    public static final int COST_MEGA = 2000;
    public static final int COST_FORM = 1500;
    public static final int COST_NATURE = 200;

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
