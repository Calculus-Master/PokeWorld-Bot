package com.calculusmaster.pokecord.game;

import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public enum Achievements
{
    START_JOURNEY(250, "Started your journey!"),
    CAUGHT_1ST_POKEMON(500, "Caught your first Pokemon!"),
    WON_1ST_DUEL(1000, "Won your first duel!"),
    //TODO: Unimplemented achievements
    CAUGHT_1ST_LEGENDARY(1000, "Caught your first legendary Pokemon!"),
    LISTED_1ST_MARKET(200, "Listed a Pokemon on the market for the first time!"),
    BOUGHT_1ST_MARKET(200, "Bought a Pokemon from the market for the first time!");

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

    //TODO: Uses a thread! Check for performance changes
    public static void grant(String playerID, Achievements a, MessageReceivedEvent event)
    {
        new Thread(() -> {
            PlayerDataQuery p = new PlayerDataQuery(playerID);
            for(int i = 0; i < p.getAchievements().length(); i++) if(((String)(p.getAchievements().get(0))).equalsIgnoreCase(a.toString())) return;

            event.getChannel().sendMessage(grant(playerID, a)).queue();
        }).start();
    }

    public static Achievements asAchievement(String a)
    {
        for(Achievements s : values()) if(s.toString().equalsIgnoreCase(a)) return s;
        return null;
    }
}
