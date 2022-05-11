package com.calculusmaster.pokecord.game.pokemon;

import com.calculusmaster.pokecord.game.moves.Move;
import com.calculusmaster.pokecord.game.moves.MoveData;
import com.calculusmaster.pokecord.game.moves.registry.MaxMoveRegistry;
import com.calculusmaster.pokecord.game.moves.registry.MoveTutorRegistry;
import com.calculusmaster.pokecord.game.moves.registry.ZMoveRegistry;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonData;
import com.calculusmaster.pokecord.util.helpers.CSVHelper;
import com.calculusmaster.pokecord.util.helpers.CacheHelper;
import com.calculusmaster.pokecord.util.helpers.DataHelper;
import com.calculusmaster.pokecord.util.helpers.LoggerHelper;
import org.apache.commons.collections4.ListUtils;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class PokemonAI
{
    public static boolean ENABLED;

    //Outermost Map: {Pokemon Name: {Target Name: {Damage Effectiveness Map, or Move : Effectiveness}}
    private static final Map<String, Map<String, Map<String, Float>>> SIMULATED_EFFECTIVENESS = new HashMap<>();

    public static void main(String[] args)
    {
        LoggerHelper.disableMongoLoggers();

        CSVHelper.init();
        PokemonData.init();
        PokemonRarity.init();
        MoveData.init();
        MoveTutorRegistry.init();
        ZMoveRegistry.init();
        MaxMoveRegistry.init();
        DataHelper.createGigantamaxDataMap();

        PokemonAI.init();
        PokemonAI.test();
    }

    public static void init()
    {
        List<List<String>> chunkedPokemonList = ListUtils.partition(PokemonData.POKEMON, 50);

        ExecutorService pool = Executors.newFixedThreadPool(chunkedPokemonList.size());

        for(List<String> chunk : chunkedPokemonList)
        {
            pool.execute(() -> {
                for(String userName : chunk)
                {
                    Map<String, Map<String, Float>> targetEffectiveness = new HashMap<>();

                    for(String targetName : PokemonData.POKEMON)
                    {
                        targetEffectiveness.put(targetName, PokemonAI.simulateTargetEffectiveness(userName, targetName));
                    }

                    SIMULATED_EFFECTIVENESS.put(userName, targetEffectiveness);
                }

                LoggerHelper.info(PokemonAI.class, "PokemonAI Learning Chunk #" + chunkedPokemonList.indexOf(chunk) + " Completed!");
            });
        }

        pool.shutdown();

        try { pool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS); }
        catch (Exception e) { LoggerHelper.reportError(CacheHelper.class, "PokemonAI Init Failed!", e); }
    }

    private static void test()
    {
        for(int i = 0; i < 5; i++)
        {
            Pokemon user = Pokemon.create(PokemonRarity.getSpawn());

            PokemonAI ai = new PokemonAI(user);
            for(int j = 0; j < 5; j++)
            {
                Pokemon target = Pokemon.create(PokemonRarity.getSpawn());

                Move hdm = new Move(ai.getHighestDamageMove(target));

                System.out.printf("User {%s} vs {%s} -> Highest Damage Move: {%s, Power: %s, Type: %s}%n", user.getName(), target.getName(), hdm.getName(), hdm.getPower(), hdm.getType());
            }
        }
    }

    private static Map<String, Float> simulateTargetEffectiveness(String userName, String targetName)
    {
        Pokemon user = Pokemon.create(userName); user.setLevel(100);
        Pokemon target = Pokemon.create(targetName); target.setLevel(100);

        return PokemonAI.simulateMoveDamages(user, target);
    }

    private static Map<String, Float> simulateMoveDamages(Pokemon user, Pokemon target)
    {
        Map<String, Float> damages = new HashMap<>();

        for(String moveName : user.allMoves())
        {
            if(MoveData.hasData(moveName) && Move.isImplemented(moveName))
            {
                Move move = new Move(moveName);
                float totalDamage = 0;
                int trials = 10;

                for(int i = 0; i < trials; i++) totalDamage += move.getDamage(user, target);

                float averageDamage = totalDamage / (float)trials;

                damages.put(moveName, averageDamage);
            }
            else damages.put(moveName, -1.0F);
        }

        return damages;
    }

    //Class
    private final Pokemon pokemon;
    private Map<String, Map<String, Float>> totalSimulatedDamageEffectiveness;

    public PokemonAI(Pokemon pokemon)
    {
        this.pokemon = pokemon;
        this.totalSimulatedDamageEffectiveness = SIMULATED_EFFECTIVENESS.get(pokemon.getName());
    }

    public String getHighestDamageMove(Pokemon target)
    {
        return Optional.of(Collections.max(this.totalSimulatedDamageEffectiveness.get(target.getName()).entrySet(), Map.Entry.comparingByValue()).getKey()).orElse("Tackle");
    }
}
