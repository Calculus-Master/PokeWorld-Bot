package com.calculusmaster.pokecord.commands.player;

import com.calculusmaster.pokecord.Pokecord;
import com.calculusmaster.pokecord.commands.CommandData;
import com.calculusmaster.pokecord.commands.PokeWorldCommand;
import com.calculusmaster.pokecord.game.enums.functional.Achievements;
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
                .withCommand(Commands
                        .slash("achievements", "Take a look at the Achievements you've earned!")
                        .addOption(OptionType.USER, "user", "Optional: View the achievement progress of another %s user.".formatted(Pokecord.NAME), false)
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
            else return this.error("This user has not started their %s journey!".formatted(Pokecord.NAME));
        }

        int completed = target.getAchievementsList().size();
        int total = Achievements.values().length - 1; // -1 because of the "Final" achievement

        float progressPercent = completed / (float)total; // -1 because of the "Final" achievement

        List<String> latest = new ArrayList<>();
        latest.add("*These are your most recently completed achievements.*\n");

        for(int i = 0; i < Math.min(5, completed); i++)
        {
            Achievements a = Achievements.valueOf(target.getAchievementsList().get(completed - 1 - i));

            latest.add(a.desc);
        }

        this.embed
                .setTitle(target.getUsername() + "'s Pokemon Achievements")
                .setDescription("Achievements can be earned by completing various tasks in %s!".formatted(Pokecord.NAME))
                .addField("Achievement Progress", "**" + String.format("%.2f", progressPercent * 100) + "%\n" + completed + " / " + total + "** Achievements Earned.", false)
                .addField("Latest Achievements", String.join("\n", latest), false);

        return true;
    }
}
