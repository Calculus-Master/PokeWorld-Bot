package com.calculusmaster.pokecord.commands.pokemon;

import com.calculusmaster.pokecord.commands.CommandData;
import com.calculusmaster.pokecord.commands.PokeWorldCommand;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonEntity;
import com.calculusmaster.pokecord.game.pokemon.evolution.EvolutionData;
import com.calculusmaster.pokecord.game.pokemon.evolution.EvolutionRegistry;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CommandEvolve extends PokeWorldCommand
{
    public static void init()
    {
        CommandData
                .create("evolve")
                .withConstructor(CommandEvolve::new)
                .withFeature(Feature.EVOLVE_POKEMON)
                .withCommand(Commands
                        .slash("evolve", "Evolve your Pokemon, if eligible!")
                        .addSubcommands(
                                new SubcommandData("pokemon", "Evolve your active Pokemon.")
                                        .addOption(OptionType.STRING, "target", "Optional: Specific evolution to evolve into. If not present, the first valid target will be chosen."),
                                new SubcommandData("info", "View the evolution requirements for your active Pokemon.")
                        )
                )
                .register();
    }

    @Override
    protected boolean slashCommandLogic(SlashCommandInteractionEvent event)
    {
        String subcommand = Objects.requireNonNull(event.getSubcommandName());

        Pokemon active = this.playerData.getSelectedPokemon();
        if(!EvolutionRegistry.hasEvolutionData(active.getEntity())) return this.error(active.getName() + " cannot evolve!");

        if(subcommand.equals("pokemon"))
        {
            OptionMapping targetOption = event.getOption("target");
            List<EvolutionData> evolutionOptions = EvolutionRegistry.getEvolutionData(active.getEntity());

            EvolutionData targetData = null;

            if(targetOption != null)
            {
                PokemonEntity input = PokemonEntity.cast(targetOption.getAsString());

                if(input == null) return this.error("\"" + targetOption.getAsString() + "\" is not a valid Pokemon name.");
                else
                {
                    EvolutionData specificTargetData = evolutionOptions.stream().filter(d -> d.getTarget().equals(input)).findFirst().orElse(null);

                    if(specificTargetData == null) return this.error(active.getName() + " cannot evolve into a " + input.getName() + ".");
                    else if(specificTargetData.validate(active, this.server.getId())) targetData = specificTargetData;
                }
            }
            else for(EvolutionData data : evolutionOptions)
                if(data.validate(active, this.server.getId())) { targetData = data; break; }

            if(targetData != null)
            {
                String original = active.hasNickname() ? active.getDisplayName()  + " (" + active.getEntity().getName() + ")" : active.getName();

                active.evolve(targetData, this.playerData);

                this.response = "Congratulations! " + original + " evolved into a **" + active.getName() + "**!";
                return true;
            }
            else //If this stage is reached, evolution did not take place
                this.response = active.getName() + " did not meet fulfill its evolution requirements. Check its evolution requirements in detail using `/evolve info`.";
        }
        else if(subcommand.equals("info"))
        {
            List<EvolutionData> evolutionOptions = EvolutionRegistry.getEvolutionData(active.getEntity());
            boolean single = evolutionOptions.size() == 1;

            for(int i = 0; i < evolutionOptions.size(); i++)
            {
                EvolutionData data = evolutionOptions.get(i);

                String fieldName = (single ? "" : "Option " + (i + 1) + ": ") + data.getSource().getName() + " ---> " + data.getTarget().getName();

                this.embed.addField(fieldName, data.getTriggers().stream()
                                .map(t -> "- " + t.getDescription() + (t.canEvolve(active, this.server.getId()) ? " :white_check_mark:" : " :x:"))
                                .collect(Collectors.joining("\n")),
                        false);
            }

            this.embed
                    .setTitle("Evolution Information for " + active.getName())
                    .setDescription("""
                    You can view the possible evolutions your active Pokemon has below.
                    Evolutions require various things in order to take place – in most cases, evolution will happen automatically when you perform an action related to the trigger.
                    However, if this does not occur, you can use `/evolve pokemon` to check the requirements for evolution, and evolve your active Pokemon if they are satisfied.
                    """);
        }

        return true;
    }
}
