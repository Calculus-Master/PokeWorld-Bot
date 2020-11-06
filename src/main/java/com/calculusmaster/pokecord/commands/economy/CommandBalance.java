package com.calculusmaster.pokecord.commands.economy;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandBalance extends Command
{
    public CommandBalance(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg, "credits:c:bal");
    }

    @Override
    public Command runCommand()
    {
        int credits = new PlayerDataQuery(this.player.getId()).getCredits();
        this.embed.setDescription("You currently have " + credits + " credits!");
        this.embed.setThumbnail("https://images-ext-2.discordapp.net/external/xlEcYc2ErW6-vD7-nHbk3pv2u4sNwjDVx3jFEL6w9fc/https/emojipedia-us.s3.amazonaws.com/thumbs/120/emoji-one/104/money-bag_1f4b0.png");
        return this;
    }
}
