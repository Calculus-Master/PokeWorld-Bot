package com.calculusmaster.pokecord.game.pokemon.augments;

import com.calculusmaster.pokecord.util.enums.Prices;

public enum PokemonAugment
{
    //Basic Stat Boost Augments
    HP_BOOST_I      (1, "Health Boost I", "Increases HP by 5%"),
    ATK_BOOST_I     (1, "Attack Boost I", "Increases Attack by 5%"),
    DEF_BOOST_I     (1, "Defense Boost I", "Increases Defense by 5%"),
    SPATK_BOOST_I   (1, "Special Attack Boost I", "Increases Special Attack by 5%"),
    SPDEF_BOOST_I   (1, "Special Defense Boost I", "Increases Special Defense 5%"),
    SPD_BOOST_I     (1, "Speed Boost I", "Increases Speed by 5%"),

    HP_BOOST_II     (1, "Health Boost II", "Increases HP by 10%"),
    ATK_BOOST_II    (1, "Attack Boost II", "Increases Attack by 10%"),
    DEF_BOOST_II    (1, "Defense Boost II", "Increases Defense by 10%"),
    SPATK_BOOST_II  (1, "Special Attack Boost II", "Increases Special Attack by 10%"),
    SPDEF_BOOST_II  (1, "Special Defense Boost II", "Increases Special Defense 10%"),
    SPD_BOOST_II    (1, "Speed Boost II", "Increases Speed by 10%"),

    HP_BOOST_III    (1, "Health Boost III", "Increases HP by 20%"),
    ATK_BOOST_III   (1, "Attack Boost III", "Increases Attack by 20%"),
    DEF_BOOST_III   (1, "Defense Boost III", "Increases Defense by 20%"),
    SPATK_BOOST_III (1, "Special Attack Boost III", "Increases Special Attack by 20%"),
    SPDEF_BOOST_III (1, "Special Defense Boost III", "Increases Special Defense 20%"),
    SPD_BOOST_III   (1, "Speed Boost III", "Increases Speed by 20%"),

    //Somewhat Universal Augments
    SUPERCHARGED(2, "Supercharged", "All moves deal 25% more damage, but increases damage taken by 25%."),
    SUPERFORTIFIED(2, "Superfortified", "Reduces damage taken by 25%, but all moves deal 25% less damage."),

    //Unique Augments - Marshadow
    SPECTRAL_AMPLIFICATION(3, "Spectral Amplification", "Increases the damage of Ghost-type moves by 250%. Every time a Ghost-type move is used, both Attack and Defense are lowered by 2 stages."),
    PHANTOM_TARGETING(1, "Phantom Targeting", "All super effective moves deal 50% more damage."),
    SHADOW_PROPULSION(1, "Shadow Propulsion", "Using Ghost-type moves has a 33% chance to raise Speed by 1 stage."),

    //Unique Augments - Necrozma
    PRISMATIC_CONVERGENCE(2, "Prismatic Convergence", "Supercharges Prismatic Laser, increasing its power by 30%, but lowering Speed by 2 stages on use."),
    RADIANT_PRISMATIC_CONVERGENCE(2, "Radiant Prismatic Convergence", "Supercharges Prismatic Laser, increasing its power by 60%, but lowering Speed by 4 stages on use."),
    LIGHT_ABSORPTION(1, "Light Absorption", "During Harsh Sunlight, reduces damage taken from Special moves by 15%."),
    DIFFRACTED_BEAMS(2, "Diffraction Beam", "50% of the damage dealt by Beam Moves (Charge Beam, Prismatic Laser, Solar Beam, Hyper Beam, Flash Cannon) is distributed across the opponent's team."),
    RADIANT_DIFFRACTED_BEAMS(2, "Radiant Diffraction Beam", "50% of the damage dealt by Beam Moves (Charge Beam, Prismatic Laser, Solar Beam, Hyper Beam, Flash Cannon) is distributed across the opponent's team. The damage dealt to each team member is boosted by an additional 10%."),
    PRISMATIC_MOONLIT_SHIELD(1, "Prismatic Moonlit Shield", "Provides immunity to Dark-type moves. Damage taken from Ghost-type moves is reduced by 75%.")

    ;

    private final int slotCost;
    private final String augmentName;
    private final String description;

    PokemonAugment(int slotCost, String augmentName, String description)
    {
        this.slotCost = slotCost;
        this.augmentName = augmentName;
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

    public String getAugmentID()
    {
        return this.toString();
    }

    public String getAugmentDescription()
    {
        return this.description;
    }

    public int getCreditCost()
    {
        int price = (int)(Prices.UNLOCK_AUGMENT_BASE.get() * Math.pow(1.25, this.getSlotCost() - 1));
        price -= price % 10;
        return price;
    }

    public static PokemonAugment fromID(String augmentID)
    {
        for(PokemonAugment augment : PokemonAugment.values()) if(augment.toString().equalsIgnoreCase(augmentID)) return augment;
        return null;
    }
}
