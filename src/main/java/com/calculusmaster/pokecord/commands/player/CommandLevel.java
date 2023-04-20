package com.calculusmaster.pokecord.commands.player;

import com.calculusmaster.pokecord.commands.CommandData;
import com.calculusmaster.pokecord.commands.PokeWorldCommand;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.player.level.MasteryLevelManager;
import com.calculusmaster.pokecord.game.player.level.pmltasks.AbstractPMLTask;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.List;

public class CommandLevel extends PokeWorldCommand
{
    private static final Button SEND_EMBED = Button.primary("level_send_embed", "Send Guide");

    public static void init()
    {
        CommandData
                .create("level")
                .withConstructor(CommandLevel::new)
                .withFeature(Feature.VIEW_LEVEL)
                .withButtons(SEND_EMBED.getId())
                .withCommand(Commands
                        .slash("level", "View your Pokemon Mastery Level progress!")
                )
                .register();
    }

    @Override
    protected boolean slashCommandLogic(SlashCommandInteractionEvent event)
    {
        if(MasteryLevelManager.isMax(this.playerData))
        {
            this.embed
                    .setTitle("Pokemon Mastery Level: " + this.playerData.getUsername())
                    .setDescription("""
                            ***Congratulations!***
                            You've reached the maximum Pokemon Mastery Level. Your journey is far from over, however. Keep collecting, battling, and perfecting your Pokemon!
                            """)
                    .addField("Recently Unlocked Features", MasteryLevelManager.MASTERY_LEVELS.get(this.playerData.getLevel()).getUnlockedFeaturesOverview(), false);
        }
        else
        {
            this.embed
                    .setTitle("Pokemon Mastery Level: " + this.playerData.getUsername())
                    .setDescription("""
                            Pokemon Mastery Level is a progression system that unlocks new features as you progress.
                            
                            Current: **__Pokemon Mastery Level %s__**
                            """.formatted(this.playerData.getLevel()));

            if(this.playerData.getLevel() != 0)
                this.embed.addField("Recently Unlocked Features", """
                            Reaching PML %s has unlocked the following features:
                            %s
                            """.formatted(this.playerData.getLevel(), this.playerData.getLevel() == 0 ? "N/A" : MasteryLevelManager.MASTERY_LEVELS.get(this.playerData.getLevel()).getUnlockedFeaturesOverview()), false);

            this.embed.addField("Level Up Tasks", """
                            To reach PML %s, you'll need to complete various tasks.
                            You can view your progress for each task below.
                            """.formatted(this.playerData.getLevel() + 1), false);

            List<AbstractPMLTask> tasks = MasteryLevelManager.MASTERY_LEVELS.get(this.playerData.getLevel() + 1).getTasks();

            for(int i = 0; i < tasks.size(); i++)
            {
                AbstractPMLTask t = tasks.get(i);
                boolean complete = t.isCompleted(this.playerData);

                String title = "Task " + (i + 1);
                String desc = t.getDesc();
                String overview = (complete ? "~~" : "") + t.getProgressOverview(this.playerData) + (complete ? "~~" : "");

                this.embed.addField(title, desc + "\n" + overview, true);
            }

            event.replyEmbeds(this.embed.build()).setActionRow(SEND_EMBED).queue();
            this.setResponsesHandled();
        }

        return true;
    }

    @Override
    protected boolean buttonLogic(ButtonInteractionEvent event)
    {
        if(event.getComponentId().equals(SEND_EMBED.getId()))
        {
            this.playerData.directMessage(MasteryLevelManager.MASTERY_LEVELS.get(this.playerData.getLevel()).getEmbed().build());

            this.ephemeral = true;
            this.response = "The PML guide for your current level has been sent to your DMs!";
        }

        return true;
    }
}
