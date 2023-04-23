package com.calculusmaster.pokecord.commands.pokemon;

import com.calculusmaster.pokecord.commands.CommandData;
import com.calculusmaster.pokecord.commands.PokeWorldCommand;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.Objects;

public class CommandFavorites extends PokeWorldCommand
{
    public static void init()
    {
        CommandData
                .create("favorites")
                .withConstructor(CommandFavorites::new)
                .withFeature(Feature.CREATE_POKEMON_FAVORITES)
                .withCommand(Commands
                        .slash("favorites", "Mark Pokemon as favorites for easy access and identification!")
                        .addSubcommands(
                                new SubcommandData("add", "Add a Pokemon to your favorites.")
                                        .addOption(OptionType.INTEGER, "number", "The number of the Pokemon.", true),
                                new SubcommandData("remove", "Remove a Pokemon from your favorites.")
                                        .addOption(OptionType.INTEGER, "number", "The number of the Pokemon.", true),
                                new SubcommandData("clear", "Removed all favorited Pokemon.")
                        )
                )
                .register();
    }

    @Override
    protected boolean slashCommandLogic(SlashCommandInteractionEvent event)
    {
        String subcommand = Objects.requireNonNull(event.getSubcommandName());

        if(subcommand.equals("add"))
        {
            OptionMapping numberOption = Objects.requireNonNull(event.getOption("number"));
            int num = numberOption.getAsInt();

            if(num < 1 || num > this.playerData.getPokemonList().size()) return this.error("Invalid Pokemon number.");

            Pokemon p = Objects.requireNonNull(Pokemon.build(this.playerData.getPokemonList().get(num - 1), num));

            if(this.playerData.isFavorite(p.getUUID())) return this.error(p.getName() + " is already in your favorites.");

            this.playerData.addFavorite(p.getUUID());

            this.response = p.getName() + " was added to your favorites!";
        }
        else if(subcommand.equals("remove"))
        {
            OptionMapping numberOption = Objects.requireNonNull(event.getOption("number"));
            int num = numberOption.getAsInt();

            if(num < 1 || num > this.playerData.getPokemonList().size()) return this.error("Invalid Pokemon number.");

            Pokemon p = Objects.requireNonNull(Pokemon.build(this.playerData.getPokemonList().get(num - 1), num));

            if(!this.playerData.isFavorite(p.getUUID())) return this.error(p.getName() + " is not in your favorites.");

            this.playerData.removeFavorite(p.getUUID());

            this.response = p.getName() + " was removed from your favorites!";
        }
        else if(subcommand.equals("clear"))
        {
            if(this.playerData.getFavorites().isEmpty()) return this.error("You don't have any favorited Pokemon.");

            this.playerData.clearFavorites();

            this.response = "All favorited Pokemon were removed!";
        }

        return true;
    }
}
