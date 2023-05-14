package com.calculusmaster.pokecord.commands.duel;

import com.calculusmaster.pokecord.Pokeworld;
import com.calculusmaster.pokecord.commands.CommandData;
import com.calculusmaster.pokecord.commands.PokeWorldCommand;
import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.duel.extension.TrainerDuel;
import com.calculusmaster.pokecord.game.duel.trainer.TrainerData;
import com.calculusmaster.pokecord.game.duel.trainer.TrainerManager;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CommandTrainerDuel extends PokeWorldCommand
{
    public static void init()
    {
        CommandData
                .create("trainer-duel")
                .withConstructor(CommandTrainerDuel::new)
                .withFeature(Feature.PVE_DUELS_TRAINER)
                .withCommand(Commands
                        .slash("trainer-duel", "Duel randomly generated trainers for rewards!")
                        .addSubcommands(
                                new SubcommandData("view", "View the currently available Trainers."),
                                new SubcommandData("info", "View information about one of the available Trainers.")
                                        .addOption(OptionType.INTEGER, "number", "The number of the Trainer you want to view.", true),
                                new SubcommandData("challenge", "Challenge one of the available Trainers.")
                                        .addOption(OptionType.INTEGER, "number", "The number of the Trainer you want to challenge.", true)
                        )
                )
                .register();
    }

    @Override
    protected boolean slashCommandLogic(SlashCommandInteractionEvent event)
    {
        String subcommand = Objects.requireNonNull(event.getSubcommandName());

        if(subcommand.equals("view"))
        {
            this.embed.setTitle(Pokeworld.NAME + " Trainer Duels")
                    .setDescription("""
                            Every rotation, a group of trainers are randomly generated, and organized into classes as shown below.
                            Most classes of trainers have Team Restrictions - you can view these using `/trainer-duel info`.
                            *Note*: Not all classes of trainers may be visible to you below - in order to see higher classes, you must defeat any trainer from the class prior to it.
                            
                            Defeating all trainers of a class will reward with you a large amount of credits.
                            Defeating all classes will reward you with a redeem!
                            """);

            int trainerNum = 1;
            for(int i = 1; i <= TrainerManager.getPlayerMaxClass(this.playerData); i++)
            {
                List<String> trainerNames = new ArrayList<>(), trainerTeamSizes = new ArrayList<>(), trainerDefeated = new ArrayList<>();

                for(TrainerData d : TrainerManager.getTrainersOfClass(i))
                {
                    trainerNames.add("`%s`: %s".formatted(trainerNum, d.getName()));
                    trainerTeamSizes.add("Level: **%s** | Size: **%s**".formatted(d.getAveragePokemonLevel(), d.getTeam().size()));
                    trainerDefeated.add(this.playerData.hasDefeatedTrainer(d.getTrainerID()) ? ":white_check_mark:" : ":x:");

                    trainerNum++;
                }

                String mainTitle = "Class %s Trainers".formatted(TrainerManager.getRoman(i));

                this.embed
                        .addField(mainTitle, String.join("\n", trainerNames), true)
                        .addField("Team", String.join("\n", trainerTeamSizes), true)
                        .addField("Defeated", String.join("\n", trainerDefeated), true);
            }

            if(!this.isInvalidMasteryLevel(Feature.PVE_DUELS_ELITE)) this.embed.addField("Elite Trainers", "You can duel challenging, randomized Elite Trainers using the `/elite-duel` command.", false);
        }
        else if(subcommand.equals("info"))
        {
            if(this.isInvalidMasteryLevel(Feature.VIEW_TRAINER_INFO)) return this.respondInvalidMasteryLevel(Feature.VIEW_TRAINER_INFO);

            int number = Objects.requireNonNull(event.getOption("number")).getAsInt();
            if(number < 1 || number > TrainerManager.REGULAR_TRAINERS.size()) return this.error("Invalid Trainer number. Use `/trainer-duel view` to see the available trainers.");

            TrainerData data = TrainerManager.REGULAR_TRAINERS.get(number - 1);

            if(TrainerManager.getPlayerMaxClass(this.playerData) < data.getTrainerClass())
                return this.error("You have not unlocked this class of Trainers yet. You must defeat a Class %s Trainer first!".formatted(TrainerManager.getRoman(data.getTrainerClass() - 1)));

            String description;
            if(this.playerData.hasDefeatedTrainer(data.getTrainerID()))
                description = "*You have defeated this Trainer on the current rotation!*";
            else description = """
                    *You have not defeated this Trainer!*
                    *Challenge this trainer with* `/trainer-duel challenge number:%s`.
                    """.formatted(number);

            this.embed
                    .setTitle("Trainer #" + number + ": " + data.getName())
                    .setDescription("""
                            **Class %s**
                            %s
                            """.formatted(TrainerManager.getRoman(data.getTrainerClass()), description))
                    .setFooter("Trainer ID: " + data.getTrainerID())

                    .addField("Average Level", "Level " + data.getAveragePokemonLevel(), true)
                    .addField("Z-Crystal", data.getZCrystal() == null ? "None" : data.getZCrystal().getName(), true)

                    .addField("Team", IntStream.range(0, data.getTeam().size()).mapToObj(i -> (i + 1) + ": " + data.getTeam().get(i).getName()).collect(Collectors.joining("\n")), false)

                    .addField("Team Restrictions", data.getRestrictions().isEmpty() ? "None" : data.getRestrictions().stream().map(tr -> "- " + tr.getDescription()).collect(Collectors.joining("\n")), false)

                    ;
        }
        else if(subcommand.equals("challenge"))
        {
            int number = Objects.requireNonNull(event.getOption("number")).getAsInt();
            if(number < 1 || number > TrainerManager.REGULAR_TRAINERS.size()) return this.error("Invalid Trainer number. Use `/trainer-duel view` to see the available trainers.");

            TrainerData data = TrainerManager.REGULAR_TRAINERS.get(number - 1);

            if(TrainerManager.getPlayerMaxClass(this.playerData) < data.getTrainerClass())
                return this.error("You have not unlocked this class of Trainers yet. You must defeat a Class %s Trainer first!".formatted(TrainerManager.getRoman(data.getTrainerClass() - 1)));

            String warning = "";
            if(this.playerData.getTeam().size() < data.getTeam().size())
                warning = "Warning! The trainer's Pokemon team is larger than yours. It has " + data.getTeam().size() + " Pokemon.";

            if(this.playerData.getTeam().size() > 6)
                return this.error("Your team is too large! You must have 6 or fewer Pokemon in your team to challenge a trainer.");

            List<Pokemon> team = this.playerData.getTeam().getActiveTeamPokemon();
            if(!data.getRestrictions().stream().allMatch(tr -> tr.validate(team)))
                return this.error("Your team does not meet the Trainer's Team Restrictions! Use `/trainer-duel info` to view the trainer's restrictions in more detail.");

            Duel duel = TrainerDuel.create(this.player.getId(), event.getChannel().asTextChannel(), data);

            event.reply("You have challenged %s!\n*%s*".formatted(data.getName(), warning)).queue();
            this.setResponsesHandled();

            duel.sendTurnEmbed();
        }

        return true;
    }
}
