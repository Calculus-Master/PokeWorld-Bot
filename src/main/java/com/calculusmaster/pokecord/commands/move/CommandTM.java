package com.calculusmaster.pokecord.commands.move;

import com.calculusmaster.pokecord.commands.CommandData;
import com.calculusmaster.pokecord.commands.PokeWorldCommand;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.enums.items.Item;
import com.calculusmaster.pokecord.game.enums.items.TM;
import com.calculusmaster.pokecord.game.moves.data.MoveEntity;
import com.calculusmaster.pokecord.game.player.components.PlayerInventory;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.ArrayList;
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
                                new SubcommandData("remove", "Remove a TM move from your active Pokemon, and convert it back into a TM. Requires Machine Discs.")
                                        .addOption(OptionType.STRING, "move", "The TM move you want to remove from your active Pokemon.", true, true),
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
        else if(subcommand.equals("remove"))
        {
            Pokemon active = this.playerData.getSelectedPokemon();
            PlayerInventory inv = this.playerData.getInventory();

            OptionMapping moveOption = Objects.requireNonNull(event.getOption("move"));
            String moveInput = moveOption.getAsString();

            MoveEntity move = MoveEntity.cast(moveInput);
            if(move == null) return this.error("\"" + moveInput + "\" is not a valid Move name.");
            else if(TM.getTM(move) == null) return this.error();

            if(active.getTMs().isEmpty()) return this.error(active.getName() + " does not know any TM moves.");
            else if(!inv.hasItem(Item.MACHINE_DISC)) return this.error("You do not have any Machine Discs available. Buy them from the shop (if available)!");

            TM tm = TM.getTM(move);

            if(active.getMoves().contains(move))
            {
                List<MoveEntity> moves = new ArrayList<>(active.getMoves());
                int index;
                while((index = moves.indexOf(move)) != -1) moves.set(index, MoveEntity.TACKLE);

                active.setMoves(moves);
                active.updateMoves();
            }

            active.removeTM(tm);
            active.updateTMs();

            inv.removeItem(Item.MACHINE_DISC);
            inv.addTM(tm);

            this.response = active.getName() + " has forgotten *" + move.getName() + "* (" + tm + ")! **" + tm + "** has been returned to your inventory.";
        }
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
        String input = event.getFocusedOption().getValue();

        if(event.getFocusedOption().getName().equals("tm"))
        {
            List<String> tmNames = Arrays.stream(TM.values()).map(Enum::toString).toList();
            event.replyChoiceStrings(this.getAutocompleteOptions(input, tmNames)).queue();
        }
        else if(event.getFocusedOption().getName().equals("move"))
        {
            List<String> tmNames = this.playerData.getSelectedPokemon().getTMs().stream().map(MoveEntity::getName).toList();
            event.replyChoiceStrings(this.getAutocompleteOptions(input, tmNames)).queue();
        }

        return true;
    }
}
