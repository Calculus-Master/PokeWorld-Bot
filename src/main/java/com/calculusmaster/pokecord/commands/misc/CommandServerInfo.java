package com.calculusmaster.pokecord.commands.misc;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.util.Global;
import com.calculusmaster.pokecord.util.helpers.DataHelper;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CommandServerInfo extends Command
{
    public CommandServerInfo(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg, true);
    }

    @Override
    public Command runCommand()
    {
        if(this.insufficientMasteryLevel(Feature.VIEW_SERVER_INFO)) return this.invalidMasteryLevel(Feature.VIEW_SERVER_INFO);

        this.server.loadMembers();
        DataHelper.updateServerPlayers(this.server);

        this.embed.setTitle(this.server.getName() + " (ID: " + this.server.getId() + ")");

        this.embed
                .addField("Prefix", "`" + this.serverData.getPrefix() + "`", true)
                .addField("Created", this.server.getTimeCreated().format(DateTimeFormatter.RFC_1123_DATE_TIME), true)
                .addField("Members", "Pokecord Players: " + DataHelper.SERVER_PLAYERS.get(this.server.getId()).size() + "\nTotal: " + this.server.getMembers().size(), true);

        this.embed.addField("General Settings",
                "Changing Z Crystals in Duels: " + this.serverData.canEquipZCrystalDuel() + "\n" +
                "Z-Moves: " + this.serverData.areZMovesEnabled() + "\n" +
                "Dynamax: " + this.serverData.isDynamaxEnabled(),
                false);

        this.embed.addField("Spawn Channels", this.getSpawnChannels(), false);
        this.embed.addField("Duel Channels", this.getDuelChannels(), false);
        this.embed.addField("Bot Channels", this.getBotChannels(), false);

        this.embed.setFooter(Global.userHasAdmin(this.server, this.player) ? "You are a Server Administrator!" : "");
        return this;
    }

    private String getSpawnChannels()
    {
        StringBuilder s = new StringBuilder();
        List<String> ids = new ArrayList<>(List.copyOf(this.serverData.getSpawnChannels()));

        if(ids.size() == 0) s.append("Spawns are disabled! Notify a server administrator to run `p!settings server spawnchannel` in a channel to allow spawns.");
        else for(String id : ids) s.append(this.server.getTextChannelById(id).getAsMention()).append("\n");
        return s.toString();
    }

    private String getDuelChannels()
    {
        StringBuilder s = new StringBuilder();
        List<String> ids = new ArrayList<>(List.copyOf(this.serverData.getDuelChannels()));

        if(ids.size() == 0) s.append("Duels are allowed anywhere!");
        else for(String id : ids) s.append(this.server.getTextChannelById(id).getAsMention()).append("\n");
        return s.toString();
    }

    private String getBotChannels()
    {
        StringBuilder s = new StringBuilder();
        List<String> ids = new ArrayList<>(List.copyOf(this.serverData.getBotChannels()));

        if(ids.size() == 0) s.append("Bot commands are allowed anywhere!");
        else for(String id : ids) s.append(this.server.getTextChannelById(id).getAsMention()).append("\n");
        return s.toString();
    }
}
