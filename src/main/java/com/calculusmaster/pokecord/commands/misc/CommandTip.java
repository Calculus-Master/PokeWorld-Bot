package com.calculusmaster.pokecord.commands.misc;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.game.Tips;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandTip extends Command
{
    public CommandTip(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
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
