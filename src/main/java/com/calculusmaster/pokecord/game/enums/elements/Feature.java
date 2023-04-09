package com.calculusmaster.pokecord.game.enums.elements;

import com.calculusmaster.pokecord.game.player.level.MasteryLevelManager;

import java.util.EnumSet;

public enum Feature
{
    //Level 0 – Basic Features (Non-Active)
    VIEW_POKEMON_LIST("View your caught Pokemon"),
    VIEW_LEVEL("View your Pokemon Mastery Level"),
    VIEW_TIPS("View helpful tips for your adventure"),
    ACCESS_SETTINGS("Access your player settings (and if applicable, server settings)"),
    VIEW_DEX_INFO("View your PokeDex, and PokeDex information about specific Pokemon"),
    VIEW_UNIQUE_INFO("View specific information about any of your Pokemon"),
    VIEW_SERVER_INFO("View information about the current server"),
    VIEW_PROFILE("View your player profile"),
    CREATE_REPORT("Submit feature requests and bug reports to help out with bot development"),
    VIEW_HELP("Access the help command to understand other features"),
    VIEW_BALANCE("View your credits balance"),
    VIEW_ABILITY_INFO("View information about any implemented Pokemon ability"),
    VIEW_MOVE_INFO("View information about any Pokemon move"),
    VIEW_MOVES("View your active Pokemon's moveset"),
    VIEW_LOCATION("View the server's current location, which will impact certain features"),
    VIEW_ACHIEVEMENTS("View your achievement progress"),
    ACCESS_INVENTORY("Access your Item inventory"),
    ACCESS_LEADERBOARD("View the Global Leaderboard"),

    //Level 0 – Basic Features (Active)
    CATCH_POKEMON("Catch Pokemon"),
    SELECT_POKEMON("Select an active Pokemon"),
    EVOLVE_POKEMON("Evolve Pokemon if they are eligible"),
    RELEASE_POKEMON("Release unwanted Pokemon back into the wild"),
    LEARN_REPLACE_MOVES("Learn new moves and edit your current Pokemon's moveset"),

    //Level 1 – Introduction to Dueling
    CREATE_POKEMON_TEAMS("Create a team of Pokemon to be used in Duels"),
    PVP_DUELS("Duel other players using your powerful team of Pokemon"),
    USE_MOVES("Use your Pokemon's moves in duels"),

    //Level 2 – Introduction to Economy (Trading, Shops, Market)
    TRADE("Trade with other players"),
    ACCESS_BUY_SHOP("Access the shop to buy rare candies (instant level-up) and natures."),
    ACCESS_MARKET("Access the Pokemon Market where you can buy other players' listed Pokemon or list your own"),

    //Level 3 – Introduction to PvE Duels & More Advanced Battle Mechanics
    PVE_DUELS("Duel Wild Pokemon"),
    CREATE_POKEMON_FAVORITES("Tag Pokemon as favorites"),
    ACQUIRE_POKEMON_FORMS("Change the forms of certain Pokemon"),
    ACQUIRE_POKEMON_MEGA_EVOLUTIONS("Mega-Evolve your Pokemon"),

    //Level 4 – Bounties & Items
    ACCESS_BOUNTIES("Complete short bounties to earn gold and experience"),
    GIVE_POKEMON_ITEMS("Give Items to your Pokemon"),
    REDEEM_POKEMON("Use redeems to obtain any Pokemon"),

    //Level 5 – Trainer Duels
    PVE_DUELS_TRAINER("Duel randomly generated Trainers and earn rewards for defeating them each rotation"),
    VIEW_TRAINER_INFO("View information regarding randomly generated Trainers"),
    FLEE_TRAINER_DUELS("Flee duels against Trainers that aren't going so well"),

    //Level 6 – Advanced Battle Mechanics (TMs, Dynamaxing, Move Tutor)
    ACCESS_TMS("Buy Technical Machines (TMs) that can be used to teach your Pokemon new moves"),
    ACCESS_TRS("Buy Technical Records (TRs) that can be used to teach your Pokemon new moves"),
    TEACH_TMS("Teach TMs to your Pokemon to grant them new moves"),
    DYNAMAX_POKEMON("Dynamax Pokemon in Duels to improve their health and be able to use Max Moves"),
    PURCHASE_MOVE_TUTOR_MOVES("Purchase Move Tutor moves to teach your Pokemon"),

    //Level 7 – Pokemon Breeding & Raids, Advanced Items
    BREED_POKEMON("Breed Pokemon to create new Pokemon with improved stats"),
    HATCH_EGGS("Hatch bred eggs to obtain new and improved Pokemon"),
    PVE_DUELS_RAID("Join other players in a Duel against a Raid Pokemon (these will spawn randomly)"),
    ACTIVATE_ITEMS("Activate certain items that extend beyond Pokemon"),

    //Level 8 – Z-Moves
    PVE_DUELS_ZTRIAL("Duel a Trial Pokemon to obtain typed Z-Crystals"),
    EQUIP_Z_CRYSTALS("Equip an active Z-Crystal to be able to use it in Duels"),
    USE_Z_MOVES("Use powerful Z-Moves in Duels"),

    //Level 9 – Pokemon Prestige & Unique Z-Crystals
    PRESTIGE_POKEMON("Prestige Pokemon to gain permanent boosts"),
    PURCHASE_Z_CRYSTALS("Purchase Unique Z-Crystals from the Shop"),

    //Level 10 – Advanced Dueling TODO: Ranked Duels
    PVE_DUELS_ELITE("Duel Elite Trainers for large rewards"),

    //Level 11 – Pokemon Augments
    AUGMENT_POKEMON("Apply Augments to your Pokemon, customizing their capabilities"),

    //Level 12 – Elite Dueling TODO: Gyms, Tower Duels
    PVE_DUELS_GAUNTLET("Challenge a Gauntlet Duel, a test of how many Pokemon your active Pokemon can defeat for large rewards"),

    ;

    public static final EnumSet<Feature> DISABLED = EnumSet.noneOf(Feature.class);

    private String overview;

    Feature(String overview) { this.overview = overview; }

    public String getOverview()
    {
        return this.overview;
    }

    public int getRequiredLevel()
    {
        return MasteryLevelManager.MASTERY_LEVELS.stream().filter(pml -> pml.getFeatures().contains(this)).findFirst().orElseThrow().getLevel();
    }
}
