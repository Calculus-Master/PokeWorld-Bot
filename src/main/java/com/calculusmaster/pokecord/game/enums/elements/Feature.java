package com.calculusmaster.pokecord.game.enums.elements;

import com.calculusmaster.pokecord.game.player.level.MasteryLevelManager;

public enum Feature
{
    //Level 0
    CATCH_POKEMON("Catch Pokemon"),
    VIEW_POKEMON_LIST("View your caught Pokemon"),
    SELECT_POKEMON("Select an active Pokemon"),
    VIEW_LEVEL("View your Pokemon Mastery Level"),
    VIEW_TIPS("View helpful tips for your adventure"),

    //Level 1
    VIEW_DEX_INFO("View Pokedex information about any Pokemon"),
    VIEW_UNIQUE_INFO("View specific information about any of your Pokemon"),
    VIEW_SERVER_INFO("View information about the current server"),
    VIEW_PROFILE("View your player profile"),
    CREATE_REPORT("Submit feature requests and bug reports to help out with bot development"),
    VIEW_HELP("Access the help command to understand other features"),
    ACCESS_SETTINGS("Access your player settings (and if applicable, server settings)"),
    VIEW_BALANCE("View your credits balance"),

    //Level 2
    EVOLVE_POKEMON("Evolve Pokemon if they are eligible"),
    RELEASE_POKEMON("Release unwanted Pokemon back into the wild"),
    VIEW_ABILITY_INFO("View information about any implemented Pokemon ability"),
    VIEW_MOVE_INFO("View information about any Pokemon move"),
    VIEW_MOVES("View your active Pokemon's moveset"),
    LEARN_REPLACE_MOVES("Learn new moves and edit your current Pokemon's moveset"),
    VIEW_LOCATION("View the server's current location, which will impact certain features"),
    VIEW_ACHIEVEMENTS("View your achievement progress"),

    //Level 3
    PVP_DUELS_1V1("Duel other players in 1v1 duels using your active Pokemon"),
    USE_MOVES("Use your Pokemon's moveset in duels"),
    TRADE("Trade with other players"),

    //Level 4
    ACCESS_BUY_SHOP("Access the shop to buy rare candies (instant level-up) and natures."),
    REDEEM_POKEMON("Use redeems to obtain any Pokemon"),

    //Level 5
    PVP_DUELS_6v6("Duel other players using a standard 6-Pokemon team"),
    CREATE_POKEMON_TEAMS("Create a Duel team of Pokemon"),
    CREATE_POKEMON_FAVORITES("Tag Pokemon as favorites"),

    //Level 6
    ACCESS_BOUNTIES("Complete short bounties to earn gold and experience"),
    ACCESS_PURSUITS_INTRO("Complete a pursuit (a series of bounties) to earn individual rewards as well as an overall pursuit completion reward"),

    //Level 7
    PVE_DUELS("Duel Wild Pokemon"),
    PVE_DUELS_TRAINER("Duel randomly generated Trainers and earn rewards for defeating them each day"),
    FLEE_TRAINER_DUELS("Flee duels against Trainers that aren't going so well"),

    //Level 8
    ACQUIRE_POKEMON_FORMS("Change the forms of certain Pokemon"),

    //Level 9
    ACCESS_INVENTORY("Access your Item inventory"),
    GIVE_POKEMON_ITEMS("Give Items to your Pokemon"),

    //Level 10
    PVP_DUELS_UNLIMITED("Duel other players with an unlimited size Pokemon team"),
    PVE_DUELS_ELITE("Duel Elite Trainers for large rewards"),

    //Level 11
    ACQUIRE_POKEMON_MEGA_EVOLUTIONS("Mega-Evolve your Pokemon"),

    //Level 12
    ACCESS_TMS("Buy Technical Machines (TMs) that can be used to teach your Pokemon new moves"),
    ACCESS_TRS("Buy Technical Records (TRs) that can be used to teach your Pokemon new moves"),
    TEACH_TMS_TRS("Teach TMs and TRs to your Pokemon to grant them new moves"),

    //Level 13
    ACCESS_MARKET("Access the Pokemon Market where you can buy other players' listed Pokemon or list your own"),

    //Level 14
    PVE_DUELS_ZTRIAL("Duel a Trial Pokemon to obtain typed Z-Crystals"),
    PURCHASE_Z_CRYSTALS("Purchase Unique Z-Crystals from the Shop"),
    EQUIP_Z_CRYSTALS("Equip an active Z-Crystal to be able to use it in Duels"),
    USE_Z_MOVES("Use powerful Z-Moves in Duels"),
    ACTIVATE_ITEMS("Activate certain items that extend beyond Pokemon"),

    //Level 15
    PVE_DUELS_RAID("Join other players in a Duel against a Raid Pokemon (these will spawn randomly)"),
    PURCHASE_MOVE_TUTOR_MOVES("Purchase Move Tutor moves to teach your Pokemon"),

    //Level 16
    BREED_POKEMON("Breed Pokemon to create new Pokemon with improved stats"),
    HATCH_EGGS("Hatch bred eggs to obtain new and improved Pokemon"),

    //Level 17
    DYNAMAX_POKEMON("Dynamax Pokemon in Duels to improve their health and be able to use Max Moves"),
    ACCESS_PURSUITS_FULL("Start Pursuits of much larger sizes for much larger rewards"),

    //Level 18
    PVE_DUELS_GAUNTLET("Challenge a Gauntlet Duel, a test of how many Pokemon your active Pokemon can defeat for large rewards"),

    //Level 19
    PVP_DUELS_TOURNAMENT("Participate in PvP Tournaments"),
    ACCESS_LEADERBOARD("View the Global Leaderboard"),

    //Level 20
    PRESTIGE_POKEMON("Prestige Pokemon to gain permanent boosts")

    ;

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
