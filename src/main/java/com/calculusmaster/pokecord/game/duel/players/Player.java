package com.calculusmaster.pokecord.game.duel.players;

import com.calculusmaster.pokecord.game.moves.Move;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Player
{
    public String ID;

    public List<Pokemon> team;
    public Pokemon active;
    public Move move;

    public boolean usedZMove;
    public boolean usedDynamax;
    public int dynamaxTurns;

    protected Player(String ID)
    {
        this.ID = ID;

        this.team = new ArrayList<>();
        this.active = null;
        this.move = null;

        this.usedZMove = false;
        this.usedDynamax = false;
        this.dynamaxTurns = 0;
    }

    protected void setTeam(List<Pokemon> team)
    {
        this.team = Collections.unmodifiableList(team);
        this.active = this.team.get(0);

        this.team.forEach(p -> p.setHealth(p.getMaxHealth()));
    }

    public abstract String getName();

    public void swap(int index)
    {
        this.active = this.team.get(index);
    }

    public boolean lost()
    {
        return this.team.stream().allMatch(Pokemon::isFainted);
    }

    @Override
    public String toString() {
        return "Player{" +
                "ID='" + ID + '\'' +
                ", active=" + active +
                ", move=" + move +
                ", usedZMove=" + usedZMove +
                ", usedDynamax=" + usedDynamax +
                ", dynamaxTurns=" + dynamaxTurns +
                ", team=" + team +
                '}';
    }
}
