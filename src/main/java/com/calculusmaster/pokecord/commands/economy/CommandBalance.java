package com.calculusmaster.pokecord.commands.economy;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
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
        if(this.insufficientMasteryLevel(Feature.VIEW_BALANCE)) return this.invalidMasteryLevel(Feature.VIEW_BALANCE);

        String targetID = this.mentions.size() > 0 ? this.mentions.get(0).getId() : this.player.getId();

        if(!PlayerDataQuery.isRegistered(targetID))
        {
            this.response = this.mentions.get(0).getEffectiveName() + " is not registered!";
            return this;
        }

        PlayerDataQuery p = targetID.equals(this.player.getId()) ? this.playerData : PlayerDataQuery.of(targetID);

        this.embed.setTitle(p.getUsername() + "'s Balance");
        this.embed
                .addField("Credits", "`" + p.getCredits() + "`", true)
                .addField("Redeems", "`" + p.getRedeems() + "`", true)
                .addField("Items", "`" + (p.getItemList().size() + p.getTMList().size() + p.getZCrystalList().size()) + "`", true);
        this.embed.setThumbnail("https://images-ext-2.discordapp.net/external/xlEcYc2ErW6-vD7-nHbk3pv2u4sNwjDVx3jFEL6w9fc/https/emojipedia-us.s3.amazonaws.com/thumbs/120/emoji-one/104/money-bag_1f4b0.png");

        return this;
    }
}
