package com.calculusmaster.pokecord.game.duel.trainer;

import com.calculusmaster.pokecord.game.duel.restrictions.TeamRestrictionRegistry;
import com.calculusmaster.pokecord.game.enums.elements.Type;
import com.calculusmaster.pokecord.game.enums.items.ZCrystal;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonEntity;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonRarity;
import com.calculusmaster.pokecord.mongo.Mongo;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.util.helpers.LoggerHelper;
import com.mongodb.client.model.Filters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import static com.calculusmaster.pokecord.game.pokemon.data.PokemonRarity.Rarity.*;

public class TrainerManager
{
    public static final List<TrainerData> REGULAR_TRAINERS = new ArrayList<>();

    public static void init()
    {
        Mongo.TrainerData.find(Filters.exists("trainerID")).forEach(d -> REGULAR_TRAINERS.add(new TrainerData(d)));

        //If the list is empty that means the database didn't have any trainers, so create them
        if(REGULAR_TRAINERS.isEmpty()) TrainerManager.createRegularTrainers();
    }

    public static void createRegularTrainers()
    {
        //Clear existing trainers
        if(!REGULAR_TRAINERS.isEmpty())
        {
            LoggerHelper.info(TrainerManager.class, "Deleting existing Regular Trainers to prepare for new ones.");

            Mongo.TrainerData.deleteMany(Filters.exists("trainerID"));
            REGULAR_TRAINERS.clear();
        }

        LoggerHelper.info(TrainerManager.class, "Creating new Regular Trainers.");

        final Random random = new Random();
        final List<String> trainerNames = List.of("Team Rocket Grunt", "Team Aqua Grunt", "Team Magma Grunt", "Team Galactic Grunt", "Team Plasma Grunt", "Team Flare Grunt", "Team Skull Grunt", "Team Yell Grunt");
        final Supplier<String> randomName = () -> trainerNames.get(random.nextInt(trainerNames.size()));

        //Class I Trainers: No Restrictions
        List.of(20, 40, 60).forEach(level -> {
            int teamSize = random.nextInt(3, 6);
            List<PokemonEntity> team = IntStream.range(0, teamSize).mapToObj(i -> PokemonRarity.getSpawn(true, COPPER, SILVER, GOLD, DIAMOND)).toList();

            REGULAR_TRAINERS.add(new TrainerData(randomName.get(), 1, team, null, level, 1.05F));
        });

        //Class II Trainers: Default Restrictions
        List.of(25, 50, 75, 100).forEach(level -> {
            int teamSize = random.nextInt(5, 7);
            List<PokemonEntity> team = IntStream.range(0, teamSize).mapToObj(i -> PokemonRarity.getSpawn(true, COPPER, SILVER, GOLD, DIAMOND, PLATINUM, MYTHICAL)).toList();

            TrainerData data = new TrainerData(randomName.get(), 2, team, null, level, 1.15F);
            data.addRestriction(TeamRestrictionRegistry.STANDARD);
            REGULAR_TRAINERS.add(data);
        });

        //Class III Trainers: Single Tier 1 Restriction
        List.of(50, 70, 90, 100).forEach(level -> {
            List<PokemonEntity> team = IntStream.range(0, 6).mapToObj(i -> PokemonRarity.getSpawn(true)).toList();

            TrainerData data = new TrainerData(randomName.get(), 3, team, random.nextFloat() < 0.15F ? ZCrystal.getCrystalOfType(Type.getRandom()) : null, level, 1.25F);
            data.addRestriction(TeamRestrictionRegistry.STANDARD);
            data.addRestriction(TeamRestrictionRegistry.getRandomOfTier(1));
            REGULAR_TRAINERS.add(data);
        });

        //Class IV Trainers: Double Tier 1 Restriction
        List.of(70, 90, 100).forEach(level -> {
            List<PokemonEntity> team = IntStream.range(0, 6).mapToObj(i -> PokemonRarity.getPokemon(true)).toList();

            TrainerData data = new TrainerData(randomName.get(), 4, team, random.nextFloat() < 0.5F ? ZCrystal.getCrystalOfType(Type.getRandom()) : null, level, 1.35F);
            data.addRestriction(TeamRestrictionRegistry.STANDARD);
            TeamRestrictionRegistry.getRandomOfTier(1, 2).forEach(data::addRestriction);
            REGULAR_TRAINERS.add(data);
        });

        //Class V Trainers: Single Tier 2 Restriction
        List.of(85, 100).forEach(level -> {
            List<PokemonEntity> team = IntStream.range(0, 6).mapToObj(i -> PokemonRarity.getPokemon(true, GOLD, DIAMOND, PLATINUM, MYTHICAL, ULTRA_BEAST, LEGENDARY)).toList();

            TrainerData data = new TrainerData(randomName.get(), 5, team, ZCrystal.getCrystalOfType(Type.getRandom()), level, 1.4F);
            data.addRestriction(TeamRestrictionRegistry.STANDARD);
            data.addRestriction(TeamRestrictionRegistry.getRandomOfTier(2));
            REGULAR_TRAINERS.add(data);
        });

        //Class VI (Bonus) Trainers: Single Tier 3 Restriction
        List.of(100).forEach(level -> {
            List<PokemonEntity> team = IntStream.range(0, 6).mapToObj(i -> PokemonRarity.getPokemon(true, PLATINUM, MYTHICAL, ULTRA_BEAST, LEGENDARY)).toList();

            TrainerData data = new TrainerData(randomName.get(), 6, team, ZCrystal.getCrystalOfType(Type.getRandom()), level, 1.5F);
            data.addRestriction(TeamRestrictionRegistry.STANDARD);
            data.addRestriction(TeamRestrictionRegistry.getRandomOfTier(3));
            REGULAR_TRAINERS.add(data);
        });

        REGULAR_TRAINERS.forEach(d -> Mongo.TrainerData.insertOne(d.serialize()));
    }

    public static List<TrainerData> getTrainersOfClass(int clazz)
    {
        return REGULAR_TRAINERS.stream().filter(t -> t.getTrainerClass() == clazz).toList();
    }

    public static int getMax()
    {
        return Collections.max(REGULAR_TRAINERS.stream().map(TrainerData::getTrainerClass).toList());
    }

    public static int getPlayerMaxClass(PlayerDataQuery data)
    {
        int clazz = 1;
        for(int i = 1; i < TrainerManager.getMax(); i++)
            if(TrainerManager.getTrainersOfClass(i).stream().anyMatch(d -> data.hasDefeatedTrainer(d.getTrainerID()))) clazz = i;

        return Math.min(clazz + 1, TrainerManager.getMax());
    }

    public static String getRoman(int clazz)
    {
        return switch(clazz) {
            case 1, 2, 3 -> "I".repeat(clazz);
            case 4 -> "IV";
            case 5 -> "V";
            case 6 -> "VI";
            default -> throw new IllegalStateException("Invalid Trainer Class: " + clazz);
        };
    }
}
