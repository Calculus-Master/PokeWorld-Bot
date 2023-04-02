package com.calculusmaster.pokecord.commandslegacy.player;

import com.calculusmaster.pokecord.commandslegacy.CommandLegacy;
import com.calculusmaster.pokecord.game.player.level.MasteryLevelManager;
import com.calculusmaster.pokecord.game.player.level.pmltasks.AbstractPMLTask;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class CommandLegacyLevel extends CommandLegacy
{
    public CommandLegacyLevel(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public CommandLegacy runCommand()
    {
        this.embed.setTitle(this.playerData.getUsername());

        if(!MasteryLevelManager.isMax(this.playerData))
        {
            int level = this.playerData.getLevel();
            List<AbstractPMLTask> tasks = MasteryLevelManager.MASTERY_LEVELS.get(level + 1).getTasks();

            this.embed
                    .addField("Level", "You are `Level " + level + "`", false)
                    .addField("Features", level == 0 ? "You've just started exploring the world of Pokemon! You're able to catch, release, select, evolve, teach moves and organize your Pokemon." : "You recently unlocked the following features:\n" + MasteryLevelManager.MASTERY_LEVELS.get(level).getUnlockedFeaturesOverview(), false);

            for(int i = 0; i < tasks.size(); i++)
            {
                AbstractPMLTask t = tasks.get(i);
                boolean complete = t.isCompleted(this.playerData);

                String title = "Task " + (i + 1);
                String desc = t.getDesc();
                String overview = (complete ? "~~" : "") + t.getProgressOverview(this.playerData) + (complete ? "~~" : "");

                this.embed.addField(title, desc + "\n" + overview, true);
            }

            this.embed.addField("New Features", "Upon reaching Level " + (level + 1) + ", you will unlock:\n" + MasteryLevelManager.MASTERY_LEVELS.get(level + 1).getUnlockedFeaturesOverview(), false);

            this.embed.setDescription("Complete the tasks listed below to proceed to the next Pokemon Mastery Level! Leveling up your PML will unlock new features!");
        }
        else this.embed.setDescription("You have reached the maximum Pokemon Mastery Level!");

        return this;
    }
}
