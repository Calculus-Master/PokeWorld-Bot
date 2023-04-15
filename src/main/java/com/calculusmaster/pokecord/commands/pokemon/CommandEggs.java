package com.calculusmaster.pokecord.commands.pokemon;

import com.calculusmaster.pokecord.commands.CommandData;
import com.calculusmaster.pokecord.commands.PokeWorldCommand;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.pokemon.evolution.PokemonEgg;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.List;
import java.util.Objects;

public class CommandEggs extends PokeWorldCommand
{
    public static void init()
    {
        CommandData
                .create("eggs")
                .withConstructor(CommandEggs::new)
                .withFeature(Feature.HATCH_EGGS)
                .withCommand(Commands
                        .slash("eggs", "View your Pokemon eggs!")
                        .addSubcommands(
                                new SubcommandData("view", "View your Pokemon eggs."),
                                new SubcommandData("select", "Select an egg to be your active egg.")
                                        .addOption(OptionType.INTEGER, "number", "The number of the egg you want to select.", true)
                        )
                )
                .register();
    }

    @Override
    protected boolean slashCommandLogic(SlashCommandInteractionEvent event)
    {
        String subcommand = Objects.requireNonNull(event.getSubcommandName());

        if(subcommand.equals("view"))
        {
            if(!this.playerData.hasEggs()) return this.error("You do not have any Pokemon eggs. Use `/breed` to breed Pokemon and get Pokemon eggs!");

            List<PokemonEgg> eggs = this.playerData.getOwnedEggs();
            for(int i = 0; i < eggs.size(); i++) this.embed.addField("Egg #" + (i + 1), eggs.get(i).getOverview(), true);

            this.embed
                    .setTitle(this.player.getName() + "'s Pokemon Eggs")
                    .setDescription("Hatch eggs by selecting one to be your active egg, and then sending messages!")
                    .addField("Active Egg",
                    this.playerData.hasActiveEgg() ? this.playerData.getActiveEgg().getOverview() : "**None**\n*Select an egg to be your active egg using* `/eggs select`.",
                    false);
        }
        else if(subcommand.equals("select"))
        {
            OptionMapping numberOption = Objects.requireNonNull(event.getOption("number"));
            int numberInput = numberOption.getAsInt();

            if(numberInput < 1 || numberInput > this.playerData.getOwnedEggIDs().size()) return this.error("Invalid egg number!");
            else if(this.playerData.getActiveEggID().equals(this.playerData.getOwnedEggIDs().get(numberInput - 1))) return this.error("This egg is already your active egg!");

            this.playerData.setActiveEgg(this.playerData.getOwnedEggIDs().get(numberInput - 1));

            this.response = "You've selected Egg #" + numberInput + " to be your active egg.";
        }

        return true;
    }
}
