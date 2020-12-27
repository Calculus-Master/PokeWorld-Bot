package com.calculusmaster.pokecord.commands.economy;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.util.Mongo;
import com.mongodb.client.model.Filters;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.bson.Document;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class CommandMarket extends Command
{
    public static List<MarketEntry> marketEntries = new ArrayList<>();

    public static void init()
    {
        Mongo.MarketData.find(Filters.exists("marketID")).forEach(d -> marketEntries.add(MarketEntry.build(d.getString("marketID"))));
    }

    public CommandMarket(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    //TODO: p!market info, p!market collect, p!market search
    @Override
    public Command runCommand()
    {
        if(this.msg.length >= 4 && (this.msg[1].equals("list") || this.msg[1].equals("sell")) && isNumeric(2) && isNumeric(3) && Integer.parseInt(this.msg[2]) <= this.playerData.getPokemonList().length() && Integer.parseInt(this.msg[3]) > 0)
        {
            //Add pokemon to the market
            MarketEntry newMarketEntry = MarketEntry.create(this.player.getId(), this.playerData.getPokemonList().getString(Integer.parseInt(this.msg[2]) - 1), Integer.parseInt(this.msg[3]));
            marketEntries.add(newMarketEntry);
            this.playerData.removePokemon(newMarketEntry.pokemonID);

            this.embed.setDescription("Listed your " + newMarketEntry.pokemon.getName() + " for " + newMarketEntry.price + "c!");
        }
        else if(this.msg.length >= 3 && this.msg[1].equals("buy") && isNumeric(2))
        {
            //Buy pokemon from market
            MarketEntry entry = marketEntries.stream().filter(m -> m.marketID.equals(this.msg[2])).collect(Collectors.toList()).get(0);
            if(!this.player.getId().equals(entry.sellerID) && this.playerData.getCredits() >= entry.price)
            {
                marketEntries.remove(entry);
                Mongo.MarketData.deleteOne(Filters.eq("marketID", entry.marketID));

                this.playerData.changeCredits(-1 * entry.price);
                this.playerData.addPokemon(entry.pokemonID);
                new PlayerDataQuery(entry.sellerID).changeCredits(entry.price);

                this.embed.setDescription("Purchased a Level " + entry.pokemon.getLevel() + " " + entry.pokemon.getName() + " for " + entry.price + "c!");
            }
        }
        else
        {
            //Catch all - Displays random assortment of market entries
            Collections.shuffle(marketEntries);

            this.embed.setTitle("Market Listings");
            this.embed.setDescription(getMarketPage(this.msg.length > 2 && isNumeric(2) ? Integer.parseInt(this.msg[2]) : 0));
        }

        return this;
    }

    private static String getMarketPage(int start)
    {
        StringBuilder mList = new StringBuilder();

        for(int i = start * 20; i < start * 20 + 20; i++) if(i < marketEntries.size()) mList.append(getEntryLine(marketEntries.get(i))).append("\n");

        return mList.toString();
    }

    private static String getEntryLine(MarketEntry m)
    {
        return "ID: " + m.marketID + " | Level " + m.pokemon.getLevel() + " " + m.pokemon.getName() + " | Price: " + m.price;
    }

    public static class MarketEntry
    {
        public String marketID;
        public String sellerID;
        public String pokemonID;
        public int price;
        public Pokemon pokemon;

        public static MarketEntry create(String sellerID, String pokemonID, int price)
        {
            String marketID = generateUUID();
            Document marketData = new Document("marketID", marketID).append("sellerID", sellerID).append("pokemonID", pokemonID).append("price", price);
            Mongo.MarketData.insertOne(marketData);

            return build(marketID);
        }

        public static MarketEntry build(String marketID)
        {
            MarketEntry m = new MarketEntry();
            m.marketID = marketID;

            JSONObject marketJSON = getMarketJSON(marketID);
            m.sellerID = marketJSON.getString("sellerID");
            m.pokemonID = marketJSON.getString("pokemonID");
            m.price = marketJSON.getInt("price");
            m.pokemon = Pokemon.buildCore(m.pokemonID, -1);

            return m;
        }

        public static boolean isIDValid(String marketID)
        {
            return marketEntries.stream().anyMatch(m -> m.marketID.equals(marketID));
        }

        public static JSONObject getMarketJSON(String marketID)
        {
            return new JSONObject(Mongo.MarketData.find(Filters.eq("marketID", marketID)).first().toJson());
        }

        private static String generateUUID()
        {
            String digits = "0123456789";
            StringBuilder s = new StringBuilder();
            for(int i = 0; i < 12; i++) s.append(digits.charAt(new Random().nextInt(digits.length())));
            return s.toString();
        }

    }
}
