package com.calculusmaster.pokecord.game;

import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public enum Achievements
{
    START_JOURNEY(250, "Started your journey!"),
    CAUGHT_1ST_POKEMON(500, "Caught your first Pokemon!");

    public int credits;
    public String name;
    Achievements(int credits, String name)
    {
        this.credits = credits;
        this.name = name;
    }

    public static String grant(String playerID, Achievements a)
    {
        PlayerDataQuery p = new PlayerDataQuery(playerID);
        p.addAchievement(a);
        p.changeCredits(a.credits);

        return "Unlocked an achievement: " + a.name + " !";
    }

    public static void grant(String playerID, Achievements a, MessageReceivedEvent event)
    {
        event.getChannel().sendMessage(grant(playerID, a)).queue();
    }

    public static Achievements asAchievement(String a)
    {
        for(Achievements s : values()) if(s.toString().equalsIgnoreCase(a)) return s;
        return null;
    }
}
