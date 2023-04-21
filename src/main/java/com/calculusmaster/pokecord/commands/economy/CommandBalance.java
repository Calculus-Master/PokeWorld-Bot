package com.calculusmaster.pokecord.commands.economy;

import com.calculusmaster.pokecord.commands.CommandData;
import com.calculusmaster.pokecord.commands.PokeWorldCommand;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.mongo.PlayerData;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class CommandBalance extends PokeWorldCommand
{
    public static void init()
    {
        CommandData
                .create("balance")
                .withConstructor(CommandBalance::new)
                .withFeature(Feature.VIEW_BALANCE)
                .withCommand(Commands
                        .slash("balance", "View your credits and redeems.")
                        .addOption(OptionType.USER, "user", "[Optional] The user who you want to see the balance of.", false)
                )
                .register();
    }

    @Override
    protected boolean slashCommandLogic(SlashCommandInteractionEvent event)
    {
        OptionMapping userOption = event.getOption("user");

        String targetID = userOption == null ? this.player.getId() : userOption.getAsUser().getId();

        PlayerData p;
        if(userOption != null && !targetID.equals(this.player.getId()))
        {
            if(!PlayerData.isRegistered(targetID))
                return this.error("That user has not joined the world of Pokemon!");
            else p = PlayerData.build(userOption.getAsUser().getId());
        }
        else p = this.playerData;

        this.embed
                .setTitle(p.getUsername() + "'s Balance")
                .addField("Credits", "" + p.getCredits(), true)
                .addField("Redeems", "" + p.getRedeems(), true)
                .setThumbnail("https://images-ext-2.discordapp.net/external/xlEcYc2ErW6-vD7-nHbk3pv2u4sNwjDVx3jFEL6w9fc/https/emojipedia-us.s3.amazonaws.com/thumbs/120/emoji-one/104/money-bag_1f4b0.png");

        return true;
    }
}
