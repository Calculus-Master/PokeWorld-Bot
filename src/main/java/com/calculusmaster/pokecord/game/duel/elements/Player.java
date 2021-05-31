package com.calculusmaster.pokecord.game.duel.elements;

import com.calculusmaster.pokecord.game.Move;
import com.calculusmaster.pokecord.game.Pokemon;
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
    public List<Pokemon> team;

    public Player(String id, int numPokemon)
    {
        this.ID = id;
        this.data = new PlayerDataQuery(id);

        List<Pokemon> teamBuilder = new ArrayList<>();

        if(numPokemon == 1) teamBuilder.add(this.data.getSelectedPokemon());
        else for(int i = 0; i < numPokemon; i++) teamBuilder.add(Pokemon.build(this.data.getTeam().getString(i)));

        this.team = Collections.unmodifiableList(teamBuilder);
        this.active = this.team.get(0);
        this.move = null;
        this.usedZMove = false;
    }

    public Player()
    {
        this.ID = "BOT";
        this.usedZMove = false;
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
}
