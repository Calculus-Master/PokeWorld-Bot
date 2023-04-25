package com.calculusmaster.pokecord.commands.move;

import com.calculusmaster.pokecord.commands.CommandData;
import com.calculusmaster.pokecord.commands.PokeWorldCommand;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.enums.items.TM;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CommandTM extends PokeWorldCommand
{
    public static void init()
    {
        CommandData
                .create("tm")
                .withConstructor(CommandTM::new)
                .withFeature(Feature.TEACH_TMS)
                .withCommand(Commands
                        .slash("tm", "Teach your active Pokemon a new move, from a Technical Machine!")
                        .addSubcommands(
                                new SubcommandData("teach", "Teach your active Pokemon a move from an owned TM. Note: This will use up the TM.")
                                        .addOption(OptionType.STRING, "tm", "The TM you want to teach your active Pokemon.", true, true),
                                new SubcommandData("info", "View what move a TM teaches.")
                                        .addOption(OptionType.STRING, "tm", "The TM you want to view information about.", true, true)
                        )
                )
                .register();
    }

    @Override
    protected boolean slashCommandLogic(SlashCommandInteractionEvent event)
    {
        String subcommand = Objects.requireNonNull(event.getSubcommandName());

        if(subcommand.equals("teach"))
        {
            OptionMapping tmOption = Objects.requireNonNull(event.getOption("tm"));
            String inputTMName = tmOption.getAsString();

            if(TM.cast(inputTMName) == null) return this.error("\"" + inputTMName + "\" is not a valid TM!");

            TM tm = TM.cast(inputTMName);
            Pokemon active = this.playerData.getSelectedPokemon();

            if(!this.playerData.getInventory().hasTM(tm)) return this.error("You do not own any " + tm + "!");
            else if(!active.getData().getTMs().contains(tm.getMove())) return this.error(active.getName() + " cannot learn " + tm + ".");
            else if(active.getTMs().contains(tm.getMove())) return this.error(active.getName() + " has already been taught " + tm + " (Move: " + tm.getMove().getName() + ")!");
            else if(active.getTMs().size() == active.getMaxTMs()) return this.error(active.getName() + " has reached its maximum number of TMs (**" + active.getMaxTMs() + "**), and cannot learn any new TMs.");
            {
                this.playerData.getInventory().removeTM(tm);

                active.addTM(tm);
                active.updateTMs();

                this.response = "Taught " + active.getName() + " **" + tm + "**! *It can now learn " + tm.getMove().getName() + "*.";
            }
        }
//        else if(subcommand.equals("remove"))
//        {
//            //TODO: Reimplement with another item (like a tm casing or something, to retrieve tms from a Pokemon)
//            Pokemon active = this.playerData.getSelectedPokemon();
//
//            if(!active.hasTM()) return this.error(active.getName() + " is not holding a TM currently.");
//            else
//            {
//                TM tm = active.getTM();
//
//                active.setTM();
//                active.updateTM();
//
//                boolean hadMove = false;
//                while(active.getMoves().contains(tm.getMove()))
//                {
//                    hadMove = true;
//                    active.getMoves().set(active.getMoves().indexOf(tm.getMove()), MoveEntity.TACKLE);
//                }
//
//                this.playerData.getInventory().addTM(tm);
//                if(hadMove) active.updateMoves();
//
//                this.response = "**" + tm + "** has been removed from " + active.getName() + " and returned to your inventory!" + (hadMove ? "\n*" + active.getName() + " has forgotten " + tm.getMove().getName() + "*." : "");
//            }
//        }
        else if(subcommand.equals("info"))
        {
            OptionMapping tmOption = Objects.requireNonNull(event.getOption("tm"));
            TM tm = TM.cast(tmOption.getAsString());

            if(tm == null) return this.error("\"" + tmOption.getAsString() + "\" is not a valid TM!");
            else this.response = tm + " teaches a Pokemon **" + tm.getMove().getName() + "**.\nView information about this move with the command: `/moves info move:" + tm.getMove().getName() + "`.";
        }

        return true;
    }

    @Override
    protected boolean autocompleteLogic(CommandAutoCompleteInteractionEvent event)
    {
        if(event.getFocusedOption().getName().equals("tm"))
        {
            String input = event.getFocusedOption().getValue();

            List<String> tmNames = Arrays.stream(TM.values()).map(Enum::toString).toList();

            event.replyChoiceStrings(this.getAutocompleteOptions(input, tmNames)).queue();
        }

        return true;
    }
}
