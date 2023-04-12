package com.calculusmaster.pokecord.game.pokemon.augments;

import com.calculusmaster.pokecord.util.Global;

import java.util.Arrays;

public enum PokemonAugment
{
    //TODO: Add acquisition sourecs for Augments (first 3 sections are done)
    //TODO: Make acquisition sources more deterministic (currently they are random)

    //Stat Boost Augments
    HP_BOOST            (1, "Health Boost",         "Automatically given upon unlocking the Augment feature.", "Increases HP by 5%."),
    ATK_BOOST           (1, "Attack Boost",         "Automatically given upon unlocking the Augment feature.", "Increases Attack by 5%."),
    DEF_BOOST           (1, "Defense Boost",        "Automatically given upon unlocking the Augment feature.", "Increases Defense by 5%."),
    SPATK_BOOST         (1, "Special Attack Boost", "Automatically given upon unlocking the Augment feature.", "Increases Special Attack by 5%."),
    SPDEF_BOOST         (1, "Special Defense Boost","Automatically given upon unlocking the Augment feature.", "Increases Special Defense by 5%."),
    SPD_BOOST           (1, "Speed Boost",          "Automatically given upon unlocking the Augment feature.", "Increases Speed by 5%."),

    //General Augments
    SUPERCHARGED        (3, "Supercharged",     "Found randomly after defeating Pokemon.", "All moves deal 25% more damage, but increases damage taken by 25%."),
    SUPERFORTIFIED      (3, "Superfortified",   "Found randomly after defeating Pokemon.", "Reduces damage taken by 25%, but all moves deal 25% less damage."),
    HARMONY             (2, "Harmony",          "Found randomly after defeating Pokemon.", "Increases the boost provided by STAB from 25% to 75%."),
    PINNACLE_EVASION    (2, "Pinnacle Evasion", "Found randomly after defeating Pokemon.", "Grants a 5% chance to evade an attack, but Speed is reduced by 10%."),
    PRECISION_STRIKES   (2, "Precision Strikes","Found randomly after defeating Pokemon.", "Increases the chance of a critical hit by 8.3%."),
    PRECISION_BURST     (3, "Precision Burst",  "Found randomly after defeating Pokemon.", "Increases the damage boost provided by a critical hit from 50% to 100%."),
    RAW_FORCE           (3, "Raw Force",        "Found randomly after defeating Pokemon.", "Increases the power of moves by 50%, but removes bonuses from Type Effectiveness, STAB, and Critical Hits."),
    MODIFYING_FORCE     (3, "Modifying Force",  "Found randomly after defeating Pokemon.", "Increases the bonuses provided from Type Effectiveness, STAB, and Critical Hits by 25%, 20%, and 15% respectively, but reduces the power of moves by 40%."),

    //Type Augments
    STANDARDIZATION     (4, "Standardization",  "Found randomly after defeating Pokemon with Normal-type moves.", "Using a Normal-type move has a small chance to remove a negative stat change."),
    SEARING_SHOT        (1, "Searing Shot",     "Found randomly after defeating Pokemon with Fire-type moves.", "All Fire-type moves have an additional 5% chance to burn the target, if the target is not burned by the move itself."),
    DRENCH              (2, "Drench",           "Found randomly after defeating Pokemon with Water-type moves.", "Using Water-type moves has an additional chance to reduce the opponent's Speed by 2 stages, Evasion by 1 stage, and Accuracy by 1 stage."),
    STATIC              (1, "Static",           "Found randomly after defeating Pokemon with Electric-type moves.", "Grants a 10% chance for Electric-type moves to deal partial damage to a random Pokemon from the opponent's team."),
    FLORAL_HEALING      (1, "Floral Healing",   "Found randomly after defeating Pokemon with Grass-type moves.", "While not on the battlefield, grants a 10% chance for the user to heal 15% of their maximum HP each turn."),
    ICY_AURA            (2, "Icy Aura",         "Found randomly after defeating Pokemon with Ice-type moves.", "Ice-type moves have a 20% chance to reduce the target's Speed by 1 stage. During a hailstorm, the chance is increased to 80%."),
    TRUE_STRIKE         (2, "True Strike",      "Found randomly after defeating Pokemon with Fighting-type moves.", "Reduces the power of Physical Fighting-type moves by 30. Successfully using a Physical Fighting move deals an additional 10, 20, or 30 damage as a true strike (directly reducing an opponent's health. Fighting-type moves with less than 50 power do not trigger this effect."),
    POISONOUS_SINGE     (2, "Poisonous Singe",  "Found randomly after defeating Pokemon with Poison-type moves.", "While the user is on the battlefield, damage taken by the opponent due to Poisoned status condition is doubled."),
    GROUNDED_EMPOWERMENT(1, "Grounded Empowerment", "Found randomly after defeating Pokemon with Ground-type moves.", "Increases the power of Ground-type moves by 30% against lighter opponents. If a Ground-type move is used against a heavier opponent, the user's Speed is lowered by 1 stage."),
    AERIAL_EVASION      (1, "Aerial Evasion",   "Found randomly after defeating Pokemon with Flying-type moves.", "Using Flying-type moves increases the user's Evasion by 1 stage. Reduces overall Speed by 10%."),
    SURE_SHOT           (2, "Magical Sure Shot","Found randomly after defeating Pokemon with Psychic-type moves.", "Halves the damage reduction of Psychic-type moves when not effective against the opponent, and increases the damage boost of Psychic-type moves when super effective against opponents by 50%."),
    SWARM_COLLECTIVE    (2, "Swarm Collective", "Found randomly after defeating Pokemon with Bug-type moves.", "Increases the power of Bug-type moves based on the number of Bug-type moves the user knows (+5% each) and the number of Bug-type allies the user has (+10% each, +25% if the ally has this augment equipped)."),
    HEAVYWEIGHT_BASH    (3, "Heavyweight Bash", "Found randomly after defeating Pokemon with Rock-type moves.", "Increases the power of Physical Rock-type moves with base power of at least 80 by 60. Reduces the power of non-Rock type moves by 20."),
    PHASE_SHIFTER       (2, "Phase Shifter",    "Found randomly after defeating Pokemon with Ghost-type moves.", "Using a Ghost-type move has a chance to increase Evasion by 4 stages. Normal-type moves used against the user have a small chance to deal 10 true damage to the opponent."),
    DRACONIC_ENRAGE     (3, "Draconic Enrage",  "Found randomly after defeating Pokemon with Dragon-type moves.", "Increases Attack, Special Attack, and Speed by 15%. Decreases Defense and Special Defense by 15%."),
    UMBRAL_ENHANCEMENTS (1, "Umbral Enhancements","Found randomly after defeating Pokemon with Dark-type moves.", "During nighttime, increases the power of Dark-type moves by 30%. During daytime, decreases the power of Dark-type moves by 10%."),
    PLATED_ARMOR        (1, "Plated Armor",     "Found randomly after defeating Pokemon with Steel-type moves.", "Taking damage from Physical moves increases the user's Defense by 1 stage."),
    FLOWERING_GRACE     (3, "Flowering Grace",  "Found randomly after defeating Pokemon with Fairy-type moves.", "Lowers the power of Special Fairy-type moves by 40. Dealing damage with Special Fairy-type moves heals 40HP, plus an additional 15HP for each Fairy-type move the user and opponent know."),

    // Move Augments
    WEIGHTED_PUNCH      (2, "Weighted Punch", "", "Multiplies the power of Punch moves by the ratio of the user's weight to the opponent's weight."),
    Z_AFFINITY          (5, "Z-Affinity", "", "Doubles the power of Z-Moves."),
    RESTORATIVE_HAIL    (2, "Restorative Hail", "", "Heals the user for 10% of their maximum HP while a Hailstorm is active."),
    RESTORATIVE_SANDSTORM(2, "Restorative Sandstorm", "", "Heals the user for 10% of their maximum HP while a Sandstorm is active."),
    METEOR_SHOWER       (4, "Meteor Shower", "", "The power of Meteor Mash is reduced by 20. Meteor Mash now lowers the opponent's Defense by 2 stages, but no longer increases the user's Attack."),

    //Unique Augments - Victini
    VICTORY_RESOLVE     (3, "Victory Resolve", "", "Defeating an opponent restores HP equal to 50% of the user's remaining HP and cures the user of any Status Conditions."),
    FINAL_RESORT_V      (2, "Final Resort V", "", "If the user's health is below 10%, V-Create's power is doubled. Using V-Create will lower the user's Defense and Special Defense by 6 stages."),
    V_RUSH              (1, "V Rush", "", "Missing an attack increases the user's Attack and Special Attack by 1 stage."),
    SHINING_STAR        (2, "Shining Star", "", "The Victory Star ability now increases Accuracy by 30% instead of 10%."),
    VICTORY_ENSURED     (4, "Victory Ensured", "", "When the user faints, the opponent takes damage equal to half of their remaining health."),

    //Unique Augments - Marshadow
    SPECTRAL_AMPLIFICATION  (3, "Spectral Amplification", "", "Increases the damage of Ghost-type moves by 250%. Every time a Ghost-type move is used, both Attack and Defense are lowered by 2 stages."),
    PHANTOM_TARGETING       (1, "Phantom Targeting", "", "All super effective moves deal 50% more damage."),
    SHADOW_PROPULSION       (1, "Shadow Propulsion", "", "Using Ghost-type moves has a 33% chance to raise Speed by 1 stage."),
    SPECTRAL_SUPERCHARGE    (3, "Spectral Supercharge", "", "Supercharges the Spectral Thief move, increasing its power by 50%. Converts all Fighting-type moves to Ghost-type moves."),

    //Unique Augments - Necrozma
    PRISMATIC_CONVERGENCE           (2, "Prismatic Convergence", "", "Supercharges Prismatic Laser, increasing its power by 30%, but lowering Speed by 2 stages on use."),
    RADIANT_PRISMATIC_CONVERGENCE   (2, "Radiant Prismatic Convergence", "", "Supercharges Prismatic Laser, increasing its power by 60%, but lowering Speed by 4 stages on use."),
    LIGHT_ABSORPTION                (1, "Light Absorption", "", "During Harsh Sunlight, reduces damage taken from Special moves by 15%."),
    DIFFRACTED_BEAMS                (2, "Diffraction Beam", "", "50% of the damage dealt by Beam Moves (Charge Beam, Prismatic Laser, Solar Beam, Hyper Beam, Flash Cannon) is distributed across the opponent's team."),
    RADIANT_DIFFRACTED_BEAMS        (2, "Radiant Diffraction Beam", "", "50% of the damage dealt by Beam Moves (Charge Beam, Prismatic Laser, Solar Beam, Hyper Beam, Flash Cannon) is distributed across the opponent's team. The damage dealt to each team member is boosted by an additional 10%."),
    PRISMATIC_MOONLIT_SHIELD        (1, "Prismatic Moonlit Shield", "", "Provides immunity to Dark-type moves. Damage taken from Ghost-type moves is reduced by 75%."),

    //Unique Augments - Regieleki
    ELECTRIFIED_HYPER_SPEED     (2, "Electrified Hyper Speed", "", "Increases Speed by 100%."),

    ;

    private final int slotCost;
    private final String augmentName;
    private final String source;
    private final String description;

    PokemonAugment(int slotCost, String augmentName, String source, String description)
    {
        this.slotCost = slotCost;
        this.augmentName = augmentName;
        this.source = source;
        this.description = description;
    }

    public int getSlotCost()
    {
        return this.slotCost;
    }

    public String getAugmentName()
    {
        return this.augmentName;
    }

    public String getSource()
    {
        return this.source.isEmpty() ? "SOURCE NOT FOUND" : this.source;
    }

    public String getAugmentID()
    {
        return this.toString();
    }

    public String getAugmentDescription()
    {
        return this.description;
    }

    public static PokemonAugment cast(String input)
    {
        PokemonAugment augment = Global.getEnumFromString(values(), input);
        if(augment == null) augment = Arrays.stream(values()).filter(a -> a.augmentName.equalsIgnoreCase(input)).findFirst().orElse(null);
        return augment;
    }
}
