package com.calculusmaster.pokecord.game.world;

import com.calculusmaster.pokecord.game.objectives.ObjectiveType;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonRarity;
import com.calculusmaster.pokecord.game.pokemon.evolution.FormRegistry;
import com.calculusmaster.pokecord.game.pokemon.evolution.PokemonEgg;
import com.calculusmaster.pokecord.mongo.Mongo;
import com.calculusmaster.pokecord.mongo.PlayerData;
import com.calculusmaster.pokecord.util.Global;
import com.calculusmaster.pokecord.util.enums.StatisticType;
import com.calculusmaster.pokecord.util.helpers.IDHelper;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class PokeWorldNursery
{
    private static final int COOLDOWN_TIME = 2 * 60 * 60; //TODO: Dynamic Cooldown time?

    private static final ExecutorService UPDATER = Executors.newThreadPerTaskExecutor(Thread.ofVirtual().name("Breeding-", 0).factory());
    private static final ScheduledExecutorService TICKER = Executors.newSingleThreadScheduledExecutor();

    public static final Map<String, AtomicInteger> COOLDOWNS = Collections.synchronizedMap(new HashMap<>()); //Key is UUID
    public static final Map<String, BreedingPair> BREEDING_PAIRS = Collections.synchronizedMap(new HashMap<>()); //Key is pair ID

    public static final Map<String, List<String>> PLAYER_BREEDING_PAIRS = Collections.synchronizedMap(new HashMap<>());

    public static void init()
    {
        //Cooldowns
        Document cooldowns = Mongo.BreedingData.find(Filters.eq("type", "cooldowns")).first();

        if(cooldowns == null) Mongo.BreedingData.insertOne(new Document("type", "cooldowns"));
        else cooldowns.forEach((pokemonID, timeObject) -> { if(timeObject instanceof Integer i) COOLDOWNS.put(pokemonID, new AtomicInteger(i)); });

        TICKER.scheduleAtFixedRate(PokeWorldNursery::tick, 0, 1, TimeUnit.SECONDS);

        //Breeding Pairs
        Mongo.BreedingData.find(Filters.exists("pairID")).forEach(document -> new BreedingPair(document).register());
    }

    private static void tick() //TODO: Global tick method, or somehow integrate into RotationManager
    {
        //Cooldowns
        Set<String> cooldownRemovals = new HashSet<>();
        COOLDOWNS.forEach((pokemonID, cooldown) ->
        {
            if(cooldown.decrementAndGet() <= 0) cooldownRemovals.add(pokemonID);

            UPDATER.submit(() -> Mongo.BreedingData.updateOne(Filters.eq("type", "cooldowns"), Updates.set(pokemonID, cooldown.get())));
        });
        cooldownRemovals.forEach(PokeWorldNursery::removeCooldown);

        //Breeding Pairs
        Set<BreedingPair> pairRemovals = new HashSet<>();
        BREEDING_PAIRS.forEach((pairID, pair) ->
        {
            pair.tick();

            if(pair.isComplete()) pairRemovals.add(pair);

            UPDATER.submit(() -> Mongo.BreedingData.updateOne(Filters.eq("pairID", pairID), Updates.set("time", pair.getTime())));
        });
        pairRemovals.forEach(PokeWorldNursery::removeBreedingPair);
    }

    private static void removeCooldown(String UUID)
    {
        COOLDOWNS.remove(UUID);
        UPDATER.submit(() -> Mongo.BreedingData.updateOne(Filters.eq("type", "cooldowns"), Updates.unset(UUID)));
    }

    private static void addCooldown(String UUID)
    {
        COOLDOWNS.put(UUID, new AtomicInteger(COOLDOWN_TIME));
    }

    public static String getCooldownFormatted(String UUID)
    {
        long time = Global.timeNowEpoch() + COOLDOWNS.get(UUID).get();

        return "<t:" + time + ":R> (<t:" + time + ":f>)";
    }

    public static List<BreedingPair> getBreedingPairs(String playerID)
    {
        return PLAYER_BREEDING_PAIRS.getOrDefault(playerID, new ArrayList<>()).stream().map(BREEDING_PAIRS::get).toList();
    }

    public static void removeBreedingPair(BreedingPair pair)
    {
        BREEDING_PAIRS.remove(pair.pairID);
        PLAYER_BREEDING_PAIRS.get(pair.playerID).remove(pair.pairID);

        PokemonEgg egg = PokemonEgg.create(pair.pokemon1, pair.pokemon2);
        UPDATER.submit(egg::upload);

        PokeWorldNursery.addCooldown(pair.pokemon1.getUUID());
        PokeWorldNursery.addCooldown(pair.pokemon2.getUUID());

        UPDATER.submit(() ->
        {
            PlayerData playerData = PlayerData.build(pair.playerID);

            playerData.updateObjective(ObjectiveType.BREED_POKEMON, 1);
            playerData.getStatistics().increase(StatisticType.POKEMON_BRED);

            playerData.addEgg(egg.getEggID());

            playerData.addPokemon(pair.pokemon1.getUUID());
            playerData.addPokemon(pair.pokemon2.getUUID());

            playerData.directMessage("Your Pokemon have finished breeding! You received an egg that will hatch into a **" + egg.getTarget().getName() + "**!");
        });

        UPDATER.submit(() -> Mongo.BreedingData.deleteOne(Filters.eq("pairID", pair.pairID)));
    }

    public static void addBreedingPair(String playerID, Pokemon p1, Pokemon p2)
    {
        Function<PokemonRarity.Rarity, Integer> timeFunction = r -> (int)(switch(r)
        {
            case COPPER -> 15 * 60;
            case SILVER -> 20 * 60;
            case GOLD -> 25 * 60;
            case DIAMOND -> 40 * 60;
            case PLATINUM -> 60 * 60;
            case MYTHICAL, ULTRA_BEAST -> 90 * 60;
            case LEGENDARY -> 120 * 60;
        });

        if(!p1.getEntity().equals(p2.getEntity())
                || (FormRegistry.hasFormData(p1.getEntity()) && FormRegistry.getFormData(p1.getEntity()).getForms().contains(p2.getEntity())))
        {
            final Function<PokemonRarity.Rarity, Integer> tf = timeFunction;
            timeFunction = r -> tf.apply(r) + new Random().nextInt(5, 11) * 60;
        }

        int time = Math.max(timeFunction.apply(p1.getRarity()), timeFunction.apply(p2.getRarity()));

        BreedingPair p = new BreedingPair(playerID, p1, p2, time);
        p.register();

        UPDATER.submit(() -> Mongo.BreedingData.insertOne(p.serialize()));

        UPDATER.submit(() ->
        {
            PlayerData playerData = PlayerData.build(playerID);
            playerData.removePokemon(p1.getUUID());
            playerData.removePokemon(p2.getUUID());
        });
    }

    public static boolean isOnCooldown(Pokemon p)
    {
        return COOLDOWNS.containsKey(p.getUUID());
    }

    public static class BreedingPair
    {
        private final String pairID;
        private final String playerID;
        private final Pokemon pokemon1, pokemon2;
        private int time;

        BreedingPair(String playerID, Pokemon p1, Pokemon p2, int time)
        {
            this.pairID = IDHelper.numeric(8);
            this.playerID = playerID;
            this.pokemon1 = p1;
            this.pokemon2 = p2;
            this.time = time;
        }

        BreedingPair(Document data)
        {
            this.pairID = data.getString("pairID");
            this.playerID = data.getString("playerID");
            this.pokemon1 = Pokemon.build(data.getString("pokemon1"));
            this.pokemon2 = Pokemon.build(data.getString("pokemon2"));
            this.time = data.getInteger("time");
        }

        void register()
        {
            BREEDING_PAIRS.put(this.pairID, this);

            if(!PLAYER_BREEDING_PAIRS.containsKey(this.playerID)) PLAYER_BREEDING_PAIRS.put(this.playerID, new ArrayList<>());
            PLAYER_BREEDING_PAIRS.get(this.playerID).add(this.pairID);
        }

        void tick()
        {
            if(this.time > 0) this.time--;
        }

        boolean isComplete()
        {
            return this.time <= 0;
        }

        Document serialize()
        {
            return new Document()
                    .append("pairID", this.pairID)
                    .append("playerID", this.playerID)
                    .append("pokemon1", this.pokemon1.getUUID())
                    .append("pokemon2", this.pokemon2.getUUID())
                    .append("time", this.time);
        }

        public int getTime()
        {
            return this.time;
        }

        public Pokemon getPokemon1()
        {
            return this.pokemon1;
        }

        public Pokemon getPokemon2()
        {
            return this.pokemon2;
        }
    }
}
