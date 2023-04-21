package com.calculusmaster.pokecord.commands.player;

import com.calculusmaster.pokecord.Pokeworld;
import com.calculusmaster.pokecord.commands.CommandData;
import com.calculusmaster.pokecord.commands.PokeWorldCommand;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.player.leaderboard.LeaderboardScoreComponent;
import com.calculusmaster.pokecord.game.player.leaderboard.PlayerScoreData;
import com.calculusmaster.pokecord.game.player.leaderboard.PokeWorldLeaderboard;
import com.calculusmaster.pokecord.mongo.PlayerData;
import kotlin.Pair;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.Arrays;
import java.util.Objects;

public class CommandLeaderboard extends PokeWorldCommand
{
    public static void init()
    {
        CommandData
                .create("leaderboard")
                .withConstructor(CommandLeaderboard::new)
                .withFeature(Feature.ACCESS_LEADERBOARD)
                .withCommand(Commands
                        .slash("leaderboard", "View the global leaderboard!")
                        .addSubcommands(
                                new SubcommandData("global", "View the top " + Pokeworld.NAME + " players."),
                                new SubcommandData("score", "View a particular player's score calculation.")
                                        .addOption(OptionType.USER, "player", "Optional: The player to view the score calculation of. If not provided, view your own score.", false)
                        )
                )
                .register();
    }

    @Override
    protected boolean slashCommandLogic(SlashCommandInteractionEvent event)
    {
        String subcommand = Objects.requireNonNull(event.getSubcommandName());

        if(subcommand.equals("global"))
        {
            PokeWorldLeaderboard current = PokeWorldLeaderboard.getCurrent();
            Pair<String, String> top = current.getTop();
            int ranking = current.getRanking(this.playerData.getID());

            String rankingText = ranking <= 0 ? "*Your ranking has not been calculated yet.*" : "You are ranked **#" + ranking + "** out of **" + current.size() + "** players!";

            this.embed
                    .setTitle(Pokeworld.NAME + " Global Leaderboard")
                    .setDescription("""
                            **Your Ranking**: %s
                            
                            **Leaderboard Last Calculated**: %s
                            """.formatted(rankingText, current.getTimestamp()))
                    .addField("Top Trainers", top.getFirst(), true)
                    .addField("Score", top.getSecond(), true)
                    .addBlankField(true)
                    .setFooter("View the score breakdown for you or any top trainer using /leaderboard score!");
        }
        else if(subcommand.equals("score"))
        {
            OptionMapping userOption = event.getOption("player");

            String id = userOption == null ? this.player.getId() : userOption.getAsUser().getId();

            if(!PlayerData.isRegistered(id)) return this.error("That player has not started their journey with " + Pokeworld.NAME + ".");

            PokeWorldLeaderboard current = PokeWorldLeaderboard.getCurrent();
            int ranking = current.getRanking(id);
            String username = userOption == null ? this.player.getName() : userOption.getAsUser().getName();

            if(ranking <= 0) return this.error("The leaderboard calculation for " + username + " has not been run yet. It was last run at " + current.getTimestamp() + ".");

            PlayerScoreData scoreData = current.getScoreData(id);

            this.embed
                    .setTitle("Leaderboard Score Calculation for: " + username)
                    .setDescription("""
                            %s currently ranks **%s** out of a total **%s** players, with a total score of `%s`!
                            
                            Below is a breakdown of the components that make up their score on the leaderboard.
                            """.formatted(username, ranking, current.size(), String.format("%.2f", scoreData.getScore())));

            Arrays.stream(LeaderboardScoreComponent.values()).forEach(c -> this.embed.addField(c.getName(), """
                    **Score**: `%s`
                    **Weight**: `%s`
                    """.formatted(scoreData.getScore(c), c.getWeight()), true));
        }

        return true;
    }
}
