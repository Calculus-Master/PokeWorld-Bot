package com.calculusmaster.pokecord.game.duel.trainer;

import com.calculusmaster.pokecord.game.duel.teamrules.TeamRestrictionRegistry;
import com.calculusmaster.pokecord.game.enums.elements.Type;
import com.calculusmaster.pokecord.game.enums.items.ZCrystal;
import com.calculusmaster.pokecord.game.pokemon.PokemonRarity;
import com.calculusmaster.pokecord.util.Mongo;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import static com.calculusmaster.pokecord.game.pokemon.PokemonRarity.Rarity.*;

public class TrainerManager
{
    public static int REGULAR_TRAINER_INTERVAL;

    public static final List<TrainerData> REGULAR_TRAINERS = new ArrayList<>();
    private static final ScheduledExecutorService TIMER = Executors.newSingleThreadScheduledExecutor();

    private static final Bson TIMER_QUERY = Filters.eq("type", "timer");

    public static void init()
    {
        //No Timer Document
        if(Mongo.TrainerData.find(Filters.eq("type", "timer")).first() == null)
        {
            Mongo.TrainerData.insertOne(new Document("type", "timer").append("time", REGULAR_TRAINER_INTERVAL));

            TrainerManager.createRegularTrainers();
        }

        if(REGULAR_TRAINERS.isEmpty()) Mongo.TrainerData.find(Filters.exists("trainerID")).forEach(d -> REGULAR_TRAINERS.add(new TrainerData(d)));

        TIMER.schedule(TrainerManager::updateTrainers, 0, TimeUnit.HOURS);
    }

    private static void updateTrainers()
    {
        Mongo.TrainerData.updateOne(TIMER_QUERY, Updates.inc("time", -1));

        if(Objects.requireNonNull(Mongo.TrainerData.find(TIMER_QUERY).first()).getInteger("time") <= 0)
        {
            Mongo.TrainerData.updateOne(TIMER_QUERY, Updates.set("time", REGULAR_TRAINER_INTERVAL));

            Mongo.PlayerData.updateMany(Filters.exists("playerID"), Updates.set("defeated_trainers", new ArrayList<>()));

            REGULAR_TRAINERS.clear();

            TrainerManager.createRegularTrainers();
        }
    }

    public static void createRegularTrainers()
    {
        final Random random = new Random();
        final List<String> trainerNames = List.of("Team Rocket Grunt", "Team Aqua Grunt", "Team Magma Grunt", "Team Galactic Grunt", "Team Plasma Grunt", "Team Flare Grunt", "Team Skull Grunt", "Team Yell Grunt");
        final Supplier<String> randomName = () -> trainerNames.get(random.nextInt(trainerNames.size()));

        //Class I Trainers: No Restrictions
        List.of(20, 40, 60).forEach(level -> {
            int teamSize = random.nextInt(3, 6);
            List<String> team = IntStream.range(0, teamSize).mapToObj(i -> PokemonRarity.getSpawnOfRarities(COPPER, SILVER, GOLD, PLATINUM, DIAMOND)).toList();

            REGULAR_TRAINERS.add(new TrainerData(randomName.get(), 1, team, null, level, 1.05F));
        });

        //Class II Trainers: Default Restrictions
        List.of(25, 50, 75, 100).forEach(level -> {
            int teamSize = random.nextInt(5, 7);
            List<String> team = IntStream.range(0, teamSize).mapToObj(i -> PokemonRarity.getSpawnOfRarities(COPPER, SILVER, GOLD, PLATINUM, DIAMOND, MYTHICAL)).toList();

            TrainerData data = new TrainerData(randomName.get(), 2, team, null, level, 1.15F);
            data.addRestriction(TeamRestrictionRegistry.STANDARD);
            REGULAR_TRAINERS.add(data);
        });

        //Class III Trainers: Single Tier 1 Restriction
        List.of(50, 70, 90, 100).forEach(level -> {
            List<String> team = IntStream.range(0, 6).mapToObj(i -> PokemonRarity.getSpawn()).toList();

            TrainerData data = new TrainerData(randomName.get(), 3, team, random.nextFloat() < 0.15F ? ZCrystal.getCrystalOfType(Type.getRandom()) : null, level, 1.25F);
            data.addRestriction(TeamRestrictionRegistry.STANDARD);
            data.addRestriction(TeamRestrictionRegistry.getRandomOfTier(1));
            REGULAR_TRAINERS.add(data);
        });

        //Class IV Trainers: Double Tier 1 Restriction
        List.of(70, 90, 100).forEach(level -> {
            List<String> team = IntStream.range(0, 6).mapToObj(i -> PokemonRarity.getSpawn()).toList();

            TrainerData data = new TrainerData(randomName.get(), 4, team, random.nextFloat() < 0.5F ? ZCrystal.getCrystalOfType(Type.getRandom()) : null, level, 1.35F);
            data.addRestriction(TeamRestrictionRegistry.STANDARD);
            TeamRestrictionRegistry.getRandomOfTier(1, 2).forEach(data::addRestriction);
            REGULAR_TRAINERS.add(data);
        });

        //Class V Trainers: Single Tier 2 Restriction
        List.of(85, 100).forEach(level -> {
            List<String> team = IntStream.range(0, 6).mapToObj(i -> PokemonRarity.getSpawnOfRarities(GOLD, PLATINUM, DIAMOND, MYTHICAL, LEGENDARY, EXTREME)).toList();

            TrainerData data = new TrainerData(randomName.get(), 5, team, ZCrystal.getCrystalOfType(Type.getRandom()), level, 1.4F);
            data.addRestriction(TeamRestrictionRegistry.STANDARD);
            data.addRestriction(TeamRestrictionRegistry.getRandomOfTier(2));
            REGULAR_TRAINERS.add(data);
        });

        //Class VI (Bonus) Trainers: Single Tier 3 Restriction
        List.of(100).forEach(level -> {
            List<String> team = IntStream.range(0, 6).mapToObj(i -> PokemonRarity.getSpawnOfRarities(DIAMOND, MYTHICAL, LEGENDARY, EXTREME)).toList();

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
