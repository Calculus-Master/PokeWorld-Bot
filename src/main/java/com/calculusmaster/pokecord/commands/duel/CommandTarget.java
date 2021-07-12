package com.calculusmaster.pokecord.commands.duel;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;
import com.calculusmaster.pokecord.util.helpers.LoggerHelper;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.*;

public class CommandTarget extends Command
{
    public static final Map<String, String> SERVER_TARGETS = new HashMap<>();
    public static final Map<String, Integer> SERVER_TARGET_DUELS_WON = new HashMap<>();
    public static final Map<String, List<String>> SERVER_POKECORD_PLAYERS = new HashMap<>();

    public CommandTarget(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        if(!SERVER_TARGETS.containsKey(this.server.getId()))
        {
            CommandTarget.generateNewServerTarget(this.server);

            this.sendMsg("A new Server Target has been chosen! Use `p!target` to see who it is!");
        }
        else
        {
            Member m = this.getMember(SERVER_TARGETS.get(this.server.getId()));

            this.sendMsg("The Server Target is " + m.getEffectiveName() + "! Defeat them in a PvP Duel to earn extra rewards! If you are the target, winning PvP Duels grants extra credits the more duels you win!");
        }

        return this;
    }

    public static void generateNewServerTarget(Guild g)
    {
        CommandTarget.generatePlayerCache(g);

        List<String> pool = SERVER_POKECORD_PLAYERS.get(g.getId());

        String target = pool.get(new Random().nextInt(pool.size()));

        SERVER_TARGETS.put(g.getId(), target);
        SERVER_TARGET_DUELS_WON.put(g.getId(), 0);

        LoggerHelper.info(CommandTarget.class, "Generated new Duel Target for " + g.getName() + " (" + g.getId() + ") - ID: " + target);
    }

    public static void generatePlayerCache(Guild g)
    {
        SERVER_POKECORD_PLAYERS.put(g.getId(), new ArrayList<>());

        g.loadMembers();

        for(Member m : g.getMembers()) if(PlayerDataQuery.isRegistered(m.getId())) SERVER_POKECORD_PLAYERS.get(g.getId()).add(m.getId());
    }

    public static boolean isTarget(Guild server, String ID)
    {
        return SERVER_TARGETS.containsKey(server.getId()) && SERVER_TARGETS.get(server.getId()).contains(ID);
    }
}
