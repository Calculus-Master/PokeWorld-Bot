package com.calculusmaster.pokecord.game.trade;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TradeHelper
{
    public static final List<Trade> TRADES = new ArrayList<>();

    public static boolean isInTrade(String playerID)
    {
        return TRADES.stream().anyMatch(t -> t.hasPlayer(playerID));
    }

    public static Trade instance(String playerID)
    {
        return TRADES.stream().filter(t -> t.hasPlayer(playerID)).collect(Collectors.toList()).get(0);
    }

    public static void delete(String playerID)
    {
        int index = -1;
        for(Trade t : TRADES) if(t.hasPlayer(playerID)) index = TRADES.indexOf(t);
        TRADES.remove(index);
    }

    public enum OfferType
    {
        CREDITS,
        REDEEMS,
        POKEMON,
        TM,
    }

    public enum TradeStatus
    {
        WAITING,
        TRADING,
        COMPLETE
    }
}
