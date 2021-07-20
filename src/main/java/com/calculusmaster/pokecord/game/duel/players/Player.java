package com.calculusmaster.pokecord.game.duel.players;

import com.calculusmaster.pokecord.game.moves.Move;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Player
{
    public String ID;
    public PlayerDataQuery data;
    public Pokemon active;
    public Move move;
    public boolean usedZMove;
    public boolean usedDynamax;
    public int dynamaxTurns;
    public List<Pokemon> team;

    public Player(String id, int numPokemon)
    {
        this.ID = id;
        this.data = new PlayerDataQuery(id);

        List<Pokemon> teamBuilder = new ArrayList<>();

        if(numPokemon == 1) teamBuilder.add(this.data.getSelectedPokemon());
        else for(int i = 0; i < numPokemon && i < this.data.getTeam().size(); i++) teamBuilder.add(Pokemon.build(this.data.getTeam().get(i)));

        this.team = Collections.unmodifiableList(teamBuilder);
        this.active = this.team.get(0);
        this.move = null;
        this.usedZMove = false;
        this.usedDynamax = false;
        this.dynamaxTurns = 0;
    }

    public Player()
    {
        this.ID = "BOT";
        this.usedZMove = false;
        this.usedDynamax = false;
        this.dynamaxTurns = 0;
    }

    public void swap(int index)
    {
        this.active = this.team.get(index);
    }

    public boolean lost()
    {
        boolean lost = true;
        for(Pokemon p : this.team) lost = lost && p.isFainted();
        return lost;
    }

    @Override
    public String toString() {
        return "Player{" +
                "ID='" + ID + '\'' +
                ", data=" + data +
                ", active=" + active +
                ", move=" + move +
                ", usedZMove=" + usedZMove +
                ", usedDynamax=" + usedDynamax +
                ", dynamaxTurns=" + dynamaxTurns +
                ", team=" + team +
                '}';
    }
}
