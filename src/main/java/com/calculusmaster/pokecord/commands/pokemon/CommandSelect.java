package com.calculusmaster.pokecord.commands.pokemon;

import com.calculusmaster.pokecord.commands.CommandData;
import com.calculusmaster.pokecord.commands.PokeWorldCommand;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.util.Objects;

public class CommandSelect extends PokeWorldCommand
{
    public static void init()
    {
        CommandData
                .create("select")
                .withConstructor(CommandSelect::new)
                .withFeature(Feature.SELECT_POKEMON)
                .withCommand(Commands
                        .slash("select", "Select a Pokemon to become your Active Pokemon!")
                        .addOption(OptionType.INTEGER, "number", "The number of the Pokemon you want to make your active/selected.", true)
                )
                .register();
    }

    @Override
    protected boolean slashCommandLogic(SlashCommandInteractionEvent event)
    {
        OptionMapping numberOption = Objects.requireNonNull(event.getOption("number"));
        int num = numberOption.getAsInt();

        if(num < 1 || num > this.playerData.getPokemonList().size()) return this.error("Invalid Pokemon number!");

        this.playerData.setSelected(num);
        this.response = "**" + this.playerData.getSelectedPokemon().getName() + "** is now your Active Pokemon!";

        return true;
    }
}
