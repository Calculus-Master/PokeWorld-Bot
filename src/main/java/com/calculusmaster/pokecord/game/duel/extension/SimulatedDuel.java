//package com.calculusmaster.pokecord.game.duel.extension;
//
//import com.calculusmaster.pokecord.game.duel.Duel;
//import com.calculusmaster.pokecord.game.duel.players.Player;
//import com.calculusmaster.pokecord.game.moves.registry.MaxMoveRegistry;
//import com.calculusmaster.pokecord.game.moves.registry.MoveTutorRegistry;
//import com.calculusmaster.pokecord.game.moves.registry.ZMoveRegistry;
//import com.calculusmaster.pokecord.game.pokemon.Pokemon;
//import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
//import com.calculusmaster.pokecord.util.helpers.DataHelper;
//import com.calculusmaster.pokecord.util.helpers.LoggerHelper;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.Random;
//
////For Machine Learning
//public class SimulatedDuel extends Duel
//{
//    //TODO: Heavily WIP (ML for Duels)
//    public static void main(String[] args)
//    {
//        LoggerHelper.disableMongoLoggers();
//        LoggerHelper.init("Pokemon Data", DataHelper::createPokemonData, true);
//        LoggerHelper.init("Pokemon", DataHelper::createPokemonList);
//        LoggerHelper.init("Pokemon", Pokemon::init);
//        LoggerHelper.init("Move Data", DataHelper::createMoveData, true);
//        LoggerHelper.init("Move", DataHelper::createMoveList);
//        LoggerHelper.init("Move Tutor", MoveTutorRegistry::init);
//        LoggerHelper.init("Z-Move", ZMoveRegistry::init);
//        LoggerHelper.init("Max Move", MaxMoveRegistry::init);
//        LoggerHelper.init("Gigantamax", DataHelper::createGigantamaxDataMap);
//        LoggerHelper.init("Gender Rates", DataHelper::createGenderRateMap, true);
//
//        Pokemon n1 = Pokemon.create("Bulbasaur");
//        Pokemon n2 = Pokemon.create("Bulbasaur");
//
//        simulate(5, n1, n2);
//    }
//
//    public SimulatedDuel(Pokemon a, Pokemon b)
//    {
//        Player A = new Player();
//            A.ID = "BOT_A";
//            A.data = new PlayerDataQuery(A.ID)
//            {
//                @Override
//                public String getUsername()
//                {
//                    return "Player A";
//                }
//            };
//            A.team = List.of(a);
//            A.active = a;
//
//            List<String> movePool = new ArrayList<>(List.copyOf(A.active.getAllMoves()));
//            Collections.shuffle(movePool);
//            for(int i = 0; i < 4; i++) A.active.learnMove(movePool.get(i), i + 1);
//
//        Player B = new Player();
//            B.ID = "BOT_B";
//            B.data = new PlayerDataQuery(B.ID)
//            {
//                @Override
//                public String getUsername()
//                {
//                    return "Player B";
//                }
//            };
//            B.team = List.of(b);
//            B.active = b;
//
//            movePool = new ArrayList<>(List.copyOf(B.active.getAllMoves()));
//            Collections.shuffle(movePool);
//            for(int i = 0; i < 4; i++) B.active.learnMove(movePool.get(i), i + 1);
//
//        this.players = new Player[]{A, B};
//        this.setDefaults();
//        this.setDuelPokemonObjects(0);
//        this.setDuelPokemonObjects(1);
//
//        final Random r = new Random();
//
//        while(!this.isComplete())
//        {
//            this.submitMove(A.ID, r.nextInt(4), 'm');
//            this.submitMove(B.ID, r.nextInt(4), 'm');
//
//            this.checkReady();
//        }
//
//        System.out.printf("Simulated Duel Complete! Player A {%s} Player B {%s}", A.active.getName(), B.active.getName());
//    }
//
//    public static void simulate(int times, Pokemon a, Pokemon b)
//    {
//        for(int i = 0; i < times; i++) new SimulatedDuel(a, b);
//    }
//}
