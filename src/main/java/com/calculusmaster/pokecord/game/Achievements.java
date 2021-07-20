package com.calculusmaster.pokecord.game;

import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.util.helpers.CacheHelper;
import com.calculusmaster.pokecord.util.helpers.ThreadPoolHandler;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public enum Achievements
{
    START_JOURNEY(250, "Started your journey!"),
    CAUGHT_FIRST_POKEMON(500, "Caught your first Pokemon!"),
    BOUGHT_FIRST_POKEMON_MARKET(500, "Bought a Pokemon from the market for the first time!"),
    WON_FIRST_PVP_DUEL(500, "Won your first PvP (Player vs Player) duel!"),
    WON_FIRST_WILD_DUEL(500, "Defeated a Wild Pokemon for the first time!"),
    WON_FIRST_TRAINER_DUEL(500, "Defeated a Trainer for the first time!"),
    DEFEATED_DAILY_TRAINERS(5000, "Defeated all daily Trainers for the first time!"),
    COMPLETED_FIRST_TRADE(500, "Completed your first trade!"),
    SUBMITTED_BUG_REPORT(500, "Submitted a bug report/suggestion! Thank you!"),
    DEFEATED_FIRST_GYM_LEADER(1000, "Defeated your first Gym Leader!"),
    BOUGHT_FIRST_ITEM_SHOP(200, "Bought your first item from the shop!"),
    ACQUIRED_FIRST_TYPED_ZCRYSTAL(1000, "Acquired your first generic Z Crystal!"),
    BOUGHT_FIRST_UNIQUE_ZCRYSTAL(1000, "Bought your first unique Z Crystal from the shop!"),
    SOLD_FIRST_POKEMON_MARKET(500, "Sold a Pokemon on the market for the first time!"),
    DEFEATED_FIRST_ELITE_TRAINER(500, "Defeated an Elite Trainer for the first time!"),
    DUEL_USE_ZMOVE(1000, "Successfully used your first Z-Move!"),
    DUEL_USE_DYNAMAX(500, "Successfully used a Max Move for the first time!"),
    NICKNAME_FIRST_POKEMON(100, "Nicknamed one of your Pokemon for the first time!"),
    EQUIP_FIRST_ZCRYSTAL(200, "Equipped a Z Crystal for the first time!"),
    WON_FIRST_DUEL_MAX_SIZE(1000, "Won your first PvP duel with a max size team!"),
    REDEEMED_FIRST_POKEMON(100, "Redeemed a Pokemon for the first time!"),
    BOUGHT_FIRST_MEGA(500, "Bought a Mega Evolution for the first time!"),
    BOUGHT_FIRST_FORM(500, "Bought a Pokemon Form for the first time!"),
    REACHED_TOP_1_LEADERBOARD(2000, "Reached 1st Place on the Global Leaderboard!"),
    REACHED_TOP_10_LEADERBOARD(1000, "Reached Top 10 on the Global Leaderboard!"),
    COMPLETED_FIRST_BOUNTY(500, "Completed your first Bounty!"),
    COMPLETED_FIRST_PURSUIT(1000, "Completed your first Pursuit"),
    COMPLETED_FIRST_LEGEND_PURSUIT(20000, "Completed your first Legend size Pursuit!"),
    PARTICIPATED_FIRST_TOURNAMENT(500, "Participated in your first Tournament!"),
    WON_FIRST_TOURNAMENT(5000, "Won your first Tournament!"),
    OWNED_10_POKEMON(200, "Owned 10 Pokemon!"),
    OWNED_100_POKEMON(600, "Owned 100 Pokemon!"),
    OWNED_500_POKEMON(1000, "Owned 500 Pokemon!"),
    OWNED_1000_POKEMON(2000, "Owned 1000 Pokemon!"),
    OWNED_5000_POKEMON(3000, "Owned 5000 Pokemon!"),
    OWNED_10000_POKEMON(7500, "Owned 5000 Pokemon!"),
    COMPLETED_ALL_ACHIEVEMENTS(100000, "Completed all Achievements!");

    public int credits;
    public String desc;
    Achievements(int credits, String desc)
    {
        this.credits = credits;
        this.desc = desc;
    }

    public static void grant(String playerID, Achievements a, MessageReceivedEvent event)
    {
        ThreadPoolHandler.ACHIEVEMENT.execute(() -> {
            if(!playerID.chars().allMatch(Character::isDigit)) return;

            if(CacheHelper.ACHIEVEMENT_CACHE.get(a).contains(playerID)) return;

            PlayerDataQuery p = new PlayerDataQuery(playerID);

            if(!p.getAchievementsList().contains(a.toString()))
            {
                p.addAchievement(a);
                p.changeCredits(a.credits);

                event.getChannel().sendMessage(p.getMention() + ": Unlocked an achievement: \"" + a.desc + "\"").queue();
            }
            else CacheHelper.ACHIEVEMENT_CACHE.get(a).add(playerID);

            if(p.getAchievementsList().size() == Achievements.values().length - 1) grant(playerID, Achievements.COMPLETED_ALL_ACHIEVEMENTS, event);
        });
    }

    public static Achievements asAchievement(String a)
    {
        for(Achievements s : values()) if(s.toString().equalsIgnoreCase(a)) return s;
        return null;
    }
}
