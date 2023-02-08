package com.calculusmaster.pokecord.game.duel.extension;

import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.duel.core.DuelHelper;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.util.helpers.LoggerHelper;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import static com.calculusmaster.pokecord.game.duel.core.DuelHelper.DUELS;

public class CasualMatchmadeDuel extends Duel
{
    public static final Queue<MatchmakingPlayer> QUEUE_1v1 = new LinkedList<>();
    public static final Queue<MatchmakingPlayer> QUEUE_3v3 = new LinkedList<>();
    public static final Queue<MatchmakingPlayer> QUEUE_6v6 = new LinkedList<>();

    public static void init()
    {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(CasualMatchmadeDuel::processQueues, 0, 15, TimeUnit.SECONDS);
    }

    private static void processQueues()
    {
        int tasks_1v1 = QUEUE_1v1.size() / 2;
        int tasks_3v3 = QUEUE_3v3.size() / 2;
        int tasks_6v6 = QUEUE_6v6.size() / 2;

        if(tasks_1v1 == 0 && tasks_3v3 == 0 && tasks_6v6 == 0) return;

        LoggerHelper.info(CasualMatchmadeDuel.class, "Processing Queues for Casual Duels...");

        int totalTaskSlots = 4;
        int slots_1v1 = tasks_1v1 > 0 ? 1 : 0;
        int slots_3v3 = tasks_3v3 > 0 ? 1 : 0;
        int slots_6v6 = tasks_6v6 > 0 ? 1 : 0;

        int remainingSlots = totalTaskSlots - slots_1v1 - slots_3v3 - slots_6v6;
        tasks_1v1 -= slots_1v1;
        tasks_3v3 -= slots_3v3;
        tasks_6v6 -= slots_6v6;

        if(tasks_1v1 == 0 && tasks_3v3 == 0 && tasks_6v6 == 0) remainingSlots = 0;

        while(remainingSlots > 0)
        {

            if(tasks_1v1 > 0) { slots_1v1++; remainingSlots--; }
            if(tasks_3v3 > 0 && remainingSlots > 0) { slots_3v3++; remainingSlots--; }
            if(tasks_6v6 > 0 && remainingSlots > 0) { slots_6v6++; remainingSlots--; }
        }

        BiConsumer<Queue<MatchmakingPlayer>, Integer> processor = (queue, size) -> {
            MatchmakingPlayer p1 = queue.remove();
            MatchmakingPlayer p2 = queue.remove();

            if(p1.getTextChannel().getGuild().equals(p2.getTextChannel().getGuild()) && p1.getTextChannel().getId().equals(p2.getTextChannel().getId()))
            {
                //TODO: implement queue rerouting and failed attempt counts
                LoggerHelper.warn(CasualMatchmadeDuel.class, "Players %s and %s are in the same TextChannel!");
            }

            Duel d = CasualMatchmadeDuel.create(p1.ID, p2.ID, size, List.of(p1.textChannel, p2.textChannel));

            Stream.of(p1, p2).forEach(p -> {
                PlayerDataQuery.ofNonNull(p.ID).directMessage("Matchmaking success! Your duel will start shortly in the channel: " + p.textChannel.getName() + ", in the server: " + p.textChannel.getGuild().getName() + ".");
                p.textChannel.sendMessage(Objects.requireNonNull(p.textChannel.getGuild().getMemberById(p.ID)).getAsMention()).delay(2, TimeUnit.SECONDS).flatMap(m -> m.delete().delay(5, TimeUnit.SECONDS)).queue();
            });

            Executors.newSingleThreadScheduledExecutor().schedule(d::sendTurnEmbed, 5, TimeUnit.SECONDS);
            LoggerHelper.info(CasualMatchmadeDuel.class, "Matchmaking success! " + p1.ID + " vs " + p2.ID);
        };

        LoggerHelper.info(CasualMatchmadeDuel.class, "Allotted Task Slots for each Casual Duel Queue: 1v1: " + slots_1v1 + ", 3v3: " + slots_3v3 + ", 6v6: " + slots_6v6);

        for(int i = 0; i < slots_1v1; i++) processor.accept(QUEUE_1v1, 1);
        for(int i = 0; i < slots_3v3; i++) processor.accept(QUEUE_3v3, 3);
        for(int i = 0; i < slots_6v6; i++) processor.accept(QUEUE_6v6, 6);
    }

    public static Duel create(String player1ID, String player2ID, int size, List<TextChannel> channels)
    {
        CasualMatchmadeDuel duel = new CasualMatchmadeDuel();

        duel.setStatus(DuelHelper.DuelStatus.WAITING);
        duel.setSize(size);
        duel.setTurn();
        channels.forEach(duel::addChannel);
        duel.setPlayers(player1ID, player2ID, size);
        duel.setDefaults();
        duel.setDuelPokemonObjects(0);
        duel.setDuelPokemonObjects(1);

        DUELS.add(duel);
        return duel;
    }

    public static void queuePlayer(String player, TextChannel channel, QueueType queue)
    {
        MatchmakingPlayer p = new MatchmakingPlayer(player, channel);

        switch(queue)
        {
            case ONES -> CasualMatchmadeDuel.QUEUE_1v1.add(p);
            case THREES -> CasualMatchmadeDuel.QUEUE_3v3.add(p);
            case SIXES -> CasualMatchmadeDuel.QUEUE_6v6.add(p);
        }
    }

    public static void dequeuePlayer(String playerID)
    {
        if(!QUEUE_1v1.isEmpty()) QUEUE_1v1.removeIf(mp -> mp.ID.equals(playerID));
        if(!QUEUE_3v3.isEmpty()) QUEUE_3v3.removeIf(mp -> mp.ID.equals(playerID));
        if(!QUEUE_6v6.isEmpty()) QUEUE_6v6.removeIf(mp -> mp.ID.equals(playerID));
    }

    //TODO: Move to DuelHelper and make universal for ranked duels, this and gym duels
    public static boolean isQueueing(String playerID)
    {
        return CasualMatchmadeDuel.QUEUE_1v1.stream().anyMatch(p -> p.ID.equals(playerID)) ||
                CasualMatchmadeDuel.QUEUE_3v3.stream().anyMatch(p -> p.ID.equals(playerID)) ||
                CasualMatchmadeDuel.QUEUE_6v6.stream().anyMatch(p -> p.ID.equals(playerID));
    }

    public static class MatchmakingPlayer
    {
        private final String ID;
        private final TextChannel textChannel;

        MatchmakingPlayer(String ID, TextChannel channel)
        {
            this.ID = ID;
            this.textChannel = channel;
        }

        public String getID()
        {
            return this.ID;
        }

        public TextChannel getTextChannel()
        {
            return this.textChannel;
        }
    }

    public enum QueueType
    {
        ONES,
        THREES,
        SIXES;
    }
}
