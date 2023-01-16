package com.calculusmaster.pokecord.game.duel.teamrules;

import com.calculusmaster.pokecord.game.enums.elements.Type;

import java.util.*;

public class TeamRestrictionRegistry
{
    public static final Map<Integer, List<List<TeamRestriction>>> RESTRICTION_TIERS = new HashMap<>();
    public static final List<TeamRestriction> RESTRICTIONS = new ArrayList<>();

    public static TeamRestriction STANDARD;

    public static void init()
    {
        RESTRICTION_TIERS.put(1, new ArrayList<>());
        RESTRICTION_TIERS.put(2, new ArrayList<>());
        RESTRICTION_TIERS.put(3, new ArrayList<>());

        //Standard Ruleset
        STANDARD = new StandardRestriction();

        //Single Type
        List<TeamRestriction> singleTypeRestrictions = new ArrayList<>();
        for(Type type : Type.values()) singleTypeRestrictions.add(new SingleTypeRestriction(type));
        RESTRICTION_TIERS.get(1).add(singleTypeRestrictions);

        //Full Type
        List<TeamRestriction> fullTypeRestrictions = new ArrayList<>();
        for(Type type : Type.values()) fullTypeRestrictions.add(new FullTypeRestriction(type));
        RESTRICTION_TIERS.get(2).add(fullTypeRestrictions);

        //All Pokemon Mastered
        RESTRICTION_TIERS.get(3).add(List.of(new AllPokemonMasteredRestriction()));
    }

    public static TeamRestriction getRestrictionByID(String restrictionID)
    {
        return RESTRICTIONS.stream().filter(r -> r.getRestrictionID().equals(restrictionID)).findFirst().orElseThrow(() -> new IllegalStateException("Invalid Restriction ID: " + restrictionID));
    }

    public static TeamRestriction getRandomOfTier(int tier)
    {
        List<List<TeamRestriction>> pool = RESTRICTION_TIERS.get(tier);

        List<TeamRestriction> pool2 = pool.get(new Random().nextInt(pool.size()));

        return pool2.get(new Random().nextInt(pool2.size()));
    }

    public static List<TeamRestriction> getRandomOfTier(int tier, int count)
    {
        List<TeamRestriction> out = new ArrayList<>();

        while(out.size() < count)
        {
            TeamRestriction chosen = TeamRestrictionRegistry.getRandomOfTier(tier);
            while(out.contains(chosen)) chosen = TeamRestrictionRegistry.getRandomOfTier(tier);
            out.add(chosen);
        }

        return out;
    }
}
