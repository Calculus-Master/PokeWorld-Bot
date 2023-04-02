package com.calculusmaster.pokecord.commandslegacy.player;

import com.calculusmaster.pokecord.commandslegacy.CommandLegacy;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.enums.functional.Achievements;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CommandLegacyAchievements extends CommandLegacy
{
    public CommandLegacyAchievements(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public CommandLegacy runCommand()
    {
        if(this.insufficientMasteryLevel(Feature.VIEW_ACHIEVEMENTS)) return this.invalidMasteryLevel(Feature.VIEW_ACHIEVEMENTS);

        List<String> achievementsCompleted = this.playerData.getAchievementsList();
        int completed = achievementsCompleted.size();
        int total = Achievements.values().length;
        int remaining = total - completed;

        this.embed.addField("Progress", "`" + completed + " / " + total + "`\nRemaining: `" + remaining + "`", false);

        StringBuilder others = new StringBuilder();
        if(remaining == 0) others.append("You have completed all currently implemented Achievements!");
        else
        {
            List<Achievements> achievementsRemaining = Arrays.stream(Achievements.values()).filter(a -> !achievementsCompleted.contains(a.toString())).collect(Collectors.toList());
            Collections.shuffle(achievementsRemaining);
            for(int i = 0; i < Math.min(5, achievementsRemaining.size()); i++) others.append("`").append(achievementsRemaining.get(i).desc).append("` - Reward: ").append(achievementsRemaining.get(i).credits).append("\n");
        }

        this.embed.addField("Other Achievements", others.toString(), false);

        this.embed.setTitle(this.player.getName() + "'s Achievement Progress");

        return this;
    }
}
