package com.calculusmaster.pokecord.commands.pokemon;

import com.calculusmaster.pokecord.commands.CommandData;
import com.calculusmaster.pokecord.commands.PokeWorldCommand;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonEntity;
import com.calculusmaster.pokecord.game.pokemon.evolution.MegaChargeManager;
import com.calculusmaster.pokecord.game.pokemon.evolution.MegaEvolutionRegistry;
import com.calculusmaster.pokecord.util.helpers.LoggerHelper;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.awt.*;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

public class CommandMega extends PokeWorldCommand
{
    public static void init()
    {
        CommandData
                .create("mega")
                .withConstructor(CommandMega::new)
                .withFeature(Feature.ACQUIRE_POKEMON_MEGA_EVOLUTIONS)
                .withCommand(Commands
                        .slash("mega", "Mega-Evolve your Pokemon!")
                        .addSubcommands(
                                new SubcommandData("evolve", "Mega-Evolve a Pokemon, or remove its Mega-Evolution.")
                                        .addOption(OptionType.STRING, "type", "Mega-Evolve into an X or Y form, if available.", false, true),
                                new SubcommandData("charges", "View the status of your active Pokemon's Mega Charges.")
                        )
                )
                .register();
    }

    @Override
    protected boolean slashCommandLogic(SlashCommandInteractionEvent event)
    {
        String subcommand = Objects.requireNonNull(event.getSubcommandName());

        Pokemon p = this.playerData.getSelectedPokemon();
        if(!MegaEvolutionRegistry.hasMegaData(p.getEntity())) return this.error("This Pokemon cannot Mega Evolve.");

        if(subcommand.equals("evolve"))
        {
            OptionMapping optionXY = event.getOption("type");
            MegaEvolutionRegistry.MegaEvolutionData megaData = MegaEvolutionRegistry.getData(p.getEntity());

            if(MegaEvolutionRegistry.isMega(p.getEntity()))
            {
                this.response = p.getName() + " has reverted from its Mega Evolution into " + megaData.getBase().getName() + "!";

                p.removeMegaEvolution();

                MegaChargeManager.removeBlocked(p.getUUID());
            }
            else
            {
                PokemonEntity target;

                if(megaData.isSingle()) target = megaData.getMega();
                else if(megaData.isXY())
                {
                    if(optionXY == null) return this.error(p.getName() + " has both an X and a Y Mega-Evolution. You must specify which one to Mega-Evolve into.");
                    else if(!optionXY.getAsString().equalsIgnoreCase("X") && !optionXY.getAsString().equalsIgnoreCase("Y")) return this.error("Invalid Mega Form input. Allowed values: **X**, **Y**.");
                    else target = optionXY.getAsString().equalsIgnoreCase("X") ? megaData.getMegaX() : megaData.getMegaY();
                }
                else
                {
                    LoggerHelper.error(CommandMega.class, "Mega Evolution Data for " + p.getName() + " returned false for both #isSingle and #isXY.");
                    return this.error();
                }

                if(p.getMegaCharges() == 0) return this.error(p.getName() + " has run out of Mega Charges, and cannot Mega-Evolve until one regenerates! Use `/mega charges` for more information.");
                else if(this.playerData.getOwnedMegas().contains(target))
                {
                    this.response = p.getName() + " has Mega-Evolved into " + target.getName() + "!";

                    p.megaEvolve(target, this.playerData);
                }
                else return this.error("You do not own the Mega Evolution: " + megaData.getMega().getName() + ".");
            }
        }
        else if(subcommand.equals("charges"))
        {
            this.embed
                    .setTitle("Mega Charge Events for " + p.getName())
                    .setDescription("""
                            All Pokemon that can Mega-Evolve have a certain amount of *Mega Charges*.
                            These are consumed when the Mega-Evolved form completes a Duel.
                            
                            Once a charge is consumed, it will begin to regenerate slowly.
                            *Note*: Charges will NOT regenerate while the Pokemon is Mega-Evolved! You must revert it to its base form to let the timer count down for regeneration.
                            
                            If a Pokemon's Mega Charges reach zero, the Pokemon will revert to its base form, and be unable to Mega-Evolve until all of its charges have regenerated.
                            """);

            List<MegaChargeManager.MegaChargeEvent> events = MegaChargeManager.getEvents(p.getUUID());

            String blockedStatus;
            if(events.isEmpty()) blockedStatus = "*" + p.getName() + "* has its maximum Mega Charges.*";
            else if(events.get(0).isBlocked()) blockedStatus = "*Charge Regeneration:* **__Blocked__**.\n--- *" + p.getName() + "'s Mega Charges will not regenerate until it reverts to its base form!*";
            else blockedStatus = "*Charge Regeneration:* **__Active__**.\n--- *" + p.getName() + "'s Mega Charges will regenerate normally.*";

            this.embed.addField("Mega Charge Overview", """
                    *Charges:* %s (Max: %s)
                    
                    %s
                    """.formatted(p.getMegaCharges(), p.getMaxMegaCharges(), blockedStatus), false);

            for(int i = 0; i < events.size(); i++)
            {
                MegaChargeManager.MegaChargeEvent e = events.get(i);
                String status = e.isBlocked() ? "Blocked" : "Charging";

                int time = e.getTime();
                int seconds = time % 60;
                int minutes = (time % 3600) / 60;
                int hours = time / 3600;

                this.embed.addField("Charge Event #" + (i + 1), """
                        *Charge ID:* %s
                        *Status:* %s
                        *Time Left:* `%sH %sM %sS`
                        """.formatted(e.getChargeID(), status, hours, minutes, seconds), true);
            }

            if(events.size() % 3 != 0) IntStream.range(0, 3 - events.size() % 3).forEach(i -> this.embed.addBlankField(true));

            this.embed.setColor(events.get(0).isBlocked() ? Color.RED : Color.GREEN);
        }

        return true;
    }

    @Override
    protected boolean autocompleteLogic(CommandAutoCompleteInteractionEvent event)
    {
        if(event.getFocusedOption().getName().equals("type"))
            event.replyChoiceStrings("X", "Y").queue();
        else return false;

        return true;
    }
}
