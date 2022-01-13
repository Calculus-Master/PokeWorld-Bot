package com.calculusmaster.pokecord.game.duel.extension;

import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.duel.players.Player;
import com.calculusmaster.pokecord.game.moves.MoveData;
import com.calculusmaster.pokecord.game.moves.registry.MaxMoveRegistry;
import com.calculusmaster.pokecord.game.moves.registry.MoveTutorRegistry;
import com.calculusmaster.pokecord.game.moves.registry.ZMoveRegistry;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonData;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.util.helpers.CSVHelper;
import com.calculusmaster.pokecord.util.helpers.DataHelper;
import com.calculusmaster.pokecord.util.helpers.LoggerHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SplittableRandom;

//For Machine Learning
public class SimulatedDuel extends Duel
{
    private static void init()
    {
        CSVHelper.init();
        PokemonData.init();
        MoveData.init();
        MoveTutorRegistry.init();
        ZMoveRegistry.init();
        MaxMoveRegistry.init();
        DataHelper.createGigantamaxDataMap();
    }

    //TODO: Heavily WIP (ML for Duels)
    public static void main(String[] args)
    {
        LoggerHelper.disableMongoLoggers();
        LoggerHelper.init("Simulated Duel", SimulatedDuel::init);

        Pokemon n1 = Pokemon.create("Bulbasaur");
        Pokemon n2 = Pokemon.create("Bulbasaur");

        new SimulatedDuel(n1, n2).simulate(5);
    }

    public SimulatedDuel(Pokemon a, Pokemon b)
    {
        Player A = this.createSimulatedPlayer(a, "A");
        Player B = this.createSimulatedPlayer(b, "B");

        this.players = new Player[]{A, B};
    }

    private Player createSimulatedPlayer(Pokemon active, String ID)
    {
        Player p = new Player();

        p.ID = "BOT_" + ID;
        p.data = new PlayerDataQuery(p.ID)
        {
            @Override
            public String getUsername()
            {
                return "Player " + ID;
            }
        };
        p.team = List.of(active);
        p.active = active;

        List<String> movePool = new ArrayList<>(List.copyOf(p.active.allMoves()));
        Collections.shuffle(movePool);
        for(int i = 0; i < 4; i++) p.active.learnMove(movePool.get(i), i);

        return p;
    }

    public void simulate()
    {
        this.setDefaults();
        this.setDuelPokemonObjects(0);
        this.setDuelPokemonObjects(1);

        final SplittableRandom r = new SplittableRandom();

        while(!this.isComplete())
        {
            this.submitMove(this.players[0].ID, r.nextInt(4), 'm');
            this.submitMove(this.players[1].ID, r.nextInt(4), 'm');

            this.checkReady();
        }
    }

    public void simulate(int times)
    {
        for(int i = 0; i < times; i++) this.simulate();
    }

    private static class DuelTurnSummary
    {

    }
}
