package com.calculusmaster.pokecord.game;

import com.calculusmaster.pokecord.mongo.PlayerDataQuery;

import java.util.ArrayList;
import java.util.List;

public class Trade
{
    public static final List<Trade> TRADES = new ArrayList<>();

    private TradeStatus status;

    private String[] players;
    private PlayerDataQuery playerData;
    private DuelOffer offers;
    private boolean[] confirms;


    private static class DuelOffer
    {
        public int credits;
        public List<String> pokemon;

        void giveToPlayer(PlayerDataQuery giver, PlayerDataQuery receiver)
        {
            if(this.credits > 0) receiver.changeCredits(this.credits);
            if(!this.pokemon.isEmpty())
            {
                for(String s : pokemon)
                {
                    giver.removePokemon(s);
                    receiver.addPokemon(s);
                }
            }
        }

        void addCredits(int c)
        {
            this.credits += c;
        }

        void removeCredits(int c)
        {
            this.credits -= c;
        }

        void addPokemon(String UUID)
        {
            this.pokemon.add(UUID);
        }

        void removePokemon(String UUID)
        {
            this.pokemon.remove(UUID);
        }
    }

    public enum TradeStatus
    {
        WAITING,
        TRADING,
        COMPLETE;
    }
}
