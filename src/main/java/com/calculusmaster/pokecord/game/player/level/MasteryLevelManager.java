package com.calculusmaster.pokecord.game.player.level;

import com.calculusmaster.pokecord.Pokeworld;
import com.calculusmaster.pokecord.game.player.level.pmltasks.*;
import com.calculusmaster.pokecord.mongo.PlayerData;
import com.calculusmaster.pokecord.util.enums.Prices;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.ArrayList;
import java.util.List;

import static com.calculusmaster.pokecord.game.enums.elements.Feature.*;

public class MasteryLevelManager
{
    public static boolean ACTIVE;
    public static final List<PokemonMasteryLevel> MASTERY_LEVELS = new ArrayList<>();

    //Limits
    private static int maxLevel() { return MASTERY_LEVELS.get(MASTERY_LEVELS.size() - 1).getLevel(); }

    public static int getMaxMarketListings(int masteryLevel)
    {
        if(!ACTIVE) masteryLevel = maxLevel();

        if(masteryLevel < ACCESS_MARKET.getRequiredLevel()) return 0;
        else return 1 + (masteryLevel - ACCESS_MARKET.getRequiredLevel()) * 2;
    }

    public static int getMaxNurseryPairs(int masteryLevel)
    {
        if(!ACTIVE) masteryLevel = maxLevel();

        if(masteryLevel < BREED_POKEMON.getRequiredLevel()) return 0;
        else return (masteryLevel - BREED_POKEMON.getRequiredLevel()) / 4 + 1;
    }

    //TODO: Names for each Level?
    public static void init()
    {
        //Level 0 – Introduction & Basic Features
        PokemonMasteryLevel
                .create(0)
                .withEmbed(() -> new EmbedBuilder()
                        .setTitle("Pokemon Mastery Level 0 – Welcome to " + Pokeworld.NAME + "!")
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
                                You can view your selected Pokemon's available moves using `/moves view`.\s
                                Some moves will be locked – you need to level up that Pokemon further to unlock them! Additionally, a select few moves may be permanently locked. These are either not going to be implemented, or are currently a work-in-progress!\s
                                To learn a new move, use `/moves learn`. This will prompt you to replace one of the moves your current Pokemon knows using `/moves replace`. There is no penalty or cost for switching out moves, so feel free to experiment and craft that perfect set of moves for your Pokemon!
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
                                There are plenty of commands such as `/pokedex`, `/info`, `/moves info` and more that will show you data regarding a specific aspect of the world of Pokemon.
                                """.formatted(Pokeworld.NAME), false)
                )
                .withFeaturesUnlocked(VIEW_POKEMON_LIST, VIEW_LEVEL, ACCESS_SETTINGS, VIEW_DEX_INFO, VIEW_UNIQUE_INFO, VIEW_PROFILE, CREATE_REPORT, VIEW_BALANCE, VIEW_ABILITY_INFO, VIEW_MOVE_INFO, VIEW_MOVES, VIEW_WORLD_INFO, VIEW_ACHIEVEMENTS, ACCESS_INVENTORY, ACCESS_LEADERBOARD,
                        CATCH_POKEMON, SELECT_POKEMON, EVOLVE_POKEMON, RELEASE_POKEMON, LEARN_REPLACE_MOVES)
                .register();

        PokemonMasteryLevel.create(1)
                .withEmbed(() -> new EmbedBuilder()
                        .setTitle("Pokemon Mastery Level 1 – Introducing: Pokemon Duels")
                        .setDescription("""
                                ***Congratulations on reaching your first Pokemon Mastery Level!***
                                Let's start using some of the Pokemon you've just caught. Pokemon Duels are a core activity you'll come across in your adventure, where you can battle against other players (or wild Pokemon, trainers, and more) with your special team of Pokemon!
                                
                                Duels can be very intricate and complex, especially with carefully crafted teams from both parties, but there's no harm in starting simple!
                                """)
                        .addField("Creating your Pokemon Team", """
                                In most types of Duels, you'll need to create a team of Pokemon to battle with.
                                The `/team` command is your hub for managing your team, with functions such as adding new Pokemon to your team, removing them, clearing your team entirely, swapping the positions of Pokemon on your team, and saving your current team to one of your saved Team slots.
                                Don't worry about needing to know all of these features right now – you'll become more familiar with them as you continue your adventure.
                                
                                To start, your team will be empty. You can view your current owned Pokemon using `/pokemon`.
                                Select a Pokemon (or a few!), and use `/team add <number>`, where <number> is the number of the Pokemon, to add them to your team!
                                """, false)
                        .addField("Initiating Player Duels (PvP)", """
                                The first kind of Duel you've unlocked are the basic Player vs Player Duels. These are duels between two players, where each player has a team of Pokemon.
                                You can use the `/duel` command to duel another player in your server. If you don't specify a size parameter, the duel will automatically start using your and your opponent's selected Pokemon.
                                Otherwise, the duel will start using up to the number of Pokemon that you've specified.
                                The opponent will then need to accept your duel request using `/duel accept`, and then your Duel will start!
                                """, false)
                        .addField("Dueling Basics", """
                                You can use moves in a Duel by using `/use <number>`, and if you don't remember your current Pokemon's moves, use `/moves` to see them.
                                In addition, if your Pokemon faints, or you want to switch to another team member, use `/team` to view your current team, and use `/use swap <number>` to swap to another Pokemon in your team.
                                The Duel ends when all of a player's Pokemon have fainted!
                                
                                There are more advanced functions that you can use within Duels, but you'll unlock them as you progress through your adventure!
                                """, false)
                )
                .withFeaturesUnlocked(CREATE_POKEMON_TEAMS, PVP_DUELS, USE_MOVES)
                .withExperienceRequirement(20)
                .withTaskRequirement(new PokemonCaughtPMLTask(10))
                .register();

        PokemonMasteryLevel.create(2)
                .withEmbed(() -> new EmbedBuilder()
                        .setTitle("Pokemon Mastery Level 2 – Economy Basics")
                        .setDescription("""
                                ***How was your first Duel?***
                                Let's now move towards some basic economy features. Up until now, you haven't really had a chance to spend your credits, the main currency within the bot.
                                Use `/balance` to view how many credits you currently have.
                                
                                There are 3 main economy features within the bot: Trading, the Shop, and the Pokemon Market.
                                Let's dive into each one!
                                """)
                        .addField("Trading", """
                                Trading is a feature that allows you to exchange credits, Pokemon, items, and redeems with other players. Simply use `/trade` to start a Trade with another user!
                                After you've started a Trade, you can use the `/trade add` and `/trade remove` commands to edit your offer. Once you're happy with your side of the offer, as well as what the other player is offering, use `/trade confirm` to confirm the exchange.
                                Once both players have confirmed, the exchange will be completed!
                                """, false)
                        .addField("Shop", """
                                The Shop houses plenty of useful items for managing your Pokemon. Many of these features will be currently locked, and you'll be able to access those pages of the Shop as you reach higher Pokemon Mastery Levels. 
                                For now, the two most basic things you can purchase from the shop are *Rare Candies* and *Natures*.
                                
                                Rare Candies instantly grant a level to your currently selected Pokemon, and you can buy multiple. Be warned, they can be very expensive if you try to buy many at a time!
                                
                                Natures are a special characteristic that every Pokemon has. A Nature will provide a minor stat boost to a particular stat, as well as a minor stat decrease to another Stat. You can view all of the available Natures in the Shop page, and buy a new one for your currently selected Pokemon as you please.
                                """, false)
                        .addField("Pokemon Market", """
                                The Pokemon Market is a player-driven market where you can buy and sell Pokemon.
                                You can view the Pokemon Market using `/market`.
                                
                                If you find a Pokemon you like, you can view more information about it using `/market info <ID>` where <ID> is the market ID of the Pokemon.
                                If you want to buy the Pokemon from the market, use `/market buy <ID>`.
                                
                                If you're interested in selling one of your own Pokemon, use `/market sell <number> <price>` where <number> is the number of the Pokemon you want to sell, and <price> is the price you want to sell it for.
                                
                                Happy exchanging!
                                """, false)
                        .addField("Warning: Credits", """
                                While it may be very tempting to spend loads of credits on the Shop and Pokemon Market, be mindful of how many credits you have!
                                Credits are not too easy to come by, especially at your current level. You'll unlock a more reliable source of them soon, but until then, keep track of your credits.
                                """, false)
                )
                .withFeaturesUnlocked(TRADE, ACCESS_BUY_SHOP, ACCESS_MARKET)
                .withExperienceRequirement(50)
                .withTaskRequirement(new PvPDuelsCompletedPMLTask(3))
                .register();

        PokemonMasteryLevel.create(3)
                .withEmbed(() -> new EmbedBuilder()
                        .setTitle("Pokemon Mastery Level 3 – Basic Wild Pokemon Duels")
                        .setDescription("""
                                ***It's time to introduce a new way to Duel.***
                                Enter: the Wild Pokemon Duel!
                                
                                Wild Pokemon Duels are a way to duel against a random wild Pokemon, using your currently selected Pokemon.
                                They're quick and (mostly) easy, and you can use them to gain experience and farm EVs.
                                
                                To start a Wild Pokemon Duel, use `/wild`. They work just like the PvP Duels you're familiar with, except, because they're not team-based, you can't swap out to another Pokemon.
                                Wild Pokemon Duels end when either Pokemon faints.
                                """)
                        .addField("Pokemon Forms & Mega-Evolution", """
                                It's time to up your game a little. You now have access to the ability to buy alternate forms for your Pokemon (if available), or Mega-Evolve them (if available).
                                Both of these can be purchased from the Shop, and are permanent changes. You can revert to the basic form anytime, and change to the form you purchased free of charge.
                                The `/form` and `/mega` commands will be the way you change between forms and Mega-Evolution!
                                
                                *Note*: Not all Pokemon have forms and/or can Mega-Evolve! You can view if a Pokemon of interest has forms/can Mega-Evolve using `/dex <pokemonName>`.
                                """, false)
                        .addField("Mega-Evolution Charges", """
                                Mega-Evolving Pokemon has a slight associated cost. Pokemon have a maximum number of *Mega Charges*.
                                A Mega Charge is consumed upon completing a Duel with a Mega-Evolved Pokemon.
                                Once a Pokemon reaches 0 Mega Charges, it will not be able to Mega-Evolve until its charges are replenished.
                                Mega Charges are replenished automatically after a set period of time, when consumed.
                                
                                *Note*: Mega Charges will **not** regenerate if the Pokemon is currently Mega-Evolved!
                                You must restore the Pokemon to its original form for Mega Charges to start to regenerate.
                                """, false)
                        .addField("Favorites", """
                                By now you've probably caught a decent amount of Pokemon. And possibly grown closer to some :).
                                You now have the ability to tag a Pokemon as a favorite, using `/favorite add <number>`, or remove one using `/favorite remove <number>`.
                                You can also view all your pokemon tagged as a favorite, using `/favorite`.
                                """, false)
                )
                .withFeaturesUnlocked(PVE_DUELS, CREATE_POKEMON_FAVORITES, ACQUIRE_POKEMON_FORMS, ACQUIRE_POKEMON_MEGA_EVOLUTIONS)
                .withExperienceRequirement(70)
                .withTaskRequirement(new PokemonEvolvedPMLTask(1))
                .withTaskRequirement(new PokemonCaughtPMLTask(15))
                .withTaskRequirement(new ShopPurchasedPMLTask(2))
                .register();

        PokemonMasteryLevel.create(4)
                .withEmbed(() -> new EmbedBuilder()
                        .setTitle("Pokemon Mastery Level 4 – Research Tasks")///TODO: update this for new system
                        .setDescription("""
                                ***How's your credit balance looking?***
                                Who doesn't want more credits? Well, you're probably running a little low on them between the Shop, Pokemon Market, Forms, and Mega Evolutions.
                                
                                Luckily, the %s Research Team has a solution for you!
                                Research Tasks are small, quick objectives that reward you with credits.
                                
                                You can view your tasks with `/tasks`. If you ever see less than the maximum (%s) number of bounties, use `/bounty get` to automatically obtain more!
                                You'll receive a DM when you've completed a bounty. You can collect a completed bounty using `/bounty collect <number>`. And that's it!
                                
                                If you ever receive a Bounty objective that seems too difficult, or you just don't feel like doing it, you can use `/bounty reroll <number>` to reroll the objective.
                                *Note*: This will reduce the credits awarded by that bounty, so be careful!
                                """.formatted(Pokeworld.NAME))
                        .addField("Items", """
                                You've also just unlocked the ability to give your Pokemon items. These can range from anything from evolution items, boosters, potions, and more!
                                Check out the Shop page to see what you can buy! You can use `/give <itemNumber> <pokemonNumber>` to give an item to one of your Pokemon.
                                You can view your items using `/inventory`.
                                """, false)
                )
                .withFeaturesUnlocked(ACCESS_TASKS, GIVE_POKEMON_ITEMS, REDEEM_POKEMON)
                .withExperienceRequirement(100)
                .withTaskRequirement(new ShopPurchasedPMLTask(5))
                .withTaskRequirement(new WildPMLTask(5))
                .register();

        PokemonMasteryLevel.create(5)
                .withEmbed(() -> new EmbedBuilder()
                        .setTitle("Pokemon Mastery Level 5 – Trainer Duels")
                        .setDescription("""
                                ***Let's up your Duel game a little.***
                                You now have the ability to duel against other randomly generated Trainers!
                                
                                Trainers are randomly created every week, and you have the rest of the week to challenge and defeat as many as you can.
                                To start, let's view the trainers available now using `/trainer`.
                                """)
                        .addField("Trainer Classes", """
                                Trainers are divided into multiple classes. Each class is tougher in a way than the previous, by way of the modifiers they have, as well as the general level of their team. More on modifiers later!
                                You can view all of the current trainers and their classes using `/trainer`.
                                There are two main rewards from the Trainer system:
                                - You'll earn a sizable amount of credits for defeating an entire class of Trainers.
                                - You'll also earn decent rewards for defeating all of the weekly Trainers!
                                
                                Some Trainers may be out of your level currently – not to worry! Continue leveling up and perfecting your Pokemon team to take on these trainers!
                                """, false)
                        .addField("Trainer Modifiers", """
                                Most trainers have at least one modifier attached. These are slight changes to the Dueling environment that either enforce some restriction on your team, or provide an interesting twist during the Duel itself.
                                You can view a Trainer's currently active modifiers using `/trainer info <number>`.
                                Modifiers also become more restrictive or imposing as you duel higher class Trainers!
                                """, false)
                )
                .withFeaturesUnlocked(PVE_DUELS_TRAINER, VIEW_TRAINER_INFO)
                .withExperienceRequirement(125)
                .withTaskRequirement(new WildPMLTask(8))
                .withTaskRequirement(new PvPDuelsCompletedPMLTask(6))
                .withTaskRequirement(new ResearchTasksPMLTask(4))
                .withTaskRequirement(new SpecificLevelPokemonPMLTask(3, 20))
                .register();

        PokemonMasteryLevel.create(6)
                .withEmbed(() -> new EmbedBuilder()
                        .setTitle("Pokemon Mastery Level 6 – Advanced Pokemon Moves")
                        .setDescription("""
                                ***It's time to improve your Pokemon's moves!***
                                Now that you've been exposed to 3 kinds of Duels, let's take a first look towards improving your Pokemon.
                                There are many ways to do so, that you'll unlock as you level, but let's start with three basic options.
                                """)
                        .addField("Technical Machines (TMs)", """
                                Technical Machines are items that represent a particular move.
                                You can use them to teach your Pokemon their respective move – however, they are a one-time use.
                                Not all TMs can be used by every Pokemon, use `/pokedex <name>` to see what TMs a particular Pokemon can accept.
                                
                                When you teach your Pokemon a TM, they will hold it in their inventory. If you teach a different TM to the same Pokemon, the original TM will be removed (and that TM's move will be forgotten).
                                You can teach your Pokemon a TM using `/tm teach`, and view your owned TMs using `/inventory`.
                                """, false)
                        .addField("Move Tutors", """
                                Move Tutors are also another way to teach your Pokemon special moves.
                                They are expensive, and if removed from a Pokemon, they must be taught again!
                                You can teach your Pokemon Move Tutor moves in its respective shop page, `/shop movetutor`.
                                """, false)
                        .addField("Dynamaxing", """
                                Finally, let's talk about Dynamaxing.
                                During Duels, you now have the option to Dynamax your Pokemon.
                                This will dramatically increase the size of the Pokemon, increasing its health, as well as give your Pokemon a new set of moves based on the types of the moves they have equipped.
                                You can dynamax during a Duel using `/use dynamax <moveNumber>`!
                                
                                *Note:* Dynamaxing can only be done once per Duel, and only lasts 3 turns! Pick carefully!
                                """, false)
                )
                .withFeaturesUnlocked(ACCESS_TMS, TEACH_TMS, DYNAMAX_POKEMON, PURCHASE_MOVE_TUTOR_MOVES)
                .withExperienceRequirement(180)
                .withTaskRequirement(new CreditsPMLTask(Prices.SHOP_BASE_TM.get()))
                .withTaskRequirement(new TrainerPMLTask(4))
                .withTaskRequirement(new PokemonEvolvedPMLTask(2))
                .register();

        PokemonMasteryLevel.create(7)
                .withEmbed(() -> new EmbedBuilder()
                        .setTitle("Pokemon Mastery Level 7 – Obtaining better quality Pokemon")
                        .setDescription("""
                                ***Now that you've improved your Pokemon's moves, let's improve your Pokemon's stats!***
                                
                                Let's shift our focus towards obtaining better quality Pokemon. Simply put, there are two methods of obtaining Pokemon that end up getting intrinsically higher IVs than Pokemon you'd catch normally.
                                They are: breeding and raids!
                                """)
                        .addField("Pokemon Breeding", """
                                Pokemon breeding is a way to take two of your owned Pokemon, combine them, and breed a new Pokemon (with the same species as the female you choose).
                                The newly bred Pokemon will generally have higher IVs on average!
                                Breeding requires a male and a female Pokemon, and you can view a Pokemon's gender using `/info`.
                                You'll also need to breed Pokemon with the same egg group, which can also be viewed through `/info` or `/dex`.
                                
                                There's a slight catch though – breeding Pokemon will produce Pokemon Eggs, not a baby Pokemon!
                                You'll need to first equip an egg using `/egg equip <number>`.
                                Then, you can hatch the Pokemon Egg by sending messages in Discord. You can view the hatch progress of your eggs using `/eggs`.
                                
                                Also, there are two limits on the breeding system.
                                First, you can only hold a certain number of eggs. Once you're full, you won't be able to breed any more Pokemon until some eggs are hatched.
                                Second, after breeding, the two parents will be unable to breed for a certain amount of time. You'll have to wait to use them again, or use different Pokemon in the meantime for breeding purposes!
                                """, false)
                        .addField("Joining Raids", """
                                Raids are a more action-packed way to obtain higher quality Pokemon.
                                If you've been engaging actively in the catching side of things, you might've seen an ominous message pop up.
                                
                                Raids will occasionally spawn in channels that also spawn Pokemon. When they show up, you can join one using `/raid join`. The raid will automatically begin in a couple minutes after it spawns.
                                If for some reason you'd like to back out (before the Raid begins), use `/raid leave`.
                                If no one joins a raid, it'll despawn, and you'll have to wait for another raid to show up.
                                """, false)
                        .addField("Dueling in Raids", """
                                Raids are duels where each player that joined uses one Pokemon (their selected one), to fight against the tough Raid Boss.
                                Swapping is not possible, but Dynamaxing is allowed!
                                
                                Upon its defeat, the rewards given out to participating players will be based on their contributions to the battle.
                                
                                The MVP (the player who assists the most), will receive the best rewards, and a guaranteed Pokemon of the same species as the Raid Boss.
                                Other players will only have a chance of receiving the Raid Pokemon, higher the more they contributed.
                                """, false)
                )
                .withFeaturesUnlocked(BREED_POKEMON, HATCH_EGGS, PVE_DUELS_RAID)
                .withExperienceRequirement(200)
                .withTaskRequirement(new PokemonCaughtPMLTask(25))
                .withTaskRequirement(new ShopPurchasedPMLTask(10))
                .withTaskRequirement(new PokemonDynamaxedPMLTask(3))
                .register();

        PokemonMasteryLevel.create(8)
                .withEmbed(() -> new EmbedBuilder()
                        .setTitle("Pokemon Mastery Level 8 – Z-Moves")
                        .setDescription("""
                                ***Z-Moves!***
                                
                                Z-Moves are powerful, one-time-use moves, that can be used by all Pokemon, provided you have the proper Z-Crystal for their usage.
                                For example, if you equip the Z-Crystal Normalium-Z, and use a Pokemon that knows a Normal-type move, that Pokemon can use the Normal-type Z-Move, Breakneck Blitz!
                                Z-Moves can be used once per Duel.
                                """)
                        .addField("Obtaining Z-Crystals", """
                                As of right now, you're only able to earn the typed Z-Crystals. There are another class of Z-Crystals that you'll unlock later.
                                Typed Z-Crystals can be earned through Z-Trial duels.
                                
                                To challenge a Z-Trial Pokemon, you must own 50 Pokemon of the type the Z-Trial is for, and have 2000 credits.
                                Once you meet the requirements, you can challenge a Z-Trial Pokemon using `/ztrial`.
                                Z-Trial duels are 1v1 battles against a powerful Z-Trial Pokemon. Good Luck!
                                Once you defeat the Z-Trial Pokemon, the Z-Crystal is yours!
                                
                                Z-Crystals are permanent unlocks, so feel free to equip and unequip them at your leisure!
                                """, false)
                        .addField("Equipping Z-Crystals and Using Z-Moves", """
                                You can view your unlocked Z-Crystals using `/inventory`, and equip them with `/equip <number>`.
                                
                                During a Duel, you can use a Z-Move using `/use zmove <moveNumber>`!
                                Also note that Trainers may have a Z-Move of their own, which they can use during a Duel, so be warned!
                                Choose when you use your Z-Move wisely, as you'll only get one use per Duel.
                                """, false)
                )
                .withFeaturesUnlocked(PVE_DUELS_ZTRIAL, EQUIP_Z_CRYSTALS, USE_Z_MOVES)
                .withExperienceRequirement(210)
                .withTaskRequirement(new SpecificLevelPokemonPMLTask(1, 50))
                .withTaskRequirement(new PokemonBredPMLTask(3))
                .withTaskRequirement(new PokemonEggsHatchedPMLTask(1))
                .register();

        PokemonMasteryLevel.create(9)
                .withEmbed(() -> new EmbedBuilder()
                        .setTitle("Pokemon Mastery Level 9 – Prestige")
                        .setDescription("""
                                ***We're now one step closer to unlocking your Pokemon's full potential!***
                                
                                You now have the ability to prestige your Pokemon.
                                Prestiging will reset a Pokemon's level to 1, remove all selected moves, and remove any items the Pokemon is holding.
                                In return, the Pokemon will receive a permanent stat boost, based on it's Prestige Level.
                                
                                You can see more info regarding prestiging using `/prestige info`, and once your Pokemon is eligible for Prestiging, use `/prestige advance` to prestige it.
                                Lastly, all Pokemon have a maximum prestige level they can reach. Once the Pokemon is that that level, they will not be able to prestige any further.
                                """)
                        .addField("Prestige Requirements", """
                                To prestige a Pokemon,
                                - It must be Level 100
                                - It must not be at its maximum Prestige Level
                                - It will cost %s credits
                                
                                You can view if you're currently able to prestige your selected Pokemon in `/info`.
                                """.formatted(Prices.PRESTIGE.get()), false)
                        .addField("Unique Z-Crystals", """
                                You've also now unlocked the ability to purchase Unique Z-Crystals from the shop.
                                These Z-Crystals are for particular Pokemon, and will convert a special move that Pokemon knows into a unique Z-Move.
                                Have fun collecting them all!
                                """, false)
                )
                .withFeaturesUnlocked(PRESTIGE_POKEMON, PURCHASE_Z_CRYSTALS)
                .withExperienceRequirement(220)
                .withTaskRequirement(new CreditsPMLTask(Prices.SHOP_ZCRYSTAL.get()))
                .withTaskRequirement(new ZCrystalsAcquiredPMLTask(1))
                .register();

        PokemonMasteryLevel.create(10) //TODO: Added Ranked Duels here
                .withEmbed(() -> new EmbedBuilder()
                        .setTitle("Pokemon Mastery Level 10 – Advanced Duel Modes")
                        .setDescription("""
                                ***Time to put your skills to the test!***
                                
                                Now that you've unlocked the ability to improve your Pokemon's moves, obtain better quality Pokemon, use Z-Moves, and prestige your Pokemon,
                                let's put those newfound skills to the test!
                                
                                You've now unlocked the penultimate tier (there's more :), you'll unlock it soon) of dueling, in the form of:
                                - PvE: Elite Trainer Duels (`/elite`)
                                - PvP: Ranked Duels (`/ranked`)
                                """)
                        .addField("Elite Trainer Duels", """
                                Elite Trainer Duels are duels against Trainers, except the Trainers have a very difficult team to battle against!
                                These trainers are also capable of using Z-Moves and Dynamaxing their Pokemon.
                                
                                You must use a team that adheres to the standard Duel restrictions as well, to duel this trainers.
                                Test your skills against Elite Trainers using `/elite`!
                                """, false)
                )
                .withFeaturesUnlocked(PVE_DUELS_ELITE)
                .withExperienceRequirement(240)
                .withTaskRequirement(new ZCrystalsAcquiredPMLTask(2))
                .withTaskRequirement(new PokemonPrestigedPMLTask(1))
                .withTaskRequirement(new SpecificLevelPokemonPMLTask(6, 50))
                .register();

        PokemonMasteryLevel.create(11)
                .withEmbed(() -> new EmbedBuilder()
                        .setTitle("Pokemon Mastery Level 11 – Augmentation")
                        .setDescription("""
                                ***The final step in unlocking your Pokemon's full potential!***
                                
                                Augments are unique modifications that you can equip onto your Pokemon, that change certain aspects of that Pokemon during Duels.
                                Augments can activate during moves, or other effects during Duels!
                                
                                Every Augment costs a certain number of Augment Slots. When you use the `/augments` command, you'll be able to see all your available Augments as well as their costs, and level requirements to be slotted in.
                                Pokemon will unlock augment slots at predetermined levels, and each Pokemon has a maximum number of augment slots they can support.
                                In general, stronger Pokemon will have less Augment slots than weaker ones!
                                
                                Augments need to be earned in various ways, usually by some particular objective. You can see where an augment is obtained using `/augmentinfo <number>`!
                                Once you earn an Augment, it's yours to use freely!
                                """)
                )
                .withFeaturesUnlocked(AUGMENT_POKEMON)
                .withExperienceRequirement(250)
                .withTaskRequirement(new ElitePMLTask(1))
                .withTaskRequirement(new PokemonPrestigedPMLTask(3))
                //TODO: Add task for Credits = Augment Price from shop
                .register();

        PokemonMasteryLevel.create(12) //TODO: Add descriptions once they're implemented
                .withEmbed(() -> new EmbedBuilder()
                        .setTitle("Pokemon Mastery Level 12 – Pinnacle Duel Modes")
                        .setDescription("""
                                
                                """)
                )
                .withFeaturesUnlocked(PVE_DUELS_GAUNTLET)
                .withExperienceRequirement(280)
                .withTaskRequirement(new PvPDuelsCompletedPMLTask(12))
                .withTaskRequirement(new WildPMLTask(16))
                .withTaskRequirement(new TrainerPMLTask(8))
                .withTaskRequirement(new ElitePMLTask(4))
                .register();
    }

    public static boolean isMax(PlayerData p)
    {
        return p.getLevel() == MASTERY_LEVELS.get(MASTERY_LEVELS.size() - 1).getLevel();
    }
}
