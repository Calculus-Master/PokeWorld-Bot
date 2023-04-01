package com.calculusmaster.pokecord.game.duel.restrictions;

import com.calculusmaster.pokecord.game.duel.restrictions.types.*;
import com.calculusmaster.pokecord.game.enums.elements.Type;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonEntity;

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

        // ------------------------ TIER 1 RESTRICTIONS ------------------------

        //Single Type
        List<TeamRestriction> singleTypeRestrictions = new ArrayList<>();
        for(Type type : Type.values()) singleTypeRestrictions.add(new SingleTypeRestriction(type));
        RESTRICTION_TIERS.get(1).add(singleTypeRestrictions);

        //Specific Generation
        List<TeamRestriction> specificGenerationRestrictions = new ArrayList<>();
        for(int i = 1; i <= 9; i++) specificGenerationRestrictions.add(new SpecificGenerationRestriction(i));
        RESTRICTION_TIERS.get(1).add(specificGenerationRestrictions);

        //Even/Odd Dex Number
        RESTRICTION_TIERS.get(1).add(List.of(new EvenOddDexNumberRestriction(true), new EvenOddDexNumberRestriction(false)));

        //Different Generations
        RESTRICTION_TIERS.get(1).add(List.of(new DifferentGenerationsRestriction()));

        // ------------------------ TIER 2 RESTRICTIONS ------------------------

        //Stat Total (Level 1)
        List<TeamRestriction> statTotalRestrictionsL1 = new ArrayList<>();
        for(int total : List.of(800, 900, 1000)) statTotalRestrictionsL1.add(new TotalStatRestriction(total));
        RESTRICTION_TIERS.get(2).add(statTotalRestrictionsL1);

        //Full Type
        List<TeamRestriction> fullTypeRestrictions = new ArrayList<>();
        for(Type type : Type.values()) fullTypeRestrictions.add(new FullTypeRestriction(type));
        RESTRICTION_TIERS.get(2).add(fullTypeRestrictions);

        //Unique Types
        RESTRICTION_TIERS.get(2).add(List.of(new UniqueTypeRestriction()));

        //Status Move Count (0)
        RESTRICTION_TIERS.get(2).add(List.of(new StatusMovesCountRestriction(0)));

        // ------------------------ TIER 3 RESTRICTIONS ------------------------

        //Stat Total (Level 2)
        List<TeamRestriction> statTotalRestrictionsL2 = new ArrayList<>();
        for(int total : List.of(500, 600, 700)) statTotalRestrictionsL2.add(new TotalStatRestriction(total));
        RESTRICTION_TIERS.get(3).add(statTotalRestrictionsL2);

        //Specific Pokemon
        List<TeamRestriction> specificPokemonRestrictions = new ArrayList<>();
        for(PokemonEntity pokemonEntity : PokemonEntity.values()) specificPokemonRestrictions.add(new ContainsSpecificPokemonRestriction(pokemonEntity));
        RESTRICTION_TIERS.get(3).add(specificPokemonRestrictions);

        //Status Move Count (1, 2)
        List<TeamRestriction> statusMovesRestrictions = new ArrayList<>();
        for(int i = 1; i <= 2; i++) statusMovesRestrictions.add(new StatusMovesCountRestriction(i));
        RESTRICTION_TIERS.get(3).add(statusMovesRestrictions);

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
