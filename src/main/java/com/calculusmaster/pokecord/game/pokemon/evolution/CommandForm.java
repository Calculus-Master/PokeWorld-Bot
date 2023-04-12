package com.calculusmaster.pokecord.game.pokemon.evolution;

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
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CommandForm extends PokeWorldCommand
{
    public static void init()
    {
        CommandData
                .create("form")
                .withConstructor(CommandForm::new)
                .withFeature(Feature.ACQUIRE_POKEMON_FORMS)
                .withCommand(Commands
                        .slash("form", "Switch Pokemon forms!")
                        .addSubcommands(
                                new SubcommandData("info", "View information about a Pokemon's forms.")
                                        .addOption(OptionType.STRING, "name", "Optional: Name of the Pokemon, other than your active.", false, true),
                                new SubcommandData("switch", "Switch your active Pokemon's form, if available.")
                                        .addOption(OptionType.STRING, "name", "The name of the Pokemon form you want to switch to.", true, true)
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
            OptionMapping nameOption = event.getOption("name");

            PokemonEntity entity;
            if(nameOption != null)
            {
                String nameInput = nameOption.getAsString();
                entity = PokemonEntity.cast(nameInput);
                if(entity == null) return this.error("\"" + nameOption.getAsString() + "\" is not a valid Pokemon name!");
            }
            else entity = this.playerData.getSelectedPokemon().getEntity();

            if(!FormRegistry.hasFormData(entity)) return this.error(entity.getName() + " does not have any alternate forms.");

            FormRegistry.FormData formData = FormRegistry.getFormData(entity);

            this.embed
                    .setTitle(entity.getName() + " – Alternate Forms")
                    .setDescription("""
                            A Pokemon's *Base Form* is one that is always obtainable from catching.
                            
                            A Pokemon's other forms maybe be possible to acquire from catching, or some other method.
                            However, some forms may not be "switchable" - this means you cannot manually switch your active Pokemon to that form, as the form changes will happen automatically when reaching certain requirements.
                            
                            To switch to a Pokemon form, you must acquire it first.
                            """)
                    .addField("Base Form", "**" + formData.getDefaultForm().getName() + "**", true)
                    .addField("Switchable", formData.isSwitchable() ? ":white_check_mark:" : ":x:", true)
                    .addField("Others", formData.getForms().stream()
                            .filter(e -> e != formData.getDefaultForm())
                            .map(e -> "**" + e.getName() + "**")
                            .collect(Collectors.joining("\n")),
                            false);
        }
        else if(subcommand.equals("switch"))
        {
            OptionMapping nameOption = Objects.requireNonNull(event.getOption("name"));
            String nameInput = nameOption.getAsString();

            PokemonEntity target = PokemonEntity.cast(nameInput);
            Pokemon active = this.playerData.getSelectedPokemon();

            if(target == null) return this.error("\"" + nameInput + "\" is not a valid Pokemon name!");
            else if(!FormRegistry.hasFormData(active.getEntity())) return this.error(active.getName() + " does not have any alternate forms.");

            FormRegistry.FormData formData = FormRegistry.getFormData(active.getEntity());

            if(target.equals(active.getEntity())) return this.error(active.getName() + " is already in the form: " + target.getName() + ".");
            else if(!formData.isSwitchable()) return this.error(active.getName() + "'s forms are not manually switchable.");
            else if(!formData.getForms().contains(target)) return this.error(target.getName() + " is not a form that " + active.getName() + " can switch to.");
            else if(!this.playerData.getOwnedForms().contains(target)) return this.error("You have not yet acquired this Pokemon form.");

            String original = active.getName();

            active.changeForm(target, this.playerData);

            this.response = "**" + original + "** has switched to the form **" + target.getName() + "**!";
        }

        return true;
    }

    @Override
    protected boolean autocompleteLogic(CommandAutoCompleteInteractionEvent event)
    {
        if(event.getFocusedOption().getName().equals("name"))
        {
            List<String> options = Arrays.stream(PokemonEntity.values()).filter(FormRegistry::hasFormData).map(PokemonEntity::getName).toList();
            event.replyChoiceStrings(this.getAutocompleteOptions(event.getFocusedOption().getValue(), options)).queue();
        }

        return true;
    }
}
