package com.calculusmaster.pokecord.commands.duel;

import com.calculusmaster.pokecord.commands.CommandData;
import com.calculusmaster.pokecord.commands.PokeWorldCommand;
import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.duel.component.DuelFlag;
import com.calculusmaster.pokecord.game.duel.core.DuelHelper;
import com.calculusmaster.pokecord.game.duel.players.Player;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.enums.elements.StatusCondition;
import com.calculusmaster.pokecord.game.enums.items.ZCrystal;
import com.calculusmaster.pokecord.game.moves.Move;
import com.calculusmaster.pokecord.game.moves.data.MoveEntity;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonEntity;
import com.calculusmaster.pokecord.game.pokemon.evolution.MegaEvolutionRegistry;
import com.calculusmaster.pokecord.util.Global;
import com.calculusmaster.pokecord.util.enums.StatisticType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.Objects;
import java.util.stream.Collectors;

import static com.calculusmaster.pokecord.game.duel.component.DuelActionType.*;

public class CommandUse extends PokeWorldCommand
{
    public static void init()
    {
        CommandData
                .create("use")
                .withConstructor(CommandUse::new)
                .withFeature(Feature.USE_MOVES)
                .withCommand(Commands
                        .slash("use", "Use moves and more actions in Duels!")
                        .addSubcommands(
                                new SubcommandData("move", "Use a standard move in a Duel.")
                                        .addOption(OptionType.INTEGER, "number", "The number of the move you want to use.", true),
                                new SubcommandData("zmove", "Use a Z-Move in a Duel.")
                                        .addOption(OptionType.INTEGER, "number", "The number of the move you want to use.", true),
                                new SubcommandData("dynamax", "Dynamax your Pokemon in a Duel.")
                                        .addOption(OptionType.INTEGER, "number", "The number of the move you want to use.", true),
                                new SubcommandData("swap", "Swap your active Pokemon in a Duel.")
                                        .addOption(OptionType.INTEGER, "number", "The number of the Pokemon you want to swap to.", true)
                        )
                )
                .register();
    }

    @Override
    protected boolean slashCommandLogic(SlashCommandInteractionEvent event)
    {
        String subcommand = Objects.requireNonNull(event.getSubcommandName());
        OptionMapping numberOption = Objects.requireNonNull(event.getOption("number"));
        int num = numberOption.getAsInt();

        Duel duel = DuelHelper.instance(this.player.getId());
        if(duel == null) return this.error("You are not in a Duel!");

        Player player = duel.getPlayer(this.player.getId());

        //Check if action submitted
        if(duel.hasSubmittedAction(this.player.getId())) return this.error("You have already submitted an action this turn.");
        //Check if active is fainted and player is trying to do anything but swap
        else if(!duel.isComplete() && !subcommand.equals("swap") && player.active.isFainted()) return this.error("Your active Pokemon is fainted. Swap out to another Pokemon on your team using `/use swap`.");

        if(subcommand.equals("move"))
        {
            if(num < 1 || num > player.active.getMoves().size()) return this.error("Invalid move number.");

            this.playerData.getStatistics().increase(StatisticType.MOVES_USED);

            duel.submitMove(this.player.getId(), num, MOVE);
        }
        else if(subcommand.equals("swap"))
        {
            if(duel.hasFlag(DuelFlag.SWAP_BANNED)) return this.error("Swapping is not allowed in this Duel.");
            else if(num < 1 || num > player.team.size()) return this.error("Invalid Pokemon number. Use `/team view` to see your current team.");
            else if(player.team.get(num - 1).isFainted()) return this.error("That Pokemon is fainted. Use `/team view` to see which of your team members have not fainted.");
            else if(player.team.get(num - 1).getUUID().equals(player.active.getUUID())) return this.error(player.active.getName() + " is already on the field.");
            else if(!player.active.isFainted())
            {
                if(!duel.data(player.active.getUUID()).canSwap) return this.error("Your active Pokemon cannot swap out right now.");
                else if(player.active.hasStatusCondition(StatusCondition.BOUND)) return this.error("Your active Pokemon is Bound, so it cannot swap out right now.");
                else if(player.active.isDynamaxed()) return this.error("Your active Pokemon is Dynamaxed, so it cannot swap out right now.");
            }

            duel.submitMove(this.player.getId(), num, SWAP);
        }
        else if(subcommand.equals("zmove"))
        {
            if(this.isInvalidMasteryLevel(Feature.USE_Z_MOVES)) return this.respondInvalidMasteryLevel(Feature.USE_Z_MOVES);
            else if(duel.hasFlag(DuelFlag.ZMOVES_BANNED)) return this.error("Z-Moves are not allowed in this Duel.");
            else if(num < 1 || num > player.active.getMoves().size()) return this.error("Invalid move number.");
            else if(this.playerData.getInventory().getEquippedZCrystal() == null) return this.error("You do not have an equipped Z-Crystal.");
            else if(player.usedZMove) return this.error("You have already used a Z-Move during this Duel. Z-Moves can only be used once per Duel.");
            else if(MegaEvolutionRegistry.isMega(player.active.getEntity())) return this.error("Your active Pokemon is Mega-Evolved. Mega-Evolved Pokemon cannot use Z-Moves.");
            else if(player.active.isDynamaxed()) return this.error("Your active Pokemon is Dynamaxed. Z-Moves cannot be used while Dynamaxed.");

            //Get the base move
            MoveEntity move = player.active.getMoves().get(num - 1);

            //Check Z-Crystal compatibility
            if(!ZCrystal.isValid(this.playerData.getInventory().getEquippedZCrystal(), new Move(move), player.active.getEntity())) return this.error("Your equipped Z-Crystal is not compatible with that move.");

            this.playerData.getStatistics().increase(StatisticType.MOVES_USED);
            this.playerData.getStatistics().increase(StatisticType.ZMOVES_USED);

            duel.submitMove(this.player.getId(), num, ZMOVE);
        }
        else if(subcommand.equals("dynamax"))
        {
            if(this.isInvalidMasteryLevel(Feature.DYNAMAX_POKEMON)) return this.respondInvalidMasteryLevel(Feature.DYNAMAX_POKEMON);
            else if(duel.hasFlag(DuelFlag.DYNAMAX_BANNED)) return this.error("Dynamaxing is not allowed in this Duel.");
            else if(num < 1 || num > player.active.getMoves().size()) return this.error("Invalid move number.");
            else if(player.active.isDynamaxed()) return this.error("Your active Pokemon is already Dynamaxed.");
            else if(player.usedDynamax) return this.error("You have already Dynamaxed during this Duel. Dynamaxing can only be done once per Duel.");
            else if(MegaEvolutionRegistry.isMega(player.active.getEntity())) return this.error("Your active Pokemon is Mega-Evolved. Mega-Evolved Pokemon cannot Dynamax.");
            else if(Global.DYNAMAX_BAN_LIST.contains(player.active.getEntity())) return this.error(player.active.getName() + " is banned from Dynamaxing. Pokemon banned from Dynamaxing: " + Global.DYNAMAX_BAN_LIST.stream().map(PokemonEntity::getName).collect(Collectors.joining(", ")));

            this.playerData.getStatistics().increase(StatisticType.MOVES_USED);
            this.playerData.getStatistics().increase(StatisticType.MAX_MOVES_USED);

            duel.submitMove(this.player.getId(), num, DYNAMAX);
        }

        //If execution reaches here, that means no errors have happened, ie an action has been submitted
        duel.checkReady();

        this.ephemeral = true;
        this.response = "Successfully submitted turn action.";

        return true;
    }
}
