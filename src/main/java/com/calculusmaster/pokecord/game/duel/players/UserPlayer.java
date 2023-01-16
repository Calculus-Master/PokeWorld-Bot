package com.calculusmaster.pokecord.game.duel.players;

import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserPlayer extends Player
{
    public PlayerDataQuery data;

    public UserPlayer(PlayerDataQuery playerData, int maxTeamSize)
    {
        super(playerData.getID());

        this.data = playerData;

        //Team
        int size = Math.min(this.data.getTeam().size(), maxTeamSize);

        List<Pokemon> team = new ArrayList<>();
        for(int i = 0; i < size; i++) team.add(Pokemon.build(this.data.getTeam().get(i)));

        this.setTeam(Collections.unmodifiableList(team));
    }

    public UserPlayer(PlayerDataQuery playerData, Pokemon active)
    {
        super(playerData.getID());

        this.data = playerData;
        this.setTeam(Collections.singletonList(active));
    }

    @Override
    public String getName()
    {
        return this.data.getUsername();
    }
}
