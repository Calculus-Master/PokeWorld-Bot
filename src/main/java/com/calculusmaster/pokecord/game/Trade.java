package com.calculusmaster.pokecord.game;

import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Trade
{
    public static final List<Trade> TRADES = new ArrayList<>();

    private TradeStatus status;

    private String[] players;
    private PlayerDataQuery[] playerData;
    private TradeOffer[] offers;
    private boolean[] confirms;
    private String messageID;

    public static Trade initiate(String p1ID, String p2ID, String msgID)
    {
        Trade t = new Trade();

        t.setPlayers(p1ID, p2ID);
        t.setPlayerData();
        t.setTrades();
        t.setConfirms();
        t.setStatus(TradeStatus.WAITING);
        //t.setMessageID(msgID);

        TRADES.add(t);
        System.out.println("Added new trade " + t.toString());
        return t;
    }

    @Override
    public String toString() {
        return "Trade{" +
                "status=" + status +
                ", players=" + Arrays.toString(players) +
                ", playerData=" + Arrays.toString(playerData) +
                ", offers=" + Arrays.toString(offers) +
                ", confirms=" + Arrays.toString(confirms) +
                ", channelID='" + messageID + '\'' +
                '}';
    }

    //Events
    public void onComplete()
    {
        this.offers[0].giveToPlayer(this.playerData[0], this.playerData[1]);
        this.offers[1].giveToPlayer(this.playerData[1], this.playerData[0]);
        this.setStatus(TradeStatus.COMPLETE);
        TRADES.remove(this);
    }

    public EmbedBuilder getTradeEmbed()
    {
        EmbedBuilder trade = new EmbedBuilder();

        trade.setTitle("Trade between " + this.playerData[0].getUsername() + " and " + this.playerData[1].getUsername());
        trade.setDescription(this.playerData[0].getUsername() + "'s " + (this.confirms[0] ? ":white_check_mark:" : "") + " Offer:\n" + this.getPlayerOffer(0) + "\n" + this.playerData[1].getUsername() + "'s " + (this.confirms[1] ? ":white_check_mark:" : "") + " Offer:\n" + this.getPlayerOffer(1));

        return trade;
    }

    private String getPlayerOffer(int player)
    {
        TradeOffer offer = this.offers[player];
        StringBuilder sb = new StringBuilder("```");
        if(offer.credits != 0) sb.append(offer.credits + " Credits\n");
        if(!offer.pokemon.isEmpty()) for(String s : offer.pokemon)
        {
            Pokemon p = Pokemon.buildCore(s);
            sb.append("Level " + p.getLevel() + " " + p.getName() + "\n");
        }
        sb.append("```");
        if(sb.length() == 6) sb = new StringBuilder("``` \n```");
        return sb.toString();
    }

    //Trade Specific Methods
    public void confirmTrade(String id)
    {
        this.confirms[this.p(id)] = true;
    }

    public void unconfirmTrade(String id)
    {
        this.confirms[this.p(id)] = false;
    }

    public boolean isComplete()
    {
        return this.confirms[0] == this.confirms[1] && this.confirms[0];
    }

    public void addCredits(String id, int c)
    {
        this.offers[this.p(id)].addCredits(c);
        this.unconfirmTrade(id);
    }

    public void removeCredits(String id, int c)
    {
        this.offers[this.p(id)].removeCredits(c);
        this.unconfirmTrade(id);
    }

    public void addPokemon(String id, int... ints)
    {
        System.out.println(Arrays.toString(ints));
        for(int n : ints) this.offers[this.p(id)].addPokemon(this.playerData[this.p(id)].getPokemonList().getString(n - 1));
        this.unconfirmTrade(id);
    }

    public void removePokemon(String id, int... ints)
    {
        for(int n : ints) this.offers[this.p(id)].removePokemon(this.playerData[this.p(id)].getPokemonList().getString(n - 1));
        this.unconfirmTrade(id);
    }

    //Getters
    private int p(String id)
    {
        return this.players[0].equals(id) ? 0 : 1;
    }

    public List<String> getPlayers()
    {
        return Arrays.asList(this.players);
    }

    public String getMessageID()
    {
        System.out.println("MESSAGE ID: " + this.messageID);
        return this.messageID;
    }

    public TradeStatus getStatus()
    {
        return this.status;
    }

    //Static
    public static boolean isInTrade(String playerID)
    {
        return TRADES.stream().anyMatch(d -> d.getPlayers().contains(playerID));
    }

    public static Trade getInstance(String pID)
    {
        return TRADES.stream().filter(d -> d.getPlayers().contains(pID)).collect(Collectors.toList()).get(0);
    }

    public static void remove(String id)
    {
        int index = -1;
        for(Trade t : TRADES) if(t.getPlayers().contains(id)) index = TRADES.indexOf(t);
        TRADES.remove(index);
    }

    //Setters
    public void setMessageID(String msgID)
    {
        this.messageID = msgID;
    }

    public void setPlayers(String p1ID, String p2ID)
    {
        this.players = new String[]{p1ID, p2ID};
    }

    public void setPlayerData()
    {
        this.playerData = new PlayerDataQuery[]{new PlayerDataQuery(this.players[0]), new PlayerDataQuery(this.players[1])};
    }

    public void setTrades()
    {
        this.offers = new TradeOffer[]{new TradeOffer(), new TradeOffer()};
    }

    public void setConfirms()
    {
        this.confirms = new boolean[]{false, false};
    }

    public void setStatus(TradeStatus status)
    {
        this.status = status;
    }

    private static class TradeOffer
    {
        public int credits;
        public List<String> pokemon = new ArrayList<>();

        void giveToPlayer(PlayerDataQuery giver, PlayerDataQuery receiver)
        {
            if(this.credits > 0)
            {
                giver.changeCredits(-1 * this.credits);
                receiver.changeCredits(this.credits);
            }
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
