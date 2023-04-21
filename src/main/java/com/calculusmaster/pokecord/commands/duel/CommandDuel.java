package com.calculusmaster.pokecord.commands.duel;

import com.calculusmaster.pokecord.Pokeworld;
import com.calculusmaster.pokecord.commands.CommandData;
import com.calculusmaster.pokecord.commands.PokeWorldCommand;
import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.duel.component.DuelFlag;
import com.calculusmaster.pokecord.game.duel.component.DuelStatus;
import com.calculusmaster.pokecord.game.duel.core.DuelHelper;
import com.calculusmaster.pokecord.game.duel.players.UserPlayer;
import com.calculusmaster.pokecord.game.duel.restrictions.TeamRestrictionRegistry;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.player.components.PlayerTeam;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CommandDuel extends PokeWorldCommand
{
    public static void init()
    {
        CommandData
                .create("duel")
                .withConstructor(CommandDuel::new)
                .withFeature(Feature.PVP_DUELS)
                .withCommand(Commands
                        .slash("duel", "Duel other players with your Pokemon!")
                        .addSubcommands(
                                new SubcommandData("create", "Send a Duel request to another player.")
                                        .addOption(OptionType.USER, "player", "The user you want to Duel.", true)
                                        .addOption(OptionType.INTEGER, "size", "Optional: The max size of teams. If 1 or not provided, active Pokemon will be used.", false)
                                        .addOption(OptionType.BOOLEAN, "z-moves", "Optional: Determine if Z-Moves are allowed in this Duel. If not provided, they will be allowed.", false)
                                        .addOption(OptionType.BOOLEAN, "dynamax", "Optional: Determine if Dynamaxing is allowed in this Duel. If not provided, it will be allowed.", false)
                                        .addOption(OptionType.BOOLEAN, "standard-restriction", "Optional: Determine if the Standard Restriction is active in this Duel. If not provided, it will be.", false),
                                new SubcommandData("accept", "Accept a Duel request from another player."),
                                new SubcommandData("deny", "Deny a Duel request from another player.")
                        )
                )
                .register();
    }

    @Override
    protected boolean slashCommandLogic(SlashCommandInteractionEvent event)
    {
        String subcommand = Objects.requireNonNull(event.getSubcommandName());

        if(subcommand.equals("create"))
        {
            OptionMapping playerOption = Objects.requireNonNull(event.getOption("player"));
            OptionMapping sizeOption = event.getOption("size");

            int size = sizeOption == null ? 1 : sizeOption.getAsInt();
            if(size < 1 || size > PlayerTeam.MAX_TEAM_SIZE) return this.error("Invalid size. The largest size you can select is " + PlayerTeam.MAX_TEAM_SIZE + ".");

            User user = playerOption.getAsUser();
            if(!PlayerDataQuery.isRegistered(user.getId())) return this.error(user.getName() + " has not started their journey in " + Pokeworld.NAME + "!");
            else if(this.player.getId().equals(user.getId())) return this.error("You cannot duel yourself.");

            if(DuelHelper.isInDuel(this.player.getId())) return this.error("You are in a Duel already.");
            else if(DuelHelper.isInDuel(user.getId())) return this.error(user.getName() + " is in a Duel already.");

            boolean standard = event.getOption("standard-restriction") == null || Objects.requireNonNull(event.getOption("standard-restriction")).getAsBoolean();
            boolean zmoves = event.getOption("z-moves") == null || Objects.requireNonNull(event.getOption("z-moves")).getAsBoolean();
            boolean dynamax = event.getOption("dynamax") == null || Objects.requireNonNull(event.getOption("dynamax")).getAsBoolean();

            List<String> warnings = new ArrayList<>();
            if(size != 1)
            {
                if(PlayerDataQuery.build(user.getId()).getTeam().isEmpty()) return this.error(user.getName() + " has not created a Pokemon team.");

                PlayerTeam team = this.playerData.getTeam();

                if(team.size() > size) warnings.add("Your team size is larger than the size of the Duel. Only the first " + size + " Pokemon from your team will be used in the Duel!");
                else if(team.size() < size) warnings.add("Your team size is smaller than the size of the Duel.");

                if(standard && !TeamRestrictionRegistry.STANDARD.validate(team.getActiveTeamPokemon()))
                    return this.error("Your team does not meet the standard team requirements. If you'd like to disable this for the Duel, set the `standard-restriction` option to `false`.");
                else if(standard && !TeamRestrictionRegistry.STANDARD.validate(PlayerDataQuery.build(user.getId()).getTeam().getActiveTeamPokemon()))
                    return this.error(user.getName() + "'s team does not meet the standard team requirements. If you'd like to disable this for the Duel, set the `standard-restriction` option to `false`.");
            }

            Duel duel = Duel.create(this.player.getId(), user.getId(), size, event.getChannel().asTextChannel());
            if(!zmoves) duel.addFlags(DuelFlag.ZMOVES_BANNED);
            if(!dynamax) duel.addFlags(DuelFlag.DYNAMAX_BANNED);
            if(standard) duel.addFlags(DuelFlag.STANDARD_RESTRICTION);

            String response = user.getAsMention() + ": " + this.player.getName() + " has invited you to a Duel! Use `/duel accept` to accept the Duel request, or `/duel deny` to deny the request.";

            this.response = response +
                    (warnings.isEmpty() ? "" : "\nWarning(s) for " + this.player.getName() + ": ||*" + String.join(", ", warnings) + "*||");
        }
        else if(subcommand.equals("accept") || subcommand.equals("deny"))
        {
            if(!DuelHelper.isInDuel(this.player.getId())) return this.error("You have no pending Duel requests.");

            Duel duel = DuelHelper.instance(this.player.getId());

            if(!duel.getStatus().equals(DuelStatus.WAITING)) return this.error("You are in a Duel already.");

            UserPlayer opponent = ((UserPlayer)duel.getOpponent(this.player.getId()));
            if(subcommand.equals("accept"))
            {
                duel.sendTurnEmbed();
                this.response = this.player.getAsMention() + " " + opponent.data.getMention() + " **Duel starting**!";
            }
            else
            {
                DuelHelper.delete(this.player.getId());
                this.response = "You have denied " + opponent.data.getMention() + "'s Duel request.";
            }
        }

        return true;
    }
}
