package com.calculusmaster.pokecord.game.player.level;

import com.calculusmaster.pokecord.Pokecord;
import com.calculusmaster.pokecord.game.player.level.pmltasks.*;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.util.enums.Prices;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.ArrayList;
import java.util.List;

import static com.calculusmaster.pokecord.game.enums.elements.Feature.*;

public class MasteryLevelManager
{
    public static boolean ACTIVE;
    public static final List<PokemonMasteryLevel> MASTERY_LEVELS = new ArrayList<>();

    //TODO: Restructure leveling and ensure it requires usage of features just unlocked
    //TODO: Names for each Level?
    public static void init()
    {
        //Level 0 – Introduction & Basic Features
        PokemonMasteryLevel
                .create(0)
                .withEmbed(() -> new EmbedBuilder()
                        .setTitle("Pokemon Mastery Level 0 – Welcome to " + Pokecord.NAME + "!")
                        .setDescription("""
                                ***Welcome to the world of Pokemon! Let's start with some basic key features you'll be using throughout your journey.***
                                
                                The core loop of the bot is to catch Pokemon, level them up, and battle against other opponents.\s
                                Along the way, you'll discover countless ways to improve, empower, diversify, and customize your Pokemon, and as a result many challenging opponents and interesting battling experiences.
                                But first, you'll need to be acquainted with some key features you'll need to solidify to become a Pokemon Master!
                                
                                New features will be unlocked as you level up your Pokemon Mastery Level. The main way to earn XP for this is to level up your Pokemon! You'll discover new ways to earn PML experience later on, but for now leveling up Pokemon is your primary way to earn experience.
                                For example, you do not have access to battling right now. You'll need to level up Pokemon you catch to earn enough experience to reach that point!
                                You can use `/level` for a more in-depth overview of what tasks you need to complete and your progress towards reaching the next Mastery Rank.
                                
                                **Good luck, Trainer!**
                                """)
                        .addField("Catching and Managing your Pokemon", """
                                **Catching Pokemon**: The most common thing you'll be doing is catching new Pokemon!\s
                                Pokemon will randomly spawn in a specific channel on your server, every so often. You can catch them using `/catch`.\s
                                Make sure you catch and guess the Pokemon's name quickly before another player does!
                                
                                **Viewing Pokemon**: You can see the Pokemon you've acquired using `/pokemon`.\s
                                You can also select a Pokemon to be your active Pokemon, using the `/select` command. A good portion of features within the bot will make use of your selected Pokemon, so always make sure to select a Pokemon you want to interact with.
                                If you're interested in one of your Pokemon's abilities, use `/info` (selected Pokemon) or `/info <number>` (a specific Pokemon from your list) to view its stats, gender, nature, experience, IVs, EVs, and more!
                                """, false)
                        .addField("Releasing Pokemon", """
                                Growing tired of a Pokemon? No longer want 15 Weedles?\s
                                Not to worry – you can release a Pokemon to the wild using `/release <number>`.\s
                                *Note: Anything equipped on or invested into this Pokemon will be lost!*
                                """, false)
                        .addField("Leveling", """
                                Sending messages on Discord will automatically grant your currently selected Pokemon some experience.\s
                                There are other ways to earn Pokemon XP, that you'll discover throughout the rest of your adventure.\s
                                Leveling up Pokemon unlocks new moves and makes them more powerful! The maximum level any Pokemon can be is 100.
                                """, false)
                        .addField("Moves", """
                                You can view your selected Pokemon's available moves using `/moves`.\s
                                Some moves will be locked – you need to level up that Pokemon further to unlock them! Additionally, a select few moves may be permanently locked. These are either not going to be implemented, or are currently a work-in-progress!\s
                                To learn a new move, use `/learn`. This will prompt you to replace one of the moves your current Pokemon knows using `/replace`. There is no penalty or cost for switching out moves, so feel free to experiment and craft that perfect set of moves for your Pokemon!
                                Moves can do a variety of different things in Duels (which you'll unlock soon!).
                                """, false)
                        .addField("Evolution", """
                                Some Pokemon have the ability to evolve into a more powerful Pokemon. You can view if your selected Pokemon can evolve using `/info`.
                                Level-based evolutions will happen automatically as you chat on Discord, when a Pokemon reaches the correct level.
                                Special evolutions that may require items and/or special conditions can be activated using `/evolve`.
                                """, false)
                        .addField("Help & Information", """
                                **Balance**: The primary currency in %s is Credits. You can earn credits in a variety of ways, as you'll discover during your journey.\s
                                You can view your credit balance using `/balance`.
                                
                                **Help**: There's *a lot* of information surrounding Pokemon, their moves, and the features of this bot.\s
                                You can use `/help` if you ever feel lost with a certain command.
                                
                                **Information**: Interested in information regarding a specific Pokemon, ability, move, or more?\s
                                There are plenty of commands such as `/dex`, `/info`, `/moveinfo` and more that will show you data regarding a specific aspect of the world of Pokemon.
                                """.formatted(Pokecord.NAME), false)
                )
                .withFeaturesUnlocked(VIEW_POKEMON_LIST, VIEW_LEVEL, VIEW_TIPS, ACCESS_SETTINGS, VIEW_DEX_INFO, VIEW_UNIQUE_INFO, VIEW_SERVER_INFO, VIEW_PROFILE, CREATE_REPORT, VIEW_HELP, VIEW_BALANCE, VIEW_ABILITY_INFO, VIEW_MOVE_INFO, VIEW_MOVES, VIEW_LOCATION, VIEW_ACHIEVEMENTS, ACCESS_INVENTORY, ACCESS_LEADERBOARD,
                        CATCH_POKEMON, SELECT_POKEMON, EVOLVE_POKEMON, RELEASE_POKEMON, LEARN_REPLACE_MOVES)
                .register();

        PokemonMasteryLevel.create(1)
                .withEmbed(() -> new EmbedBuilder())
                .withFeaturesUnlocked(CREATE_POKEMON_TEAMS, PVP_DUELS, USE_MOVES)
                .withExperienceRequirement(20)
                .withTaskRequirement(new PokemonCaughtPMLTask(10))
                .register();

        PokemonMasteryLevel.create(2)
                .withEmbed(() -> new EmbedBuilder())
                .withFeaturesUnlocked(TRADE, ACCESS_BUY_SHOP, ACCESS_MARKET)
                .withExperienceRequirement(50)
                .withTaskRequirement(new PvPDuelsCompletedPMLTask(3))
                .register();

        PokemonMasteryLevel.create(3)
                .withEmbed(() -> new EmbedBuilder())
                .withFeaturesUnlocked(PVE_DUELS, CREATE_POKEMON_FAVORITES, ACQUIRE_POKEMON_FORMS, ACQUIRE_POKEMON_MEGA_EVOLUTIONS)
                .withExperienceRequirement(70)
                .withTaskRequirement(new PokemonEvolvedPMLTask(1))
                .withTaskRequirement(new PokemonCaughtPMLTask(15))
                .withTaskRequirement(new ShopPurchasedPMLTask(2))
                .register();

        PokemonMasteryLevel.create(4)
                .withEmbed(() -> new EmbedBuilder())
                .withFeaturesUnlocked(ACCESS_BOUNTIES, GIVE_POKEMON_ITEMS, REDEEM_POKEMON)
                .withExperienceRequirement(100)
                .withTaskRequirement(new ShopPurchasedPMLTask(5))
                .withTaskRequirement(new WildPMLTask(5))
                .register();

        PokemonMasteryLevel.create(5)
                .withEmbed(() -> new EmbedBuilder())
                .withFeaturesUnlocked(PVE_DUELS_TRAINER, VIEW_TRAINER_INFO, FLEE_TRAINER_DUELS)
                .withExperienceRequirement(125)
                .withTaskRequirement(new WildPMLTask(8))
                .withTaskRequirement(new PvPDuelsCompletedPMLTask(6))
                .withTaskRequirement(new BountiesPMLTask(4))
                .withTaskRequirement(new SpecificLevelPokemonPMLTask(3, 20))
                .register();

        PokemonMasteryLevel.create(6)
                .withEmbed(() -> new EmbedBuilder())
                .withFeaturesUnlocked(ACCESS_TMS, ACCESS_TRS, TEACH_TMS_TRS, DYNAMAX_POKEMON, PURCHASE_MOVE_TUTOR_MOVES)
                .withExperienceRequirement(180)
                .withTaskRequirement(new CreditsPMLTask(Prices.SHOP_BASE_TM.get()))
                .withTaskRequirement(new TrainerPMLTask(4))
                .withTaskRequirement(new PokemonEvolvedPMLTask(2))
                .register();

        PokemonMasteryLevel.create(7)
                .withEmbed(() -> new EmbedBuilder())
                .withFeaturesUnlocked(BREED_POKEMON, HATCH_EGGS, PVE_DUELS_RAID, ACTIVATE_ITEMS)
                .withExperienceRequirement(200)
                .withTaskRequirement(new PokemonCaughtPMLTask(25))
                .withTaskRequirement(new ShopPurchasedPMLTask(10))
                .withTaskRequirement(new PokemonDynamaxedPMLTask(3))
                .register();

        PokemonMasteryLevel.create(8)
                .withEmbed(() -> new EmbedBuilder())
                .withFeaturesUnlocked(PVE_DUELS_ZTRIAL, EQUIP_Z_CRYSTALS, USE_Z_MOVES)
                .withExperienceRequirement(210)
                .withTaskRequirement(new SpecificLevelPokemonPMLTask(1, 50))
                .withTaskRequirement(new PokemonBredPMLTask(3))
                .withTaskRequirement(new PokemonEggsHatchedPMLTask(1))
                .register();

        PokemonMasteryLevel.create(9)
                .withEmbed(() -> new EmbedBuilder())
                .withFeaturesUnlocked(PRESTIGE_POKEMON, PURCHASE_Z_CRYSTALS)
                .withExperienceRequirement(220)
                .withTaskRequirement(new CreditsPMLTask(Prices.SHOP_ZCRYSTAL.get()))
                .withTaskRequirement(new ZCrystalsAcquiredPMLTask(1))
                .register();

        PokemonMasteryLevel.create(10)
                .withEmbed(() -> new EmbedBuilder())
                .withFeaturesUnlocked(PVE_DUELS_ELITE, PVP_DUELS_TOURNAMENT)
                .withExperienceRequirement(240)
                .withTaskRequirement(new ZCrystalsAcquiredPMLTask(2))
                .withTaskRequirement(new PokemonPrestigedPMLTask(1))
                .withTaskRequirement(new SpecificLevelPokemonPMLTask(6, 50))
                .register();

        PokemonMasteryLevel.create(11)
                .withEmbed(() -> new EmbedBuilder())
                .withFeaturesUnlocked(AUGMENT_POKEMON)
                .withExperienceRequirement(250)
                .withTaskRequirement(new ElitePMLTask(1))
                .withTaskRequirement(new PokemonPrestigedPMLTask(3))
                //TODO: Add task for Credits = Augment Price from shop
                .register();

        PokemonMasteryLevel.create(12)
                .withEmbed(() -> new EmbedBuilder())
                .withFeaturesUnlocked(PVE_DUELS_GAUNTLET)
                .withExperienceRequirement(280)
                .withTaskRequirement(new PvPDuelsCompletedPMLTask(12))
                .withTaskRequirement(new WildPMLTask(16))
                .withTaskRequirement(new TrainerPMLTask(8))
                .withTaskRequirement(new ElitePMLTask(4))
                .register();
    }

    public static boolean isMax(PlayerDataQuery p)
    {
        return p.getLevel() == MASTERY_LEVELS.size();
    }
}
