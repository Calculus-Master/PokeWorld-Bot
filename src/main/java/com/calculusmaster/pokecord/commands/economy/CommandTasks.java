package com.calculusmaster.pokecord.commands.economy;

import com.calculusmaster.pokecord.Pokeworld;
import com.calculusmaster.pokecord.commands.CommandData;
import com.calculusmaster.pokecord.commands.PokeWorldCommand;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.objectives.ObjectiveType;
import com.calculusmaster.pokecord.game.objectives.ResearchTask;
import com.calculusmaster.pokecord.game.objectives.types.AbstractObjective;
import com.calculusmaster.pokecord.game.player.PlayerResearchTasks;
import com.calculusmaster.pokecord.game.world.PokeWorldResearchBoard;
import com.calculusmaster.pokecord.util.enums.StatisticType;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CommandTasks extends PokeWorldCommand
{
    public static void init()
    {
        CommandData
                .create("tasks")
                .withConstructor(CommandTasks::new)
                .withFeature(Feature.ACCESS_BOUNTIES)
                .withCommand(Commands
                        .slash("tasks", "Manage your Research Tasks!")
                        .addSubcommands(
                                new SubcommandData("view", "View your current research tasks and their progress."),
                                new SubcommandData("complete", "Complete a research task and claim your reward.")
                                        .addOption(OptionType.INTEGER, "number", "The number of the task you want to complete.", true),
                                new SubcommandData("accept", "Accept a task from the Research Board.")
                                        .addOption(OptionType.INTEGER, "number", "The number of the task you want to accept.", true),
                                new SubcommandData("board", "View the %s Research Board and see what tasks are available.".formatted(Pokeworld.NAME))
                        )
                )
                .register();
    }

    @Override
    protected boolean slashCommandLogic(SlashCommandInteractionEvent event)
    {
        //TODO: task acquisition (research board)
        String subcommand = Objects.requireNonNull(event.getSubcommandName());

        if(subcommand.equals("view"))
        {
            PlayerResearchTasks tasks = this.playerData.getResearchTasks();

            if(tasks.getResearchTasks().isEmpty()) return this.error("You do not have any tasks currently.");

            this.embed
                    .setTitle(this.player.getName() + "'s Research Tasks")
                    .setDescription("""
                            Here are all your current Research Tasks and their completion progress.
                            Once all sub-objectives of a task are complete, you can complete it and collect your reward using `/tasks complete`.
                            """);

            for(int i = 0; i < tasks.getResearchTasks().size(); i++)
            {
                ResearchTask researchTask = tasks.getResearchTasks().get(i);

                String title = "Research Task #%s (+%sc)".formatted(i + 1, researchTask.getReward());

                List<String> objectivesContents = new ArrayList<>(), progress = new ArrayList<>();
                for(AbstractObjective o : researchTask.getObjectives())
                {
                    objectivesContents.add("- " + o.getDescription());
                    progress.add(o.getStatus());
                }

                if(researchTask.isComplete()) objectivesContents.add("***__Task Complete!__***");

                this.embed
                        .addField(title, String.join("\n", objectivesContents), true)
                        .addField("Progress", String.join("\n", progress), true)
                        .addBlankField(true);
            }
        }
        else if(subcommand.equals("complete"))
        {
            OptionMapping numberOption = Objects.requireNonNull(event.getOption("number"));
            int numberInput = numberOption.getAsInt();

            PlayerResearchTasks tasks = this.playerData.getResearchTasks();
            if(numberInput < 1 || numberInput > tasks.getResearchTasks().size()) return this.error("Invalid task number.");

            ResearchTask researchTask = tasks.getResearchTasks().get(numberInput - 1);
            if(!researchTask.isComplete()) return this.error("This task's objectives have not been completed!");

            this.playerData.changeCredits(researchTask.getReward());

            this.playerData.getStatistics().increase(StatisticType.TASKS_COMPLETED);

            tasks.remove(numberInput - 1);

            this.response = "Successfully completed task! (**+" + researchTask.getReward() + "c**).";
        }
        else if(subcommand.equals("accept"))
        {
            OptionMapping numberOption = Objects.requireNonNull(event.getOption("number"));
            int numberInput = numberOption.getAsInt();

            PokeWorldResearchBoard board = PokeWorldResearchBoard.getCurrentResearchBoard();
            if(numberInput < 1 || numberInput > board.getTaskHeaders().size()) return this.error("Invalid task number.");

            PlayerResearchTasks tasks = this.playerData.getResearchTasks();
            if(tasks.getResearchTasks().size() == PlayerResearchTasks.MAX_TASKS) return this.error("You have reached your capacity for Research Tasks. Complete them to accept more from the Research Board!");

            List<ObjectiveType> taskObjectives = board.getTaskHeaders().get(numberInput - 1);

            if(tasks.getResearchTasks().stream().anyMatch(t -> CollectionUtils.isEqualCollection(t.getObjectives().stream().map(AbstractObjective::getObjectiveType).toList(), taskObjectives)))
                return this.error("You have already this Research Task. Complete it to accept another one!");

            ResearchTask task = ResearchTask.create(taskObjectives);
            tasks.add(task);

            this.response = "You've successfully accepted a new task from the Research Board! View its exact requirements and reward amount using `/tasks view`.";
        }
        else if(subcommand.equals("board"))
        {
            PokeWorldResearchBoard board = PokeWorldResearchBoard.getCurrentResearchBoard();

            this.embed
                    .setTitle(Pokeworld.NAME + " Research Board")
                    .setDescription("The %s Research Team is always looking for more data on the world of Pokemon. You can help them out in various ways.".formatted(Pokeworld.NAME));

            //Research Tasks
            this.embed.addField("Research Tasks Information", """
                    You can help out the team by enlisting tasks from them.
                    Research Tasks are quick objectives that you can complete to earn credits.
                    
                    Claim a task using `/tasks accept`, and then once you've completed its objective(s), claim its reward using `/tasks complete`.
                    """, false);

            for(int i = 0; i < board.getTaskHeaders().size(); i++)
            {
                this.embed.addField("Task " + (i + 1),
                        board.getTaskHeaders().get(i).stream().map(o -> "- " + o.getDescription()).collect(Collectors.joining("\n")),
                        true);
            }

            if(board.getTaskHeaders().size() % 3 != 0)
                for(int i = 0; i < (3 - board.getTaskHeaders().size() % 3); i++)
                    this.embed.addBlankField(true);

            //Featured Pokemon
            this.embed.addField("Featured Pokemon", """
                    The %s Research Team also prioritizes a couple Pokemon every time they update the tasks board.
                    As a result, *catching* or *defeating* these Pokemon will reward you additional credits each time, while they're featured!
                    These Pokemon will appear more often, and gain boosted stats in wild Pokemon duels.
                    """.formatted(Pokeworld.NAME), false);

            board.getFeaturedPokemon().forEach(e -> this.embed.addField(e.getName(), EmbedBuilder.ZERO_WIDTH_SPACE, true));

            //Missions
        }
        return true;
    }
}
