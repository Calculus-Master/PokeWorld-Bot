package com.calculusmaster.pokecord.commandslegacy.duel;

import com.calculusmaster.pokecord.commandslegacy.CommandLegacy;
import com.calculusmaster.pokecord.util.helpers.DataHelper;
import com.calculusmaster.pokecord.util.helpers.LoggerHelper;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class CommandLegacyTarget extends CommandLegacy
{
    public static final Map<String, String> SERVER_TARGETS = new HashMap<>();
    public static final Map<String, Integer> SERVER_TARGET_DUELS_WON = new HashMap<>();

    public CommandLegacyTarget(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public CommandLegacy runCommand()
    {
        boolean info = this.msg.length == 2 && this.msg[1].equals("info");

        if(info)
        {
            this.embed.setTitle("Targets Info");
            this.embed.setDescription("Targets are randomly chosen players in a server. If a player is chosen as a Target, Duel rewards are slightly changed.");
            this.embed
                    .addField("If the Target wins a Duel", "The Target will gain increasing rewards based on how many consecutive duels they have won (as the target).", true)
                    .addField("If the Target loses a Duel", "The Target will lose their status as the Server Target. A new Target will be chosen, and the player who defeated the Target will gain a large amount of credits (and XP) as a reward.", true);
        }
        else if(!SERVER_TARGETS.containsKey(this.server.getId()))
        {
            CommandLegacyTarget.generateNewServerTarget(this.server);

            this.response = "A new Server Target has been chosen! Use `p!target` to see who it is!";
        }
        else
        {
            Member m = this.getMember(SERVER_TARGETS.get(this.server.getId()));

            this.response = "The Server Target is " + m.getEffectiveName() + " (" + m.getUser().getAsTag() + ")! Defeat them in a PvP Duel to earn extra rewards! If you are the target, winning PvP Duels grants extra credits the more duels you win!";
        }

        return this;
    }

    public static void generateNewServerTarget(Guild g)
    {
        DataHelper.updateServerPlayers(g);

        List<String> pool = DataHelper.SERVER_PLAYERS.get(g.getId());

        if(pool.isEmpty())
        {
            LoggerHelper.warn(CommandLegacyTarget.class, "No Server Targets found in " + g.getName() + "!");
            return;
        }

        String target = pool.get(new Random().nextInt(pool.size()));

        SERVER_TARGETS.put(g.getId(), target);
        SERVER_TARGET_DUELS_WON.put(g.getId(), 0);

        LoggerHelper.info(CommandLegacyTarget.class, "Generated new Duel Target for " + g.getName() + " (" + g.getId() + ") - ID: " + target);
    }

    public static boolean isTarget(Guild server, String ID)
    {
        return SERVER_TARGETS.containsKey(server.getId()) && SERVER_TARGETS.get(server.getId()).contains(ID);
    }
}
