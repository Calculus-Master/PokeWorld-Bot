package com.calculusmaster.pokecord.commands.economy;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.commands.CommandInvalid;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
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
        final ParseResults<Object> parse = this.dispatcher.parse("bal", this);
        try {
            final int result = this.dispatcher.execute(parse);
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
            this.embed.setDescription(CommandInvalid.getShort());
        }
        return this;
    }

    @Override
    public void createCommandDispatcher()
    {
        this.dispatcher.register(LiteralArgumentBuilder.literal("bal")
                        .executes(c -> {
                            int credits = this.playerData.getCredits();
                            int redeems = this.playerData.getRedeems();

                            String creditsLine = "You currently have " + credits + " credits!";

                            String redeemsLine = "You have " + redeems + " redeem" + (redeems != 1 ? "s" : "") + "!";

                            this.embed.setDescription(creditsLine + "\n\n" + redeemsLine);
                            this.embed.setThumbnail("https://images-ext-2.discordapp.net/external/xlEcYc2ErW6-vD7-nHbk3pv2u4sNwjDVx3jFEL6w9fc/https/emojipedia-us.s3.amazonaws.com/thumbs/120/emoji-one/104/money-bag_1f4b0.png");
                            return 1;
                        }));
    }
}
