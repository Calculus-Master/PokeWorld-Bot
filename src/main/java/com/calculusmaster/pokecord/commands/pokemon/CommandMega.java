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
                        .addOption(OptionType.STRING, "type", "Mega-Evolve into an X or Y form, if available.", false, true)
                )
                .register();
    }

    @Override
    protected boolean slashCommandLogic(SlashCommandInteractionEvent event)
    {
        Pokemon p = this.playerData.getSelectedPokemon();

        if(!MegaEvolutionRegistry.hasMegaData(p.getEntity())) return this.error("This Pokemon cannot Mega Evolve.");
        else
        {
            OptionMapping optionXY = event.getOption("type");
            MegaEvolutionRegistry.MegaEvolutionData megaData = MegaEvolutionRegistry.getData(p.getEntity());

            if(MegaEvolutionRegistry.isMega(p.getEntity()))
            {
                this.response = p.getName() + " has reverted from its Mega Evolution into " + megaData.getBase().getName() + "!";

                p.removeMegaEvolution();

                MegaChargeManager.removeBlocking(p.getUUID());
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

                if(this.playerData.getOwnedMegas().contains(target))
                {
                    this.response = p.getName() + " has Mega-Evolved into " + megaData.getMega().getName() + "!";

                    p.changePokemon(megaData.getMega());
                    p.updateEntity();

                    MegaChargeManager.blockCharging(p.getUUID());
                }
                else return this.error("You do not own the Mega Evolution: " + megaData.getMega().getName() + ".");
            }
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
