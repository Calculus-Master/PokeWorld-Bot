package com.calculusmaster.pokecord.commands.move;

import com.calculusmaster.pokecord.commands.CommandData;
import com.calculusmaster.pokecord.commands.PokeWorldCommand;
import com.calculusmaster.pokecord.game.enums.elements.Ability;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonEntity;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CommandAbility extends PokeWorldCommand
{
    public static void init()
    {
        CommandData
                .create("ability")
                .withConstructor(CommandAbility::new)
                .withCommand(Commands
                        .slash("ability", "View information about a specific ability.")
                        .addSubcommands(
                                new SubcommandData("info", "View information about a specific ability.")
                                        .addOption(OptionType.STRING, "ability", "The name of the ability to view information about.", true, true)
                        )
                )
                .register();
    }

    @Override
    protected boolean slashCommandLogic(SlashCommandInteractionEvent event)
    {
        OptionMapping abilityOption = Objects.requireNonNull(event.getOption("ability"));
        String abilityInput = abilityOption.getAsString();

        Ability ability = Ability.cast(abilityInput);
        if(ability == null) return this.error("\"" + abilityInput + "\" is not a valid ability name!");

        List<String> effectTextPool = ability.data().getEffectText();
        String effects = effectTextPool.isEmpty() ? "*Not available*." : "*" + effectTextPool.get(this.random.nextInt(effectTextPool.size())) + "*";

        List<String> flavorTextPool = ability.data().getFlavorText();
        String flavorText = flavorTextPool.isEmpty() ? "*Not available*." : flavorTextPool.get(this.random.nextInt(flavorTextPool.size()));

        this.embed
                .setTitle("Ability Info: " + ability.getName())
                .addField("Effects", effects, false)
                .addField("Flavor Text", flavorText, false)
                .setFooter("%s Pokemon can have this Ability!".formatted(Arrays.stream(PokemonEntity.values()).filter(e -> e.data().getMainAbilities().contains(ability) || e.data().getHiddenAbilities().contains(ability)).count()));

        return true;
    }

    @Override
    protected boolean autocompleteLogic(CommandAutoCompleteInteractionEvent event)
    {
        String input = event.getFocusedOption().getValue();

        if(event.getFocusedOption().getName().equals("ability"))
            event.replyChoiceStrings(this.getAutocompleteOptions(input, Arrays.stream(Ability.values()).map(Ability::getName).toList())).queue();

        return true;
    }
}
