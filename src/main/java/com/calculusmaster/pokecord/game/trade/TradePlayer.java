package com.calculusmaster.pokecord.game.trade;

import com.calculusmaster.pokecord.mongo.PlayerDataQuery;

public class TradePlayer
{
    private final String ID;
    private final PlayerDataQuery playerData;
    private final TradeOffer offer;
    private boolean confirmed;

    public TradePlayer(String ID)
    {
        this.ID = ID;
        this.playerData = PlayerDataQuery.build(ID);
        this.offer = new TradeOffer();
        this.confirmed = false;
    }

    public boolean isConfirmed()
    {
        return this.confirmed;
    }

    public void setConfirmed(boolean confirmed)
    {
        this.confirmed = confirmed;
    }

    public TradeOffer getOffer()
    {
        return this.offer;
    }

    public String getID()
    {
        return this.ID;
    }

    public PlayerDataQuery getPlayerData()
    {
        return this.playerData;
    }
}
