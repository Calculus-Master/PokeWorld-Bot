package com.calculusmaster.pokecord.game;

import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public enum Achievements
{
    //TODO: Add more Achievements
    START_JOURNEY(250, "Started your journey!");

    public int credits;
    public String desc;
    Achievements(int credits, String desc)
    {
        this.credits = credits;
        this.desc = desc;
    }

    public static void grant(String playerID, Achievements a, MessageReceivedEvent event)
    {
        PlayerDataQuery p = new PlayerDataQuery(playerID);

        if(!p.getAchievementsList().contains(a.toString()))
        {
            p.addAchievement(a);
            p.changeCredits(a.credits);

            event.getChannel().sendMessage(p.getMention() + ": Unlocked an achievement: \"" + a.desc + "\"").queue();
        }
    }

    public static Achievements asAchievement(String a)
    {
        for(Achievements s : values()) if(s.toString().equalsIgnoreCase(a)) return s;
        return null;
    }
}
