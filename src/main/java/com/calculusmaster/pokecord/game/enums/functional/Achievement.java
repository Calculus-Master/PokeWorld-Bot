package com.calculusmaster.pokecord.game.enums.functional;

import com.calculusmaster.pokecord.Pokeworld;
import com.calculusmaster.pokecord.game.player.level.PMLExperience;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.util.helpers.CacheHelper;
import com.calculusmaster.pokecord.util.helpers.ThreadPoolHandler;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.Arrays;
import java.util.Collections;
import java.util.function.Supplier;

import static com.calculusmaster.pokecord.game.common.CreditRewards.ACHIEVEMENT_COMPLETE_ALL;
import static com.calculusmaster.pokecord.game.common.CreditRewards.ACHIEVEMENT_TIER_1;

public enum Achievement
{
    //Standard

    START("A New Adventurer", "Begin your journey in " + Pokeworld.NAME + " as a Pokemon Trainer.", ACHIEVEMENT_TIER_1),

    //Extreme
    COMPLETE_POKEDEX("PokeDex Completionist", "Complete the entire PokeDex.", 10_000, true)

    ;

    //Fields
    private final String name;
    private final String description;
    private final int credits;
    private final boolean isExtreme;

    Achievement(String name, String description, int credits, boolean isExtreme)
    {
        this.name = name;
        this.description = description;
        this.credits = credits;
        this.isExtreme = isExtreme;
    }

    Achievement(String name, String description, int credits)
    {
        this(name, description, credits, false);
    }

    public static int getStandardCount()
    {
        return (int)Arrays.stream(Achievement.values()).filter(a -> !a.isExtreme()).count();
    }

    public static int getExtremeCount()
    {
        return (int)Arrays.stream(Achievement.values()).filter(Achievement::isExtreme).count();
    }

    //Grant Achievements
    public void grant(String playerID, Supplier<Boolean> validator, TextChannel channel)
    {
        this.grant(PlayerDataQuery.of(playerID), validator, channel);
    }

    public void grant(PlayerDataQuery playerData, Supplier<Boolean> validator, TextChannel channel)
    {
        ThreadPoolHandler.ACHIEVEMENT.submit(() -> {

            if(Collections.synchronizedMap(CacheHelper.ACHIEVEMENT_CACHE).get(this).contains(playerData.getID())) return;

            if(playerData.hasAchievement(this)) this.addCache(playerData.getID());
            else if(validator.get())
            {
                //Reward
                playerData.addAchievement(this);
                playerData.changeCredits(this.credits);
                playerData.addExp(PMLExperience.ACHIEVEMENT, 100);

                //Player Response
                String text = "*You unlocked an Achievement!* | **%s**: ||%s|| (**+%sc**).".formatted(this.name, this.description, this.credits);

                if(channel == null) playerData.directMessage(text);
                else channel.sendMessage(playerData.getMention() + " " + text).queue();

                //Add to cache
                this.addCache(playerData.getID());

                //Check 100%
                if(playerData.getAchievements().size() == values().length)
                {
                    playerData.changeCredits(ACHIEVEMENT_COMPLETE_ALL);

                    playerData.directMessage("***CONGRATULATIONS!*** *You've completed all %s Achievements!* (**+%sc**).".formatted(Pokeworld.NAME, ACHIEVEMENT_COMPLETE_ALL));
                }
            }
        });
    }

    private void addCache(String playerID)
    {
        Collections.synchronizedMap(CacheHelper.ACHIEVEMENT_CACHE).get(this).add(playerID);
    }

    public String getName()
    {
        return this.name;
    }

    public String getDescription()
    {
        return this.description;
    }

    public boolean isExtreme()
    {
        return this.isExtreme;
    }
}
