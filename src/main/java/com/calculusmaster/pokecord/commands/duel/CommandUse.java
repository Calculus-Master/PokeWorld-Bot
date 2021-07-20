package com.calculusmaster.pokecord.commands.duel;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.commands.CommandInvalid;
import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.duel.DuelChecks;
import com.calculusmaster.pokecord.game.duel.DuelHelper;
import com.calculusmaster.pokecord.game.duel.elements.Player;
import com.calculusmaster.pokecord.game.moves.Move;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import static com.calculusmaster.pokecord.game.duel.DuelChecks.CheckType.*;

public class CommandUse extends Command
{
    public CommandUse(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        if(this.msg.length == 1)
        {
            this.embed.setDescription(CommandInvalid.getShort());
            return this;
        }

        this.embed = null;

        if(!DuelHelper.isInDuel(this.player.getId()))
        {
            this.sendMsg("You are not in a duel!");
            return this;
        }

        final Duel d = DuelHelper.instance(this.player.getId());
        final DuelChecks c = new DuelChecks(this.player.getId());
        final Player p = d.getPlayers()[d.indexOf(this.player.getId())];

        boolean formatNormal = this.msg.length == 2 && this.isNumeric(1) && this.getInt(1) > 0 && this.getInt(1) < 5;
        boolean formatSwap = this.msg.length == 3 && (this.msg[1].equals("swap") || this.msg[1].equals("s")) && this.isNumeric(2) && this.getInt(2) > 0 && this.getInt(2) <= d.getSize();
        boolean formatZMove = this.msg.length == 3 && (this.msg[1].equals("zmove") || this.msg[1].equals("z")) && isNumeric(2) && this.getInt(2) > 0 && this.getInt(2) < 5;
        boolean formatDynamax = this.msg.length == 3 && (this.msg[1].equals("dynamax") || this.msg[1].equals("d")) && isNumeric(2) && this.getInt(2) > 0 && this.getInt(2) < 5;

        if(!formatNormal && !formatSwap && !formatZMove && !formatDynamax)
        {
            this.sendMsg("Invalid Turn Action! Valid formats are: `p!use <num>`, `p!use <s:swap> <index>`, `p!use <z:zmove> <num>`, and `p!use <d:dynamax> <num>`, where `num` is the move number and `index` is the index of the Pokemon in your team");
            return this;
        }

        if(c.checkFailed(NORMAL_MOVESUBMITTED))
        {
            this.sendMsg(NORMAL_MOVESUBMITTED.getInvalidMessage());
            return this;
        }
        else if(c.checkFailed(NORMAL_FAINTED) && !this.msg[1].equals("swap") && !d.isComplete())
        {
            this.sendMsg(NORMAL_FAINTED.getInvalidMessage());
            return this;
        }

        //Normal Move
        if(formatNormal)
        {
            d.submitMove(this.player.getId(), this.getInt(1), 'm');
            this.event.getMessage().delete().queue();
        }
        else if(c.checkFailed(NORMAL_WILDDUEL))
        {
            this.sendMsg(NORMAL_WILDDUEL.getInvalidMessage());
            return this;
        }

        //Swap
        if(formatSwap)
        {
            if(c.checkFailed(SWAP_ISABLE))
            {
                this.sendMsg(SWAP_ISABLE.getInvalidMessage());
                return this;
            }

            if(c.checkFailed(SWAP_BOUND))
            {
                this.sendMsg(SWAP_BOUND.getInvalidMessage());
                return this;
            }

            if(c.checkFailed(SWAP_DYNAMAXED))
            {
                this.sendMsg(SWAP_DYNAMAXED.getInvalidMessage());
                return this;
            }

            d.submitMove(this.player.getId(), this.getInt(2), 's');
            this.event.getMessage().delete().queue();
        }

        //Z Move
        if(formatZMove)
        {
            if(!this.serverData.areZMovesEnabled())
            {
                this.sendMsg("Z-Moves are not enabled in this server!");
                return this;
            }

            if(c.checkFailed(ZMOVE_CRYSTAL))
            {
                this.sendMsg(ZMOVE_CRYSTAL.getInvalidMessage());
                return this;
            }

            if(c.checkFailed(ZMOVE_USED))
            {
                this.sendMsg(ZMOVE_USED.getInvalidMessage());
                return this;
            }

            if(c.checkFailed(ZMOVE_DYNAMAXED))
            {
                this.sendMsg(ZMOVE_DYNAMAXED.getInvalidMessage());
                return this;
            }

            c.setMove(new Move(p.active.getLearnedMoves().get(this.getInt(2) - 1)));

            if(c.checkFailed(ZMOVE_MOVE))
            {
                this.sendMsg(ZMOVE_MOVE.getInvalidMessage());
                return this;
            }

            d.submitMove(this.player.getId(), this.getInt(2), 'z');
            this.event.getMessage().delete().queue();
        }

        //Dynamax
        if(formatDynamax)
        {
            if(!this.serverData.isDynamaxEnabled())
            {
                this.sendMsg("Dynamaxing is not enabled in this server!");
                return this;
            }

            if(c.checkFailed(DYNAMAX_USED))
            {
                this.sendMsg(DYNAMAX_USED.getInvalidMessage());
                return this;
            }

            if(c.checkFailed(DYNAMAX_MEGA))
            {
                this.sendMsg(DYNAMAX_MEGA.getInvalidMessage());
                return this;
            }

            if(c.checkFailed(DYNAMAX_BANLIST))
            {
                this.sendMsg(DYNAMAX_BANLIST.getInvalidMessage());
                return this;
            }

            d.submitMove(this.player.getId(), this.getInt(2), 'd');
            this.event.getMessage().delete().queue();
        }

        d.checkReady();

        return this;
    }
}
