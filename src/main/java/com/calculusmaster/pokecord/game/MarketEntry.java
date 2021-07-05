package com.calculusmaster.pokecord.game;

import com.calculusmaster.pokecord.util.Mongo;
import com.calculusmaster.pokecord.util.helpers.CacheHelper;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.Random;

public class MarketEntry
{
    public String marketID;
    public String sellerID;
    public String sellerName;
    public String pokemonID;
    public int price;
    public Pokemon pokemon;

    public static MarketEntry create(String sellerID, String sellerName, String pokemonID, int price)
    {
        MarketEntry m = new MarketEntry();

        m.sellerID = sellerID;
        m.sellerName = sellerName;
        m.pokemonID = pokemonID;
        m.price = price;

        m.pokemon = Pokemon.buildCore(pokemonID, -1);
        m.marketID = m.generateMarketID();

        Document marketData = new Document("marketID", m.marketID).append("sellerID", m.sellerID).append("sellerName", m.sellerName).append("pokemonID", m.pokemonID).append("price", m.price);
        Mongo.MarketData.insertOne(marketData);

        CacheHelper.MARKET_ENTRIES.add(m);

        return m;
    }

    public static MarketEntry build(String marketID)
    {
        MarketEntry m = new MarketEntry();
        m.marketID = marketID;

        Document doc = Mongo.MarketData.find(Filters.eq("marketID", marketID)).first();

        m.sellerID = doc.getString("sellerID");
        m.sellerName = doc.getString("sellerName");
        m.pokemonID = doc.getString("pokemonID");
        m.price = doc.getInteger("price");
        m.pokemon = Pokemon.buildCore(m.pokemonID, -1);

        return m;
    }

    public static void delete(String marketID)
    {
        Mongo.MarketData.deleteOne(Filters.eq("marketID", marketID));

        int index = -1;
        for(int i = 0; i < CacheHelper.MARKET_ENTRIES.size(); i++) if(CacheHelper.MARKET_ENTRIES.get(i).marketID.equals(marketID)) index = i;
        CacheHelper.MARKET_ENTRIES.remove(index);
    }

    public String getEntryLine()
    {
        if(this.pokemon == null) return "ERROR";

        return "ID: " + this.marketID + " | Level " + this.pokemon.getLevel() + " " + this.pokemon.getName() + " | Price: " + this.price + " | IV: " + this.pokemon.getTotalIV();
    }

    public static boolean isValidID(String marketID)
    {
        return CacheHelper.MARKET_ENTRIES.stream().anyMatch(m -> m.marketID.equals(marketID));
    }

    private String generateMarketID()
    {
        String digits = "0123456789";
        StringBuilder s = new StringBuilder();
        for(int i = 0; i < 8; i++) s.append(digits.charAt(new Random().nextInt(digits.length())));
        return s.toString();
    }
}