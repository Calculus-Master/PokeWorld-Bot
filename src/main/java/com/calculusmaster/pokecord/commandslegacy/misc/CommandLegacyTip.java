package com.calculusmaster.pokecord.commandslegacy.misc;

import com.calculusmaster.pokecord.commandslegacy.CommandLegacy;
import com.calculusmaster.pokecord.game.enums.functional.Tips;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandLegacyTip extends CommandLegacy
{
    public CommandLegacyTip(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public CommandLegacy runCommand()
    {
        StringBuilder tips = new StringBuilder();
        for(int i = 0; i < 3; i++) tips.append(Tips.get().tip).append("\n");
        tips.deleteCharAt(tips.length() - 1);

        this.embed.setTitle("Tip")
                .setDescription(tips.toString())
                .setFooter("Use `p!help` for more information on all the bot commands!");

        return this;
    }
}
