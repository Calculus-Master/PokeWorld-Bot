package com.calculusmaster.pokecord.game.tournament;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TournamentHelper
{
    public static final List<Tournament> TOURNAMENTS = new ArrayList<>();

    public static boolean isInTournament(String ID)
    {
        return TOURNAMENTS.stream().anyMatch(t -> t.hasPlayer(ID));
    }

    public static Tournament instance(String ID)
    {
        return TOURNAMENTS.stream().filter(t -> t.hasPlayer(ID)).collect(Collectors.toList()).get(0);
    }

    public static void delete(String ID)
    {
        int index = -1;
        for(int i = 0; i < TOURNAMENTS.size(); i++) if(TOURNAMENTS.get(i).hasPlayer(ID)) index = i;
        TOURNAMENTS.remove(index);
    }

    public static class Matchup
    {
        public String player1, player2;

        public Matchup(String player1, String player2)
        {
            this.player1 = player1;
            this.player2 = player2;
        }

        public boolean has(String ID)
        {
            return this.player1.equals(ID) || this.player2.equals(ID);
        }
    }

    public enum TournamentStatus
    {
        WAITING_FOR_PLAYERS,
        WAITING_FOR_START,
        DUELING,
        COMPLETE
    }
}
