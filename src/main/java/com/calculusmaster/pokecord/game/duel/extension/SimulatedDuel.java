package com.calculusmaster.pokecord.game.duel.extension;

import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.enums.elements.Category;
import com.calculusmaster.pokecord.game.moves.Move;
import com.calculusmaster.pokecord.game.moves.MoveData;
import com.calculusmaster.pokecord.game.moves.registry.MaxMoveRegistry;
import com.calculusmaster.pokecord.game.moves.registry.MoveTutorRegistry;
import com.calculusmaster.pokecord.game.moves.registry.ZMoveRegistry;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.pokemon.PokemonRarity;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonData;
import com.calculusmaster.pokecord.util.helpers.CSVHelper;
import com.calculusmaster.pokecord.util.helpers.DataHelper;
import com.calculusmaster.pokecord.util.helpers.LoggerHelper;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//For Machine Learning
public class SimulatedDuel extends Duel
{
    private static void init()
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
    }

    public static void main(String[] args)
    {
        LoggerHelper.init("Simulated Duel", SimulatedDuel::init);

        //Outermost Map: {Pokemon Name: {Target Name: {Damage Effectiveness Map}}
        Map<String, Map<String, Map<String, Float>>> simulatedEffectiveness = new HashMap<>();

        ExecutorService pool = Executors.newFixedThreadPool(20);

        for(String userName : PokemonData.POKEMON)
        {
            simulatedEffectiveness.put(userName, new HashMap<>());

            pool.execute(() -> {
                for(String targetName : PokemonData.POKEMON)
                {
                    Pokemon user = Pokemon.create(userName);
                    Pokemon target = Pokemon.create(targetName);

                    user.setLevel(100);
                    target.setLevel(100);

                    Collections.synchronizedMap(simulatedEffectiveness).get(userName).put(targetName, simulateMoveDamages(user, target));
                }
            });
        }

        StringJoiner j = new StringJoiner("\n");
        Map<String, Map<String, Integer>> highestDamageMoveFrequency = new HashMap<>();

        simulatedEffectiveness.forEach((user, allPokemonDamagesMap) -> allPokemonDamagesMap.forEach((target, damagesMap) -> {
            float max = 0;
            String maxMove = "";

            for (Map.Entry<String, Float> moveDamage : damagesMap.entrySet()) {
                if (moveDamage.getValue() > max) {
                    max = moveDamage.getValue();
                    maxMove = moveDamage.getKey();
                }
            }

            String matchup = user + " versus " + target;
            j.add(matchup + ": Highest Damage Move is " + maxMove + " (Damage: " + max + ")!");

            if(!highestDamageMoveFrequency.containsKey(user)) highestDamageMoveFrequency.put(user, new HashMap<>());
            highestDamageMoveFrequency.get(user).put(maxMove, highestDamageMoveFrequency.get(user).getOrDefault(maxMove, 0) + 1);
        }));

        System.out.println(j);
        //System.out.println(highestDamageMoveFrequency.entrySet().stream().map(e -> e.getKey() + ": " + e.getValue().keySet()).collect(Collectors.joining("\n")));
    }

    private static Map<String, Float> simulateMoveDamages(Pokemon user, Pokemon target)
    {
        List<Move> moves = user.getData().moves.keySet().stream().filter(Move::isImplemented).map(Move::new).filter(m -> m.getPower() > 0 && !m.is(Category.STATUS)).toList();

        Map<String, Float> moveDamages = new HashMap<>();

        for(Move m : moves)
        {
            int damageTotal = 0;
            int trials = 10;

            for(int i = 0; i < trials; i++)
            {
                int damage = m.getDamage(user, target);
                damageTotal += damage;
            }

            float average = damageTotal / (float)(trials);

            moveDamages.put(m.getName(), average);
        }

        //System.out.println(user.getName() + " versus " + target.getName() + ":\n\n" + moveDamages.entrySet().stream().map(e -> e.getKey() + ": " + e.getValue()).collect(Collectors.joining("\n")));
        return moveDamages;
    }
}
