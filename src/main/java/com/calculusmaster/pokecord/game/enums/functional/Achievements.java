package com.calculusmaster.pokecord.game.enums.functional;

import com.calculusmaster.pokecord.game.player.level.PMLExperience;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.util.helpers.CacheHelper;
import com.calculusmaster.pokecord.util.helpers.ThreadPoolHandler;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
    GAUNTLET_FIRST_COMPLETED(200, "Completed your first Gauntlet!"),
    GAUNTLET_FIRST_REACHED_LEVEL_3(500, "Reached Level 3 in a Gauntlet for the first time!"),
    GAUNTLET_FIRST_REACHED_LEVEL_5(1000, "Reached Level 5 in a Gauntlet for the first time!"),
    GAUNTLET_FIRST_REACHED_LEVEL_7(1500, "Reached Level 7 in a Gauntlet for the first time!"),
    GAUNTLET_FIRST_REACHED_LEVEL_10(2000, "Reached Level 10 in a Gauntlet for the first time!"),
    BRED_FIRST_POKEMON(500, "Successfully bred your first Pokemon!"),
    BRED_FIRST_DITTO(750, "Successfully bred a Pokemon with Ditto!"),
    BRED_FIRST_UNKNOWN(1000, "Successfully bred an Unknown Gender Pokemon!"),
    HATCHED_FIRST_EGG(500, "Hatched your first egg!"),
    HATCHED_FIRST_DECENT_IV(1000, "Successfully hatched an egg with decent IVs!"),
    HATCHED_FIRST_GREAT_IV(1500, "Successfully hatched an egg with great IVs!"),
    HATCHED_FIRST_EXCELLENT_IV(2000, "Successfully hatched an egg with excellent IVs!"),
    HATCHED_FIRST_NEARLY_PERFECT_IV(5000, "Successfully hatched an egg with nearly perfect IVs!"),
    COMPLETED_FIRST_RAID(400, "Completed your first Raid!"),
    WON_FIRST_RAID(800, "Won your first Raid!"),
    WON_FIRST_RAID_HIGHEST_DAMAGE(1200, "Won your first Raid and dealt the most damage!"),
    REACHED_COLLECTION_MILESTONE_10(500, "Reached a Collection Milestone of 10 for any Pokemon!"),
    REACHED_COLLECTION_MILESTONE_20(1500, "Reached a Collection Milestone of 20 for any Pokemon!"),
    REACHED_COLLECTION_MILESTONE_50(7000, "Reached a Collection Milestone of 50 for any Pokemon!"),
    REACH_MASTERY_LEVEL_20(50000, "Reached Pokemon Mastery Level 20!"),
    COMPLETED_ALL_ACHIEVEMENTS(1000000, "Completed all Achievements!");

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

            PlayerDataQuery p = PlayerDataQuery.of(playerID);

            if(!p.getAchievementsList().contains(a.toString()))
            {
                p.addAchievement(a);
                p.changeCredits(a.credits);

                p.addExp(PMLExperience.ACHIEVEMENT, 100);

                String message = "You unlocked an Achievement!\n`\"%s\"`\n*You earned %sc.*".formatted(a.desc, a.credits);

                Executors.newSingleThreadScheduledExecutor().schedule(() -> {
                    if(event != null) event.getChannel().sendMessage(p.getMention() + "\n" + message).queue();
                    else p.directMessage(message);
                }, 15, TimeUnit.SECONDS);
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
