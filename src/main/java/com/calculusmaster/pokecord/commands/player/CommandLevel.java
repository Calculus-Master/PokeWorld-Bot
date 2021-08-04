package com.calculusmaster.pokecord.commands.player;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.game.player.level.PlayerLevel;
import com.calculusmaster.pokecord.game.player.level.leveltasks.AbstractLevelTask;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class CommandLevel extends Command
{
    public CommandLevel(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        this.embed.setTitle(this.playerData.getUsername());

        if(PlayerLevel.existsNextLevel(this.playerData))
        {
            int level = this.playerData.getLevel();
            List<AbstractLevelTask> tasks = PlayerLevel.LEVEL_REQUIREMENTS.get(level + 1).getTasks();

            this.embed.addField("Level", "You are `Level " + level + "`", false);

            for(int i = 0; i < tasks.size(); i++)
            {
                AbstractLevelTask t = tasks.get(i);
                boolean complete = t.isCompleted(this.playerData);

                String title = "Task " + (i + 1);
                String desc = t.getDesc();
                String overview = (complete ? "~~" : "") + t.getProgressOverview(this.playerData) + (complete ? "~~" : "");

                this.embed.addField(title, desc + "\n" + overview, true);
            }

            this.embed.setDescription("Complete the tasks listed below to level up to the next Pokemon Mastery Level! Pokemon Mastery Level is a progression system that unlocks new features and gives you rewards!");
        }
        else this.embed.setDescription("You have reached the maximum Pokemon Mastery Level!");

        return this;
    }
}