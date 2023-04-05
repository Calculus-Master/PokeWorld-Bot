package com.calculusmaster.pokecord.commands.move;

import com.calculusmaster.pokecord.commands.CommandData;
import com.calculusmaster.pokecord.commands.PokeWorldCommand;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.enums.items.TM;
import com.calculusmaster.pokecord.game.moves.data.MoveEntity;
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
                                new SubcommandData("teach", "Teach your active Pokemon a move from an owned TM.")
                                        .addOption(OptionType.STRING, "tm", "The TM you want to teach your active Pokemon.", true, true),
                                new SubcommandData("remove", "Remove a TM from your active Pokemon. Note: This will also remove the corresponding move.")
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

            if(!active.getData().getTMs().contains(tm.getMove())) return this.error(active.getName() + " cannot learn " + tm + ".");
            else
            {
                this.playerData.getInventory().removeTM(tm);
                this.playerData.updateInventory();

                if(active.hasTM())
                {
                    TM oldTM = active.getTM();
                    MoveEntity oldTMMove = oldTM.getMove();
                    active.setTM(tm);

                    //Remove the old TM's moves
                    String forgotten = "";
                    while(active.getMoves().contains(oldTMMove))
                    {
                        active.getMoves().set(active.getMoves().indexOf(oldTMMove), MoveEntity.TACKLE);
                        forgotten = "\n" + active.getName() + " has forgotten *" + oldTMMove.getName() + "*.";
                    }

                    active.updateTM();
                    active.updateMoves();
                    this.response = "Gave " + active.getName() + " **" + tm + "**, replacing its " + oldTM + "! *It can now learn **" + tm.getMove().getName() + "*." + forgotten;
                }
                else
                {
                    active.setTM(tm);
                    active.updateTM();
                    this.response = "Gave " + active.getName() + " **" + tm + "**! *It can now learn " + tm.getMove().getName() + "*.";
                }
            }
        }
        else if(subcommand.equals("remove"))
        {
            Pokemon active = this.playerData.getSelectedPokemon();

            if(!active.hasTM()) return this.error(active.getName() + " is not holding a TM currently.");
            else
            {
                TM tm = active.getTM();

                active.setTM();
                active.updateTM();

                boolean hadMove = false;
                while(active.getMoves().contains(tm.getMove()))
                {
                    hadMove = true;
                    active.getMoves().set(active.getMoves().indexOf(tm.getMove()), MoveEntity.TACKLE);
                }

                this.playerData.getInventory().addTM(tm);
                this.playerData.updateInventory();
                if(hadMove) active.updateMoves();

                this.response = "**" + tm + "** has been removed from " + active.getName() + " and returned to your inventory!" + (hadMove ? "\n*" + active.getName() + " has forgotten " + tm.getMove().getName() + "*." : "");
            }
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
