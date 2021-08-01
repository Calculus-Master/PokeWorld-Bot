package com.calculusmaster.pokecord.game.trade.elements;

import com.calculusmaster.pokecord.mongo.PlayerDataQuery;

public class TradePlayer
{
    public String ID;
    public TradeOffer offer;
    public PlayerDataQuery data;
    public boolean confirmed;

    public TradePlayer(String ID)
    {
        this.ID = ID;
        this.offer = new TradeOffer(ID);
        this.data = this.offer.player;
        this.confirmed = false;
    }
}
