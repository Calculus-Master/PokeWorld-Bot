package com.calculusmaster.pokecord.game;

import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public enum Achievements
{
    START_JOURNEY(250, "Started your journey!"),
    CAUGHT_FIRST_POKEMON(500, "Caught your first Pokemon!"),
    BOUGHT_FIRST_POKEMON_MARKET(500, "Bought your first Pokemon from the market!"),
    WON_FIRST_PVP_DUEL(500, "Won your first PVP (Player vs Player) duel!"),
    WON_FIRST_WILD_DUEL(500, "Defeated a Wild Pokemon for the first time!"),
    WON_FIRST_TRAINER_DUEL(500, "Defeating a Trainer for the first time!"),
    DEFEATED_DAILY_TRAINERS(5000, "Defeated all daily Trainers for the first time!"),
    COMPLETED_FIRST_TRADE(500, "Completed your first trade!"),
    SUBMITTED_BUG_REPORT(500, "Submitted a bug report/suggestion! Thank you!"),
    DEFEATED_FIRST_GYM_LEADER(1000, "Defeated your first Gym Leader!");

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
