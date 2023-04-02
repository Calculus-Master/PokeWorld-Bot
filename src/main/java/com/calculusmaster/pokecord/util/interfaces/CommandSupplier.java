package com.calculusmaster.pokecord.util.interfaces;

import com.calculusmaster.pokecord.commandslegacy.CommandLegacy;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public interface CommandSupplier
{
    CommandLegacy create(MessageReceivedEvent event, String[] msg);
}
