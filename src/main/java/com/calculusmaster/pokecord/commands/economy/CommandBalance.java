package com.calculusmaster.pokecord.commands.economy;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandBalance extends Command
{
    public CommandBalance(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        String targetID = this.mentions.size() > 0 ? this.mentions.get(0).getId() : this.player.getId();

        if(!PlayerDataQuery.isRegistered(targetID))
        {
            this.sendMsg(this.mentions.get(0).getEffectiveName() + " is not registered!");
            return this;
        }

        PlayerDataQuery p = targetID.equals(this.player.getId()) ? this.playerData : new PlayerDataQuery(targetID);

        String creditsLine = "Credits: **" + p.getCredits() + "**!";
        String redeemsLine = "Redeems: **" + p.getRedeems() + "**!";

        this.embed.setTitle(p.getUsername() + "'s Balance");
        this.embed.setDescription(creditsLine + "\n\n" + redeemsLine);
        this.embed.setThumbnail("https://images-ext-2.discordapp.net/external/xlEcYc2ErW6-vD7-nHbk3pv2u4sNwjDVx3jFEL6w9fc/https/emojipedia-us.s3.amazonaws.com/thumbs/120/emoji-one/104/money-bag_1f4b0.png");

        return this;
    }
}
