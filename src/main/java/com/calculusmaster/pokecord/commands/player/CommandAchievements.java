package com.calculusmaster.pokecord.commands.player;

import com.calculusmaster.pokecord.Pokeworld;
import com.calculusmaster.pokecord.commands.CommandData;
import com.calculusmaster.pokecord.commands.PokeWorldCommand;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.enums.functional.Achievement;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.util.ArrayList;
import java.util.List;

public class CommandAchievements extends PokeWorldCommand
{
    public static void init()
    {
        CommandData
                .create("achievements")
                .withConstructor(CommandAchievements::new)
                .withFeature(Feature.VIEW_ACHIEVEMENTS)
                .withCommand(Commands
                        .slash("achievements", "Take a look at the Achievements you've earned!")
                        .addOption(OptionType.USER, "user", "Optional: View the achievement progress of another %s user.".formatted(Pokeworld.NAME), false)
                )
                .register();
    }

    @Override
    protected boolean slashCommandLogic(SlashCommandInteractionEvent event)
    {
        OptionMapping userOption = event.getOption("user");

        PlayerDataQuery target = this.playerData;
        if(userOption != null)
        {
            String targetID = userOption.getAsUser().getId();
            if(PlayerDataQuery.isRegistered(targetID)) target = PlayerDataQuery.of(targetID);
            else return this.error("This user has not started their %s journey!".formatted(Pokeworld.NAME));
        }

        int totalCompleted = target.getAchievements().size();
        int total = Achievement.values().length;

        int standardCompleted = (int)target.getAchievements().stream().filter(a -> !a.isExtreme()).count();
        int standardTotal = Achievement.getStandardCount();

        int extremeCompleted = (int)target.getAchievements().stream().filter(Achievement::isExtreme).count();
        int extremeTotal = Achievement.getExtremeCount();

        List<String> latest = new ArrayList<>();
        latest.add("*These are your most recently completed achievements.*\n");

        for(int i = 0; i < Math.min(5, totalCompleted); i++)
        {
            Achievement a = target.getAchievements().get(totalCompleted - 1 - i);

            latest.add("**" + a.getName() + "** - ||" + a.getDescription() + "||");
        }

        this.embed
                .setTitle(target.getUsername() + "'s Pokemon Achievements")
                .setDescription("Achievements can be earned by completing various tasks in %s!\nExtreme Achievements are often very challenging, and represent the ultimate level of mastery in the bot.".formatted(Pokeworld.NAME))
                .addField("Achievement Progress", """
                        *Standard Achievements:* **%d / %d** (**%s%s** completed)
                        *Extreme Achievements:* **%d / %d** (**%s%s** completed)
                        
                        *Total Achievements:* **%d / %d** (**%s%s** completed)
                        """.formatted(
                                standardCompleted, standardTotal, String.format("%.2f", (double)standardCompleted / standardTotal * 100), "%",
                                extremeCompleted, extremeTotal, String.format("%.2f", (double)extremeCompleted / extremeTotal * 100), "%",
                                totalCompleted, total, String.format("%.2f", (double)totalCompleted / total * 100), "%"
                ), false)
                .addField("Latest Achievements", String.join("\n", latest), false);

        return true;
    }
}
