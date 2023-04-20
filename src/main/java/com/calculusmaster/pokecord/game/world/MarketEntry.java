package com.calculusmaster.pokecord.game.world;

import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.util.Global;
import com.calculusmaster.pokecord.util.helpers.IDHelper;
import org.bson.Document;

public class MarketEntry
{
    private final String marketID;
    private final String sellerID;
    private final String pokemonID;
    private final int price;

    private final String sellerUsername;
    private final Pokemon pokemon;
    private final long timestamp;

    public MarketEntry(String sellerID, String pokemonID, int price, String sellerUsername)
    {
        this.marketID = IDHelper.numeric(8);
        this.sellerID = sellerID;
        this.pokemonID = pokemonID;
        this.price = price;

        this.pokemon = Pokemon.build(this.pokemonID);
        this.sellerUsername = sellerUsername;
        this.timestamp = Global.timeNowEpoch();
    }

    public MarketEntry(Document data)
    {
        this.marketID = data.getString("marketID");
        this.sellerID = data.getString("sellerID");
        this.pokemonID = data.getString("pokemonID");
        this.price = data.getInteger("price");

        this.pokemon = Pokemon.build(this.pokemonID);
        this.sellerUsername = data.getString("sellerUsername");
        this.timestamp = data.getInteger("timestamp");
    }

    public Document serialize()
    {
        return new Document()
                .append("marketID", this.marketID)
                .append("sellerID", this.sellerID)
                .append("pokemonID", this.pokemonID)
                .append("price", this.price)
                .append("sellerUsername", this.sellerUsername)
                .append("timestamp", (int)this.timestamp)
                ;
    }

    public long getTimestamp()
    {
        return this.timestamp;
    }

    public Pokemon getPokemon()
    {
        return this.pokemon;
    }

    public String getSellerUsername()
    {
        return this.sellerUsername;
    }

    public String getMarketID()
    {
        return this.marketID;
    }

    public String getSellerID()
    {
        return this.sellerID;
    }

    public String getPokemonID()
    {
        return this.pokemonID;
    }

    public int getPrice()
    {
        return this.price;
    }
}
