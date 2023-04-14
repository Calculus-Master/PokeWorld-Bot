package com.calculusmaster.pokecord.commands.economy;

import com.calculusmaster.pokecord.commands.CommandData;
import com.calculusmaster.pokecord.commands.PokeWorldCommand;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.objectives.Bounty;
import com.calculusmaster.pokecord.game.objectives.types.AbstractObjective;
import com.calculusmaster.pokecord.game.player.PlayerBounties;
import com.calculusmaster.pokecord.util.enums.StatisticType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CommandBounties extends PokeWorldCommand
{
    public static void init()
    {
        CommandData
                .create("bounties")
                .withConstructor(CommandBounties::new)
                .withFeature(Feature.ACCESS_BOUNTIES)
                .withCommand(Commands
                        .slash("bounties", "Manage your bounties!")
                        .addSubcommands(
                                new SubcommandData("view", "View your current bounties and their progress."),
                                new SubcommandData("complete", "Complete a bounty and claim your reward.")
                                        .addOption(OptionType.INTEGER, "number", "The number of the bounty you want to complete.", true),
                                new SubcommandData("reroll", "Reroll a bounty's objectives. Note: This will reduce the rewards slightly.")
                                        .addOption(OptionType.INTEGER, "number", "The number of the bounty you want to reroll.", true)
                        )
                )
                .register();
    }

    @Override
    protected boolean slashCommandLogic(SlashCommandInteractionEvent event)
    {
        //TODO: Bounty acquisition (bounty board)
        String subcommand = Objects.requireNonNull(event.getSubcommandName());

        if(subcommand.equals("view"))
        {
            PlayerBounties bounties = this.playerData.getBounties();

            if(bounties.getBounties().isEmpty()) return this.error("You do not have any bounties currently.");

            this.embed
                    .setTitle(this.player.getName() + "'s Bounties")
                    .setDescription("""
                            Here are all your current bounties and their completion progress.
                            Once all sub-objectives of a Bounty are complete, you can complete it and collect your reward using `/bounties complete`.
                            """);

            for(int i = 0; i < bounties.getBounties().size(); i++)
            {
                Bounty bounty = bounties.getBounties().get(i);

                String title = "Bounty #%s (+%sc)".formatted(i + 1, bounty.getReward());

                List<String> objectivesContents = new ArrayList<>(), progress = new ArrayList<>();
                for(AbstractObjective o : bounty.getObjectives())
                {
                    objectivesContents.add("- " + o.getDescription());
                    progress.add(o.getStatus());
                }

                if(bounty.isComplete()) objectivesContents.add("***__Bounty Complete!__***");

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

            PlayerBounties bounties = this.playerData.getBounties();
            if(numberInput < 1 || numberInput > bounties.getBounties().size()) return this.error("Invalid bounty number.");

            Bounty bounty = bounties.getBounties().get(numberInput - 1);
            if(!bounty.isComplete()) return this.error("This bounty's objectives have not been completed!");

            this.playerData.changeCredits(bounty.getReward());

            this.playerData.getStatistics().increase(StatisticType.BOUNTIES_COMPLETED);

            bounties.remove(numberInput - 1);

            this.response = "Successfully claimed bounty! (**+" + bounty.getReward() + "c**).";
        }
        else if(subcommand.equals("reroll"))
        {
            OptionMapping numberOption = Objects.requireNonNull(event.getOption("number"));
            int numberInput = numberOption.getAsInt();

            PlayerBounties bounties = this.playerData.getBounties();
            if(numberInput < 1 || numberInput > bounties.getBounties().size()) return this.error("Invalid bounty number.");

            Bounty bounty = bounties.getBounties().get(numberInput - 1);

            if(bounty.getReward() <= 50) return this.error("You cannot reroll this bounty any further.");
            else if(bounty.isComplete()) return this.error("This bounty is already complete! You can claim its rewards using `/bounties complete`");

            int originalReward = bounty.getReward();
            bounty.reroll();
            bounties.updateBounty(numberInput - 1);

            this.response = "Successfully rerolled the bounty! You can view its new objectives using `/bounties view`. The reward was reduced from **" + originalReward + "c** to **" + bounty.getReward() + "c**.";
        }
        return true;
    }
}
