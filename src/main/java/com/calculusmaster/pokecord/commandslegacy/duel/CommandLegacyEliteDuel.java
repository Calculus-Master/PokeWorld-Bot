package com.calculusmaster.pokecord.commandslegacy.duel;

import com.calculusmaster.pokecord.commandslegacy.CommandLegacy;
import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.duel.core.DuelHelper;
import com.calculusmaster.pokecord.game.duel.extension.EliteDuel;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandLegacyEliteDuel extends CommandLegacy
{
    public CommandLegacyEliteDuel(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public CommandLegacy runCommand()
    {
        if(this.insufficientMasteryLevel(Feature.PVE_DUELS_ELITE)) return this.invalidMasteryLevel(Feature.PVE_DUELS_ELITE);

        if(this.msg.length == 1)
        {
            if(DuelHelper.isInDuel(this.player.getId()))
            {
                this.response = "You are already in a duel!";
                return this;
            }

            if(this.playerData.getTeam().size() < 6)
                this.event.getChannel().sendMessage("Warning! You have a Pokemon team with less than 6 Pokemon. This Duel will be more difficult!").queue();


            Duel d = EliteDuel.create(this.player.getId(), this.event);
            this.event.getChannel().sendMessage("You challenged an Elite Trainer!").queue();
            this.embed = null;
            d.sendTurnEmbed();
        }
        else if(this.msg[1].equals("info"))
        {
            this.embed
                    .setTitle("Elite Duel Information")
                    .setDescription("Elite Duels are Duels against a powerful trainer that require a solid Pokemon team to defeat. There are increased rewards if the Elite Trainer is defeated.");
        }
        return this;
    }
}
