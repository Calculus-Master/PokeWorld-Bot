package com.calculusmaster.pokecord.game.player.level;

import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.player.level.leveltasks.*;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;

import java.util.ArrayList;
import java.util.List;

public class MasteryLevelManager
{
    public static final List<PokemonMasteryLevel> MASTERY_LEVELS = new ArrayList<>();

    public static void init()
    {
        // Level 0 – Introduction
        // Catch Pokemon – p!catch, p!pokemon, p!select
        // View Level – p!level
        // Tips – p!tip
        // Server Targets - p!target (this can't be locked behind a PML easily)
        PokemonMasteryLevel.create(0)
                .withFeaturesUnlocked(Feature.CATCH_POKEMON, Feature.SELECT_POKEMON, Feature.VIEW_POKEMON_LIST)
                .register();

        // Level 1 - Essential Features (p!start)
        // Information – p!dex, p!info, p!serverinfo
        // Misc Utilities – p!profile, p!report, p!help, p!settings, p!balance
        PokemonMasteryLevel.create(1)
                .withFeaturesUnlocked(Feature.VIEW_DEX_INFO, Feature.VIEW_UNIQUE_INFO, Feature.VIEW_SERVER_INFO, Feature.VIEW_PROFILE, Feature.CREATE_REPORT, Feature.VIEW_HELP, Feature.ACCESS_SETTINGS, Feature.VIEW_BALANCE)
                .withPokemonRequirement(2)
                .register();

        // Level 2 - Expanded Essential Features (Moves)
        // Pokemon Evolution and Release – p!evolve, p!release
        // Move Information – p!abilityinfo, p!moveinfo
        // Moves – p!moves, p!learn, p!replace
        // Additional Misc – p!location, p!achievements
        PokemonMasteryLevel.create(2)
                .withFeaturesUnlocked(Feature.EVOLVE_POKEMON, Feature.RELEASE_POKEMON, Feature.VIEW_ABILITY_INFO, Feature.VIEW_MOVE_INFO, Feature.VIEW_MOVES, Feature.LEARN_REPLACE_MOVES, Feature.VIEW_LOCATION, Feature.VIEW_ACHIEVEMENTS)
                .withExperienceRequirement(100)
                .withPokemonRequirement(15)
                .withTaskRequirement(new CreditsLevelTask(1500))
                .register();

        // Level 3 - Player Interaction
        // PvP Duels (Limited to 1v1) – p!duel, p!use
        // Trading – p!trade
        PokemonMasteryLevel.create(3)
                .withFeaturesUnlocked(Feature.PVP_DUELS_1V1, Feature.USE_MOVES, Feature.TRADE)
                .withExperienceRequirement(250)
                .withPokemonRequirement(25)
                .withTaskRequirement(new SpecificLevelPokemonLevelTask(10, 20))
                .withTaskRequirement(new CreditsLevelTask(2000))
                .register();

        // Level 4 – Shop & Inventory
        // Shop Interaction – p!shop, p!buy (Nature, Candy)
        // Redeems – p!redeem
        PokemonMasteryLevel.create(4)
                .withFeaturesUnlocked(Feature.ACCESS_BUY_SHOP, Feature.REDEEM_POKEMON)
                .withExperienceRequirement(450)
                .withPokemonRequirement(75)
                .withTaskRequirement(new PvPLevelTask(5))
                .withTaskRequirement(new CreditsLevelTask(2800))
                .register();

        // Level 5 – Expanding Duels (PvP)
        // PvP Duels (Limited to 6v6)
        // Pokemon Teams – p!team
        // Pokemon Favorites – p!favorites
        PokemonMasteryLevel.create(5)
                .withFeaturesUnlocked(Feature.PVP_DUELS_6v6, Feature.CREATE_POKEMON_TEAMS, Feature.CREATE_POKEMON_FAVORITES)
                .withExperienceRequirement(700)
                .withPokemonRequirement(125)
                .withTaskRequirement(new ShopPurchasedLevelTask(5))
                .withTaskRequirement(new SpecificLevelPokemonLevelTask(10, 30))
                .register();

        // Level 6 – Bounties
        // Bounties – p!bounties
        // Pursuits (Limited to Mini/Small/Average) – p!pursuit
        PokemonMasteryLevel.create(6)
                .withFeaturesUnlocked(Feature.ACCESS_BOUNTIES, Feature.ACCESS_PURSUITS_INTRO)
                .withExperienceRequirement(1000)
                .withPokemonRequirement(175)
                .withTaskRequirement(new PvPLevelTask(15))
                .withTaskRequirement(new SpecificLevelPokemonLevelTask(20, 20))
                .register();

        // Level 7 – Expanding Duels (PvE)
        // PvE Duels – p!wild, p!trainer (Daily, not Elite), p!flee
        PokemonMasteryLevel.create(7)
                .withFeaturesUnlocked(Feature.PVE_DUELS, Feature.PVE_DUELS_TRAINER, Feature.FLEE_TRAINER_DUELS)
                .withExperienceRequirement(1500)
                .withPokemonRequirement(225)
                .withTaskRequirement(new BountiesLevelTask(5))
                .withTaskRequirement(new SpecificLevelPokemonLevelTask(5, 40))
                .withTaskRequirement(new PvPLevelTask(20))
                .register();

        // Level 8 – Pokemon Forms
        // Forms – p!forms, p!shop/p!buy forms
        PokemonMasteryLevel.create(8)
                .withFeaturesUnlocked(Feature.ACQUIRE_POKEMON_FORMS)
                .withExperienceRequirement(2000)
                .withPokemonRequirement(300)
                .withTaskRequirement(new WildLevelTask(20))
                .withTaskRequirement(new TrainerLevelTask(5))
                .register();


        // Level 9 – Basic Pokemon Items
        // Items – p!inventory, p!give
        // Shop – p!shop items, p!buy items
        PokemonMasteryLevel.create(9)
                .withFeaturesUnlocked(Feature.ACCESS_INVENTORY, Feature.GIVE_POKEMON_ITEMS)
                .withExperienceRequirement(2500)
                .withPokemonRequirement(350)
                .withTaskRequirement(new CreditsLevelTask(3000))
                .register();

        // Level 10 – Expanding Duels (PvP)
        // PvP Duels (Unlimited)
        // Elite Duels – p!elite
        PokemonMasteryLevel.create(10)
                .withFeaturesUnlocked(Feature.PVP_DUELS_UNLIMITED, Feature.PVE_DUELS_ELITE)
                .withExperienceRequirement(3000)
                .withPokemonRequirement(350)
                .withTaskRequirement(new SpecificLevelPokemonLevelTask(6, 50))
                .register();

        // Level 11 – Mega Evolution
        // Mega – p!mega, p!shop/p!buy mega
        PokemonMasteryLevel.create(11)
                .withFeaturesUnlocked(Feature.ACQUIRE_POKEMON_MEGA_EVOLUTIONS)
                .withExperienceRequirement(3750)
                .withPokemonRequirement(400)
                .withTaskRequirement(new PokemonEvolvedLevelTask(20))
                .register();

        // Level 12 – Advanced Pokemon Items
        // TMs / TRs – p!inventory, p!teach, p!tminfo, p!trinfo
        PokemonMasteryLevel.create(12)
                .withFeaturesUnlocked(Feature.ACCESS_TMS, Feature.ACCESS_TRS, Feature.TEACH_TMS_TRS)
                .withExperienceRequirement(4500)
                .withPokemonRequirement(450)
                .withTaskRequirement(new ShopPurchasedLevelTask(20))
                .register();

        // Level 13 – Market
        // Market – p!market
        PokemonMasteryLevel.create(13)
                .withFeaturesUnlocked(Feature.ACCESS_MARKET)
                .withExperienceRequirement(4500)
                .withPokemonRequirement(475)
                .withTaskRequirement(new CreditsLevelTask(10000))
                .register();

        // Level 14 – Z-Crystals
        // Z-Crystal Access – p!ztrial, p!equip, p!inventory
        // Activate Items – p!activate
        PokemonMasteryLevel.create(14)
                .withFeaturesUnlocked(Feature.PVE_DUELS_ZTRIAL, Feature.PURCHASE_Z_CRYSTALS, Feature.EQUIP_Z_CRYSTALS, Feature.USE_Z_MOVES, Feature.ACTIVATE_ITEMS)
                .withExperienceRequirement(5250)
                .withPokemonRequirement(525)
                .withTaskRequirement(new BountiesLevelTask(30))
                .register();

        // Level 15 – Expanding Duels (PvP & PvE)
        // Raid – p!raid
        // Move Tutor Moves – p!shop movetutor, p!buy movetutor
        // Gym – p!gym TODO: Remove Gym system
        PokemonMasteryLevel.create(15)
                .withFeaturesUnlocked(Feature.PVE_DUELS_RAID, Feature.PVE_DUELS_GYM)
                .withExperienceRequirement(6000)
                .withPokemonRequirement(600)
                .withTaskRequirement(new WildLevelTask(50))
                .register();

        // Level 16 – Breeding
        // Breeding – p!breed
        // Eggs – p!egg
        PokemonMasteryLevel.create(16)
                .withFeaturesUnlocked(Feature.BREED_POKEMON, Feature.HATCH_EGGS)
                .withExperienceRequirement(7000)
                .withPokemonRequirement(700)
                .register();

        // Level 17 – Dynamaxing, Gigantamaxing and Max Moves
        // Dynamaxing (Duels) – p!use d
        // Pursuits (Long/Journey/Legend) – p!pursuit
        PokemonMasteryLevel.create(17)
                .withFeaturesUnlocked(Feature.DYNAMAX_POKEMON, Feature.ACCESS_PURSUITS_FULL)
                .withExperienceRequirement(8000)
                .withPokemonRequirement(800)
                .withTaskRequirement(new SpecificLevelPokemonLevelTask(6, 65))
                .register();

        // Level 18 – Expanding Duels (PvE)
        // Gauntlets – p!gauntlet
        PokemonMasteryLevel.create(18)
                .withFeaturesUnlocked(Feature.PVE_DUELS_GAUNTLET)
                .withExperienceRequirement(8500)
                .withPokemonRequirement(810)
                .withTaskRequirement(new SpecificLevelPokemonLevelTask(1, 100))
                .register();

        // Level 19 – Tournaments
        // Tournaments – p!tournament
        // Leaderboard - p!leaderboard
        PokemonMasteryLevel.create(19)
                .withFeaturesUnlocked(Feature.PVP_DUELS_TOURNAMENT, Feature.ACCESS_LEADERBOARD)
                .withExperienceRequirement(9000)
                .withPokemonRequirement(850)
                .withTaskRequirement(new SpecificLevelPokemonLevelTask(6, 70))
                .register();

        // Level 20 – The Apex
        // TODO: Brainstorm other features for this bot – Pokemon Prestige? Endgame Duel Type?
        PokemonMasteryLevel.create(20)
                .withFeaturesUnlocked()
                .withExperienceRequirement(10000)
                .withPokemonRequirement(1000)
                .withTaskRequirement(new SpecificLevelPokemonLevelTask(6, 100))
                .withTaskRequirement(new CreditsLevelTask(10000))
                .withTaskRequirement(new BountiesLevelTask(100))
                .withTaskRequirement(new PvPLevelTask(50))
                .withTaskRequirement(new EliteLevelTask(10))
                .withTaskRequirement(new ShopPurchasedLevelTask(100))
                .withTaskRequirement(new WildLevelTask(100))
                .withTaskRequirement(new PokemonEvolvedLevelTask(50))
                .register();
    }

    public static boolean isMax(PlayerDataQuery p)
    {
        return p.getLevel() == MASTERY_LEVELS.size();
    }
}
