package com.calculusmaster.pokecord.game;

import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.util.helpers.CacheHelper;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public enum Achievements
{
    START_JOURNEY(250, "Started your journey!"),
    CAUGHT_FIRST_POKEMON(500, "Caught your first Pokemon!"),
    BOUGHT_FIRST_POKEMON_MARKET(500, "Bought a Pokemon from the market for the first time!"),
    WON_FIRST_PVP_DUEL(500, "Won your first PVP (Player vs Player) duel!"),
    WON_FIRST_WILD_DUEL(500, "Defeated a Wild Pokemon for the first time!"),
    WON_FIRST_TRAINER_DUEL(500, "Defeating a Trainer for the first time!"),
    DEFEATED_DAILY_TRAINERS(5000, "Defeated all daily Trainers for the first time!"),
    COMPLETED_FIRST_TRADE(500, "Completed your first trade!"),
    SUBMITTED_BUG_REPORT(500, "Submitted a bug report/suggestion! Thank you!"),
    DEFEATED_FIRST_GYM_LEADER(1000, "Defeated your first Gym Leader!"),
    BOUGHT_FIRST_ITEM_SHOP(200, "Bought your first item from the shop!"),
    ACQUIRED_FIRST_TYPED_ZCRYSTAL(1000, "Acquired your first generic Z Crystal!"),
    BOUGHT_FIRST_UNIQUE_ZCRYSTAL(1000, "Bought your first unique Z Crystal from the shop!"),
    SOLD_FIRST_POKEMON_MARKET(500, "Sold a Pokemon on the market for the first time!"),
    DEFEATED_FIRST_ELITE_TRAINER(500, "Defeated an Elite Trainer for the first time!"),
    CONCEDE_FIRST_TRAINER_DUEL(1, "Concede to a Trainer for the first time!"),
    DUEL_USE_ZMOVE(1000, "Successfully used your first Z-Move!"),
    DUEL_USE_DYNAMAX(500, "Successfully use a Max Move for the first time!"),
    NICKNAME_FIRST_POKEMON(100, "Nickname one of your Pokemon for the first time!"),
    EQUIP_FIRST_ZCRYSTAL(200, "Equip a Z Crystal for the first time!"),
    WON_FIRST_DUEL_MAX_SIZE(1000, "Won your first PVP duel with a max size team!"),
    REDEEMED_FIRST_POKEMON(100, "Redeemed a Pokemon for the first time!"),
    BOUGHT_FIRST_MEGA(500, "Bought a Mega Evolution for the first time!"),
    BOUGHT_FIRST_FORM(500, "Bought a Pokemon Form for the first time!"),
    REACHED_TOP_1_LEADERBOARD(2000, "Reached 1st Place on the Global Leaderboard!"),
    REACHED_TOP_10_LEADERBOARD(1000, "Reached Top 10 on the Global Leaderboard!"),
    COMPLETED_FIRST_BOUNTY(500, "Completed your first Bounty!"),
    COMPLETED_FIRST_PURSUIT(1000, "Completed your first Pursuit"),
    COMPLETED_FIRST_LEGEND_PURSUIT(20000, "Completed your first Legend size Pursuit!");

    public static final ExecutorService ACHIEVEMENT_THREAD_POOL = Executors.newFixedThreadPool(3);

    public int credits;
    public String desc;
    Achievements(int credits, String desc)
    {
        this.credits = credits;
        this.desc = desc;
    }

    public static void grant(String playerID, Achievements a, MessageReceivedEvent event)
    {
        ACHIEVEMENT_THREAD_POOL.execute(() -> {
            if(CacheHelper.ACHIEVEMENT_CACHE.get(a).contains(playerID)) return;

            PlayerDataQuery p = new PlayerDataQuery(playerID);

            if(!p.getAchievementsList().contains(a.toString()))
            {
                p.addAchievement(a);
                p.changeCredits(a.credits);

                event.getChannel().sendMessage(p.getMention() + ": Unlocked an achievement: \"" + a.desc + "\"").queue();
            }
            else CacheHelper.ACHIEVEMENT_CACHE.get(a).add(playerID);
        });
    }

    public static Achievements asAchievement(String a)
    {
        for(Achievements s : values()) if(s.toString().equalsIgnoreCase(a)) return s;
        return null;
    }
}
