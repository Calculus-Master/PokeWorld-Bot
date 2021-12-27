package com.calculusmaster.pokecord.game.player.level;

import com.calculusmaster.pokecord.game.player.level.leveltasks.*;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerLevel
{
    public static final Map<Integer, LevelTaskHandler> LEVEL_REQUIREMENTS = new HashMap<>();

    // Level 1 - Essential Features (p!start)
    // Catching and Viewing Pokemon – p!catch, p!pokemon, p!evolve, p!select, p!release
    // Information – p!dex, p!info, p!serverinfo
    // Moves – p!moves, p!learn, p!replace
    // Misc Utilities – p!profile, p!level, p!tip, p!report, p!help, p!settings, p!balance

    // Level 2 - Expanded Essential Features (Moves)
    // Pokemon Evolution and Release – p!evolve, p!release
    // Move Information – p!abilityinfo, p!moveinfo
    // Moves – p!moves, p!learn, p!replace
    // Additional Misc – p!location, p!achievements

    // Level 3 - Player Interaction
    // PvP Duels (Limited to 1v1) – p!duel, p!use
    // Trading – p!trade

    // Level 4 – Shop & Inventory
    // Shop Interaction – p!shop, p!buy
    // Inventory – p!inventory
    // Redeems – p!redeem

    // Level 5 – Expanding Duels (PvP)
    // PvP Duels (Limited to 6v6)
    // Pokemon Teams – p!team
    // Pokemon Favorites – p!favorites

    // Level 6 – Bounties
    // Bounties – p!bounties
    // Server Targets – p!target
    // Pursuits (Limited to Mini/Small/Average) – p!pursuit

    // Level 7 – Expanding Duels (PvE)
    // PvE Duels – p!wild, p!trainer (Daily, not Elite), p!flee

    // Level 8 – Pokemon Forms
    // Forms – p!forms

    // Level 9 – Basic Pokemon Items
    // Items – p!inventory, p!give

    // Level 10 – Expanding Duels (PvP)
    // PvP Duels (Unlimited)
    // Elite Duels – p!trainer elite / p!elite TODO: Make Elite Duels their own thing

    // Level 11 – Mega Evolution
    // Mega – p!mega, p!shop

    // Level 12 – Advanced Pokemon Items
    // TMs / TRs – p!inventory, p!teach, p!tminfo, p!trinfo

    // Level 13 – Market
    // Market – p!market

    // Level 14 – Z-Crystals
    // Z-Crystal Access – p!ztrial, p!teach, p!equip, p!inventory

    // Level 15 – Expanding Duels (PvP & PvE)
    // Raid – p!raid
    // Gym – p!gym TODO: Remove Gym system

    // Level 16 – Breeding
    // Breeding – p!breed
    // Eggs – p!egg

    // Level 17 – Expanding Pursuits
    // Pursuits (Long/Journey/Legend) – p!pursuit

    // Level 18 – Expanding Duels (PvE)
    // Gauntlets – p!gauntlet

    // Level 19 – Tournaments
    // Tournaments – p!tournament

    // Level 20 – The Apex
    // TODO: Brainstorm other features for this bot – Pokemon Prestige? Endgame Duel Type?

    public static final int REQUIRED_LEVEL_TRADE = 2;
    public static final int REQUIRED_LEVEL_MARKET_LIST = 3;
    public static final int REQUIRED_LEVEL_TOURNAMENT_CREATE = 5;
    public static final int REQUIRED_LEVEL_GAUNTLET = 3;
    public static final int REQUIRED_LEVEL_RAID = 3;
    public static final int REQUIRED_LEVEL_ELITE = 3;
    public static final int REQUIRED_LEVEL_MEGA = 2;
    public static final int REQUIRED_LEVEL_FORM = 2;
    public static final int REQUIRED_LEVEL_ACTIVATE = 4;
    public static final int REQUIRED_LEVEL_UNLIMITED_DUEL_SIZE = 6;
    public static final int REQUIRED_LEVEL_BREED = 3;
    public static final int REQUIRED_LEVEL_Z_TRIAL = 4;

    public static void init()
    {
        registerNew(2)
                .setExp(100)
                .add(new PokemonLevelTask(15))
                .add(new CreditsLevelTask(2000))
                .completeRegistry();

        registerNew(3)
                .setExp(250)
                .add(new PokemonLevelTask(50))
                .add(new CreditsLevelTask(4000))
                .add(new PvPLevelTask(1))
                .completeRegistry();

        registerNew(4)
                .setExp(500)
                .add(new PokemonLevelTask(250))
                .add(new CreditsLevelTask(10000))
                .add(new BountiesLevelTask(5))
                .add(new WildLevelTask(5))
                .add(new EliteLevelTask(1))
                .completeRegistry();

        registerNew(5)
                .setExp(1000)
                .add(new PokemonLevelTask(500))
                .add(new CreditsLevelTask(15000))
                .add(new WildLevelTask(25))
                .add(new BountiesLevelTask(15))
                .completeRegistry();

        registerNew(6)
                .setExp(2500)
                .add(new PokemonLevelTask(800))
                .add(new CreditsLevelTask(20000))
                .add(new WildLevelTask(35))
                .add(new BountiesLevelTask(25))
                .completeRegistry();

        registerNew(7)
                .setExp(5000)
                .add(new PokemonLevelTask(1000))
                .add(new CreditsLevelTask(22000))
                .add(new WildLevelTask(45))
                .add(new BountiesLevelTask(30))
                .add(new PvPLevelTask(15))
                .completeRegistry();

        registerNew(8)
                .setExp(7500)
                .add(new PokemonLevelTask(1250))
                .add(new CreditsLevelTask(25000))
                .add(new WildLevelTask(55))
                .add(new BountiesLevelTask(40))
                .add(new PvPLevelTask(20))
                .add(new EliteLevelTask(10))
                .completeRegistry();
    }

    public static boolean canPlayerLevelUp(PlayerDataQuery p)
    {
        return existsNextLevel(p) && LEVEL_REQUIREMENTS.get(p.getLevel() + 1).isPlayerReady(p);
    }

    public static boolean existsNextLevel(PlayerDataQuery p)
    {
        return LEVEL_REQUIREMENTS.containsKey(p.getLevel() + 1);
    }

    private static LevelTaskHandler registerNew(int level)
    {
        return new LevelTaskHandler(level);
    }

    public static class LevelTaskHandler
    {
        private final List<AbstractLevelTask> tasks;
        private final int level;

        private LevelTaskHandler(int level)
        {
            this.tasks = new ArrayList<>();
            this.level = level;
        }

        private boolean isPlayerReady(PlayerDataQuery player)
        {
            return this.tasks.stream().allMatch(task -> task.isCompleted(player));
        }

        private LevelTaskHandler setExp(int exp)
        {
            return this.add(new ExperienceLevelTask(exp));
        }

        private LevelTaskHandler add(AbstractLevelTask task)
        {
            this.tasks.add(task);
            return this;
        }

        public List<AbstractLevelTask> getTasks()
        {
            return this.tasks;
        }

        private void completeRegistry()
        {
            LEVEL_REQUIREMENTS.put(this.level, this);
        }
    }
}
