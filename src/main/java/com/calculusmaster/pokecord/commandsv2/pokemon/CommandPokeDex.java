package com.calculusmaster.pokecord.commandsv2.pokemon;

import com.calculusmaster.pokecord.commandsv2.CommandData;
import com.calculusmaster.pokecord.commandsv2.CommandV2;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.player.PlayerPokedex;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonEntity;
import com.calculusmaster.pokecord.util.Global;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CommandPokeDex extends CommandV2
{
    private static final int DEFAULT_POKEMON_PER_PAGE = 15; //TODO: Make this a client setting

    public static void init()
    {
        CommandData
                .create("pokedex")
                .withConstructor(CommandPokeDex::new)
                .withFeature(Feature.VIEW_DEX_INFO)
                .withCommand(Commands
                        .slash("pokedex", "View your PokeDex and information about specific Pokemon!")
                        .addSubcommands(
                                new SubcommandData("info", "View information about a specific Pokemon.")
                                        .addOption(OptionType.STRING, "name", "Name of the Pokemon.", true, true),
                                new SubcommandData("view", "View your PokeDex and how many Pokemon you've collected.")
                                        .addOption(OptionType.INTEGER, "page", "Page number of your PokeDex.", false, false)
                        )
                )
                .register();
    }

    @Override
    protected boolean slashCommandLogic(SlashCommandInteractionEvent event)
    {
        String subcommand = Objects.requireNonNull(event.getSubcommandName());

        if(subcommand.equals("info"))
        {

        }
        else if(subcommand.equals("view"))
        {
            OptionMapping pageOption = event.getOption("page");

            PlayerPokedex dex = this.playerData.getPokedex();
            int page = pageOption == null ? 1 : pageOption.getAsInt();

            //Ensure page is within bounds
            int lastPage = PokemonEntity.values().length / DEFAULT_POKEMON_PER_PAGE + 1;
            if(page > lastPage) page = lastPage;
            else if(page < 1) page = 1;

            int startIndex = (page - 1) * DEFAULT_POKEMON_PER_PAGE;
            int endIndex = Math.min(PokemonEntity.values().length - 1, startIndex + DEFAULT_POKEMON_PER_PAGE - 1);

            if(startIndex > endIndex) startIndex = endIndex - DEFAULT_POKEMON_PER_PAGE + 1;

            List<MessageEmbed.Field> dexEntries = new ArrayList<>();
            for(int i = startIndex; i <= endIndex; i++)
            {
                PokemonEntity e = PokemonEntity.values()[i];

                String rarity = dex.hasCollected(e) ? Global.normalize(e.getRarity().toString()) : "Unknown";
                if(e.isNotSpawnable()) rarity += " 🚫";

                dexEntries.add(new MessageEmbed.Field(
                        e.getName() + (dex.hasCollected(e) ? " ✅" : " ❌"),
                        "#%s\nCollected: %s\nRarity: %s".formatted(e.getDex(), dex.getCollectedAmount(e), rarity),
                        true
                ));
            }

            int size = dex.getSize();
            int total = PokemonEntity.values().length;
            String percent = String.format("%.2f", (double)size / total * 100) + "%";
            String completionText = "%s (%s / %s Pokemon Discovered)".formatted(percent, size, total);

            String pageText = "Page %s / %s".formatted(page, lastPage);

            this.embed
                    .setTitle(this.player.getName() + "'s PokeDex")
                    .setDescription("""
                            Your PokeDex is a record of the Pokemon you've collected!
                            Discovering a new Pokemon rewards you credits, and you'll earn more credits each time you hit a collection milestone for a Pokemon.
                            
                            Once you collect a Pokemon, you will be able to see its Spawn Rarity below. Rarer Pokemon give more credits when reaching collection milestones!
                            Some Pokemon will never spawn randomly, and must be acquired through other means (these are designated with 🚫).
                            
                            Will you catch them all?
                            **PokeDex Completion: %s**
                            """.formatted(completionText))
                    .setFooter(pageText);

            dexEntries.forEach(f -> this.embed.addField(f));
            for(int i = 0; i < dexEntries.size() % 3; i++) this.embed.addBlankField(true);
        }

        return true;
    }

    @Override
    protected boolean autocompleteLogic(CommandAutoCompleteInteractionEvent event)
    {
        if(event.getFocusedOption().getName().equals("name"))
        {
            String currentInput = event.getFocusedOption().getName();

            List<String> pokemon = Arrays.stream(PokemonEntity.values()).map(PokemonEntity::getName).toList();

            event.replyChoiceStrings(this.getAutocompleteOptions(currentInput, pokemon)).queue();
        }

        return true;
    }
}
