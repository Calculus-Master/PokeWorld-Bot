package com.calculusmaster.pokecord.commands.duel;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.commands.CommandInvalid;
import com.calculusmaster.pokecord.game.duel.extension.RaidDuel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandRaidDuel extends Command
{
    public CommandRaidDuel(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        boolean join = this.msg.length == 2 && this.msg[1].equals("join");
        boolean leave = this.msg.length == 2 && this.msg[1].equals("leave");

        if(join || leave)
        {
            if(!RaidDuel.SERVER_RAIDS.containsKey(this.server.getId())) this.sendMsg("There are no active Raid duels in this server!");
            else if(join && RaidDuel.SERVER_RAIDS.get(this.server.getId()).isPlayerWaiting(this.player.getId())) this.sendMsg("You are already ready for this server's active Raid duel! To leave, type `p!raidduel leave`.");
            else if(leave && !RaidDuel.SERVER_RAIDS.get(this.server.getId()).isPlayerWaiting(this.player.getId())) this.sendMsg("You are not in this server's active Raid duel! To join, type `p!raidduel join`.");
            else
            {
                if(join)
                {
                    RaidDuel.SERVER_RAIDS.get(this.server.getId()).addPlayer(this.player.getId());

                    this.sendMsg("You have joined the Raid with " + this.playerData.getSelectedPokemon().getName() + "!");
                }
                else if(leave)
                {
                    RaidDuel.SERVER_RAIDS.get(this.server.getId()).removePlayer(this.player.getId());

                    this.sendMsg("You have left the Raid!");
                }
            }
        }
        else this.sendMsg(CommandInvalid.getShort());

        return this;
    }
}
