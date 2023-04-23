package com.calculusmaster.pokecord.game.world;

import com.calculusmaster.pokecord.game.duel.component.DuelFlag;
import com.calculusmaster.pokecord.game.duel.extension.RaidDuel;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonEntity;
import com.calculusmaster.pokecord.util.Global;
import com.calculusmaster.pokecord.util.helpers.LoggerHelper;
import com.calculusmaster.pokecord.util.helpers.ThreadPoolHandler;
import com.calculusmaster.pokecord.util.helpers.event.RaidEventHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class RaidEvent
{
    public static final int MAX_RAID_PLAYERS = 12;
    public static final int RAID_START_DELAY = 2 * 65;

    private final PokemonEntity pokemon;
    private final TextChannel channel;
    private final List<Member> players;

    private String messageID;
    private ScheduledFuture<?> raidEvent;
    private boolean isDuelActive;

    public RaidEvent(PokemonEntity pokemon, TextChannel channel)
    {
        this.pokemon = pokemon;
        this.channel = channel;
        this.players = new ArrayList<>();

        this.messageID = "";
        this.raidEvent = null;
        this.isDuelActive = false;
    }

    //Start the Duel
    public void startDuel()
    {
        if(this.players.isEmpty()) { this.expire(); return; }

        LoggerHelper.info(RaidEvent.class, "Raid Event | Starting! (Players: %s) | Guild: %s (%s) | Channel: %s (%s)".formatted(this.players.size(), this.channel.getGuild().getName(), this.channel.getGuild().getId(), this.channel.getName(), this.channel.getId()));

        this.isDuelActive = true;

        //Create Duel
        RaidDuel raid = RaidDuel.create();
        raid.addFlags(DuelFlag.SWAP_BANNED, DuelFlag.FLEE_BANNED);
        raid.addChannel(this.channel);

        //Mention Players
        this.channel.sendMessage("The Raid Pokemon **appears**! " + this.players.stream().map(Member::getAsMention).collect(Collectors.joining(" "))).queue(m -> {

            //Start Duel
            raid.start(this.pokemon, this.players.stream().map(Member::getId).toList());

            //Update the original embed to show the Raid has started
            this.updateEmbed();

        });
    }

    public void expire()
    {
        LoggerHelper.warn(RaidEvent.class, "Raid Event | Expired (No Players Joined) | Guild: %s (%s) | Channel: %s (%s)".formatted(this.channel.getGuild().getName(), this.channel.getGuild().getId(), this.channel.getName(), this.channel.getId()));

        EmbedBuilder embed = this.createEmbed()
                .clearFields()
                .setDescription("***__The Raid Pokemon disappears...__***\n*No players joined.*");

        this.channel.sendMessageEmbeds(embed.build()).setMessageReference(this.messageID).mentionRepliedUser(false).queue();

        RaidEventHelper.removeEvent(this.channel.getGuild().getId());
    }

    //Event
    public ScheduledFuture<?> getEvent()
    {
        return this.raidEvent;
    }

    public void queueStart()
    {
        this.raidEvent = ThreadPoolHandler.RAID.schedule(this::startDuel, RaidEvent.RAID_START_DELAY, TimeUnit.SECONDS);
    }

    //Message/Embed
    public void setMessageID(String messageID)
    {
        this.messageID = messageID;
    }

    public EmbedBuilder createEmbed()
    {
        EmbedBuilder embed = new EmbedBuilder();

        if(this.isDuelActive)
        {
            embed.setTitle("Raid Event: " + this.pokemon.getName())
                    .setDescription("***__Raid has started!__***");
        }
        else
        {
            embed.setTitle("Raid Event")
                    .setDescription("""
                        __***A Raid Pokemon is about to appear!***__
                        It seems to be a **%s**...
                        
                        **The battle will begin** %s.
                        
                        **Join the Raid** using `/raid join`! Your active Pokemon will be used for the Duel.
                        
                        If you are already in the Raid and want to leave, you can use `/raid leave`.
                        """.formatted(this.pokemon.getName(), "<t:" + (Global.timeNowEpoch() + RAID_START_DELAY) + ":R>"));
        }

        return embed.addField(
                        "Players (%s / %s)".formatted(this.players.size(), MAX_RAID_PLAYERS),
                        this.players.stream().map(m -> m.getUser().getAsTag()).collect(Collectors.joining("\n")),
                        false
                )
                .setTimestamp(Global.timeNow());
    }

    public void updateEmbed()
    {
        this.channel.retrieveMessageById(this.messageID).queue(m -> m.editMessageEmbeds(this.createEmbed().build()).queue());
    }

    //Players
    public void addPlayer(Member player)
    {
        this.players.add(player);
        this.updateEmbed();
    }

    public void removePlayer(Member player)
    {
        this.players.remove(player);
        this.updateEmbed();
    }

    public boolean hasPlayer(String playerID)
    {
        return this.players.stream().anyMatch(m -> m.getId().equals(playerID));
    }

    public List<Member> getPlayers()
    {
        return this.players;
    }

    public boolean isFull()
    {
        return this.players.size() == MAX_RAID_PLAYERS;
    }

    //Duel Active
    public boolean isDuelActive()
    {
        return this.isDuelActive;
    }

    //Basic Getters
    public TextChannel getChannel()
    {
        return this.channel;
    }

    public String getMessageID()
    {
        return this.messageID;
    }

    public PokemonEntity getPokemon()
    {
        return this.pokemon;
    }
}
