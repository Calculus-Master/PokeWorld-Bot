package com.calculusmaster.pokecord.game.trade;

import com.calculusmaster.pokecord.game.objectives.ObjectiveType;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.pokemon.evolution.EvolutionData;
import com.calculusmaster.pokecord.game.pokemon.evolution.EvolutionRegistry;
import com.calculusmaster.pokecord.game.pokemon.evolution.triggers.EvolutionTrigger;
import com.calculusmaster.pokecord.game.pokemon.evolution.triggers.TradeEvoTrigger;
import com.calculusmaster.pokecord.game.pokemon.evolution.triggers.TradeWithEvoTrigger;
import com.calculusmaster.pokecord.util.enums.StatisticType;
import com.calculusmaster.pokecord.util.helpers.LoggerHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Trade
{
    private static final ExecutorService POOL = Executors.newFixedThreadPool(2);
    private static final Map<String, Trade> TRADES = Collections.synchronizedMap(new HashMap<>());

    private TradePlayer[] players;
    private boolean waiting; //If true, trade is waiting, if false, trade is happening/complete

    private TextChannel channel;
    private String messageID;

    public static Trade create(String player1ID, String player2ID, TextChannel channel)
    {
        Trade t = new Trade();

        t.setPlayers(player1ID, player2ID);
        t.setChannel(channel);
        t.setWaiting();

        TRADES.put(player1ID, t);
        TRADES.put(player2ID, t);
        return t;
    }

    public void completeTrade()
    {
        //Delete Trade (done first just in case the transfers take time)
        Trade.delete(this.players[0].getID());

        //Transfer Offers
        this.players[0].getOffer().transfer(this.players[0].getPlayerData(), this.players[1].getPlayerData());
        this.players[1].getOffer().transfer(this.players[1].getPlayerData(), this.players[0].getPlayerData());

        //Update Statistics
        this.players[0].getPlayerData().getStatistics().increase(StatisticType.TRADES_COMPLETED);
        this.players[1].getPlayerData().getStatistics().increase(StatisticType.TRADES_COMPLETED);

        //Updated Objectives
        this.players[0].getPlayerData().updateObjective(ObjectiveType.COMPLETE_TRADE, 1);
        this.players[1].getPlayerData().updateObjective(ObjectiveType.COMPLETE_TRADE, 1);

        //Send the last message
        final String completeMessage = "***__Trade complete!__*** *Offers have been transferred into both of your inventories.*";

        this.channel //Send the trade complete message
                .sendMessage(this.players[0].getPlayerData().getMention() + " " + this.players[1].getPlayerData().getMention() + " – " + completeMessage)
                .setMessageReference(this.messageID)
                .delay(8, TimeUnit.SECONDS)
                .flatMap(Message::delete)
                .queue(v -> this.channel //Edit the embed so players know the trade was completed if they check the embed again for the final offers
                        .editMessageById(this.messageID, completeMessage)
                        .queue()
                );

        //Check Trade Evolutions
        POOL.submit(() -> {
            List<String> tradeEvolutionResults = new ArrayList<>();
            List<Runnable> queuedEvolutions = new ArrayList<>();

            for(TradePlayer p : this.players) if(p.getOffer().hasPokemon()) for(String pokemonUUID : p.getOffer().getPokemon())
            {
                Pokemon pokemon = Pokemon.build(pokemonUUID);

                if(EvolutionRegistry.hasEvolutionData(pokemon.getEntity()))
                {
                    List<EvolutionData> dataList = EvolutionRegistry.getEvolutionData(pokemon.getEntity());
                    for(EvolutionData data : dataList) if(data.hasTradeTrigger())
                    {
                        boolean canEvolve = true;
                        for(EvolutionTrigger trigger : data.getTriggers())
                        {
                            if(trigger instanceof TradeWithEvoTrigger t) canEvolve =
                                    this.getOther(p.getID()).getOffer().getPokemon().stream().anyMatch(s -> Pokemon.build(s).getEntity().equals(t.getOther()));
                            else if(!(trigger instanceof TradeEvoTrigger)) canEvolve =
                                    trigger.canEvolve(pokemon, this.channel.getGuild().getId());

                            if(!canEvolve) break;
                        }

                        if(canEvolve) queuedEvolutions.add(() -> {
                            LoggerHelper.info(Trade.class, "Performing Trade Evolution. | Pokemon: %s | UUID: %s".formatted(data.getSource().getName() + " -> " + data.getTarget().getName(), pokemonUUID));

                            pokemon.evolve(data, p.getPlayerData());

                            tradeEvolutionResults.add("**%s** has evolved into a **%s**!".formatted(pokemon.hasNickname() ? pokemon.getDisplayName() : data.getSource().getName(), data.getTarget().getName()));
                        });
                    }
                }
            }

            queuedEvolutions.forEach(Runnable::run);
            this.channel.sendMessage(String.join("\n", tradeEvolutionResults)).queue();
        });
    }

    public void start()
    {
        this.waiting = false;

        this.channel.sendMessageEmbeds(this.getTradeEmbed().build()).queue(m -> this.messageID = m.getId());
    }

    public EmbedBuilder getTradeEmbed()
    {
        return new EmbedBuilder()
                .setTitle("Trade: " + this.players[0].getPlayerData().getUsername() + " and " + this.players[1].getPlayerData().getUsername())
                .addField(this.getPlayerField(0))
                .addField(this.getPlayerField(1))
                .setFooter("Confirm your offer using /trade confirm!");
    }

    private MessageEmbed.Field getPlayerField(int p)
    {
        TradePlayer player = this.players[p];
        return new MessageEmbed.Field(
                "%s's Offer (Confirmed: %s)".formatted(player.getPlayerData().getUsername(), player.isConfirmed() ? ":white_check_mark:" : ":x:"),
                player.getOffer().getOverview(),
                false);
    }

    public void updateEmbed()
    {
        this.channel.editMessageEmbedsById(this.messageID, this.getTradeEmbed().build()).queue();
    }

    public void setConfirmed(String playerID, boolean value)
    {
        this.players[this.indexOf(playerID)].setConfirmed(value);

        if(this.isComplete()) POOL.submit(this::completeTrade);
    }

    public boolean isConfirmed(String playerID)
    {
        return this.players[this.indexOf(playerID)].isConfirmed();
    }

    public void removeBothConfirmed()
    {
        this.players[0].setConfirmed(false);
        this.players[1].setConfirmed(false);
    }

    public boolean isComplete()
    {
        return this.players[0].isConfirmed() && this.players[1].isConfirmed();
    }

    //Core
    public TradeOffer getOffer(String playerID)
    {
        return this.players[this.indexOf(playerID)].getOffer();
    }

    public void setPlayers(String p1, String p2)
    {
        this.players = new TradePlayer[]{new TradePlayer(p1), new TradePlayer(p2)};
    }

    public boolean hasPlayer(String playerID)
    {
        return this.players[0].getID().equals(playerID) || this.players[1].getID().equals(playerID);
    }

    public TradePlayer[] getPlayers()
    {
        return this.players;
    }

    public TradePlayer getOther(String playerID)
    {
        return this.players[0].getID().equals(playerID) ? this.players[1] : this.players[0];
    }

    public int indexOf(String ID)
    {
        return this.players[0].getID().equals(ID) ? 0 : 1;
    }

    public boolean isWaiting()
    {
        return this.waiting;
    }

    public void setWaiting()
    {
        this.waiting = true;
    }

    public void setChannel(TextChannel channel)
    {
        this.channel = channel;
    }

    public boolean isEmpty()
    {
        return this.players[0].getOffer().isEmpty() && this.players[1].getOffer().isEmpty();
    }

    //Static
    public static boolean isInTrade(String playerID)
    {
        return TRADES.containsKey(playerID);
    }

    public static Trade getTrade(String playerID)
    {
        return TRADES.get(playerID);
    }

    public static void delete(String playerID)
    {
        Trade t = TRADES.get(playerID);

        TRADES.remove(t.players[0].getID());
        TRADES.remove(t.players[1].getID());
    }
}
