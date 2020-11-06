package com.calculusmaster.pokecord.commands.duel;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.commands.CommandInvalid;
import com.calculusmaster.pokecord.game.Duel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.IOException;

public class CommandUse extends Command
{
    public CommandUse(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg, "use <movenumber>");
    }

    @Override
    public Command runCommand() throws IOException
    {
        if(!Duel.isInDuel(this.player.getId()) || !Duel.getInstance(this.player.getId()).getStatus().equals(Duel.DuelStatus.DUELING) || this.msg.length != 2 || !this.msg[1].chars().allMatch(Character::isDigit))
        {
            this.embed.setDescription(CommandInvalid.getShort());
            return this;
        }
        else
        {
            Duel duel = Duel.getInstance(this.player.getId());
            System.out.println(duel.getTurnID());

            if(!duel.getTurnID().equals(this.player.getId()))
            {
                this.embed.setDescription("It's not your turn!");
                return this;
            }

            if(!duel.isComplete())
            {
                String results = duel.doTurn(Integer.parseInt(this.msg[1]));
                if(duel.getTurnID().equals(this.player.getId())) duel.swapTurns();
                duel.sendGenericTurnEmbed(this.event, results);
                this.embed = null;
            }
            if(duel.isComplete())
            {
                duel.onWin();
                duel.sendWinEmbed(this.event);
                duel.giveWinExp();
                duel.giveWinCredits();
                Duel.remove(this.player.getId());
                this.embed = null;
            }
        }
        return this;
    }
}
