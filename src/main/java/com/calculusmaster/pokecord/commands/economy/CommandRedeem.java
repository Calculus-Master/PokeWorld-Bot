package com.calculusmaster.pokecord.commands.economy;

import com.calculusmaster.pokecord.commands.CommandData;
import com.calculusmaster.pokecord.commands.PokeWorldCommand;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonEntity;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.util.Arrays;
import java.util.Objects;

public class CommandRedeem extends PokeWorldCommand
{
    public static void init()
    {
        CommandData
                .create("redeem")
                .withConstructor(CommandRedeem::new)
                .withFeature(Feature.REDEEM_POKEMON)
                .withCommand(Commands
                        .slash("redeem", "Redeem any catchable Pokemon!")
                        .addOption(OptionType.USER, "name", "Name of the Pokemon you want to redeem.", true, true)
                )
                .register();
    }

    @Override
    protected boolean slashCommandLogic(SlashCommandInteractionEvent event)
    {
        OptionMapping nameOption = Objects.requireNonNull(event.getOption("name"));
        String nameInput = nameOption.getAsString();

        PokemonEntity target = PokemonEntity.cast(nameInput);
        if(target == null) return this.error("\"" + nameInput + "\" is not a valid Pokemon name!");
        else if(target.isNotSpawnable()) return this.error(target.getName() + " is not a Pokemon that can be caught. Only Pokemon that can be caught can be redeemed.");
        else if(this.playerData.getRedeems() == 0) return this.error("You do not have any redeems.");

        Pokemon p = Pokemon.create(target);
        p.setLevel(1);

        p.upload();
        this.playerData.addPokemon(p.getUUID());
        this.playerData.changeRedeems(-1);

        this.response = "Successfully redeemed a **" + target.getName() + "**!\n*Note: Redeemed Pokemon are not registered to your PokeDex or count towards credit rewards from collection milestones.*";

        return true;
    }

    @Override
    protected boolean autocompleteLogic(CommandAutoCompleteInteractionEvent event)
    {
        if(event.getFocusedOption().getName().equals("name"))
            event.replyChoiceStrings(this.getAutocompleteOptions(event.getFocusedOption().getValue(), Arrays.stream(PokemonEntity.values()).filter(e -> !e.isNotSpawnable()).map(PokemonEntity::getName).toList())).queue();

        return true;
    }
}
