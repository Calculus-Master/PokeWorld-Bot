package com.calculusmaster.pokecord.commands.duel;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.commands.CommandInvalid;
import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.duel.core.DuelChecks;
import com.calculusmaster.pokecord.game.duel.core.DuelHelper;
import com.calculusmaster.pokecord.game.duel.players.Player;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.util.enums.PlayerStatistic;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import static com.calculusmaster.pokecord.game.duel.core.DuelChecks.CheckType.*;

public class CommandUse extends Command
{
    public CommandUse(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg, true);
    }

    public CommandUse(ButtonClickEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        if(this.insufficientMasteryLevel(Feature.USE_MOVES)) return this.invalidMasteryLevel(Feature.USE_MOVES);

        if(this.msg.length == 1)
        {
            this.embed.setDescription(CommandInvalid.getShort());
            return this;
        }

        this.embed = null;

        if(!DuelHelper.isInDuel(this.player.getId()))
        {
            this.response = "You are not in a duel!";
            return this;
        }

        final Duel d = DuelHelper.instance(this.player.getId());
        final DuelChecks c = new DuelChecks(this.player.getId());
        final Player p = d.getPlayers()[d.indexOf(this.player.getId())];

        boolean normal = this.msg.length == 2 && this.isNumeric(1) && this.getInt(1) > 0 && this.getInt(1) < 5;
        boolean swap = this.msg.length == 3 && (this.msg[1].equals("swap") || this.msg[1].equals("s")) && this.isNumeric(2) && this.getInt(2) > 0 && this.getInt(2) <= d.getPlayer(this.player.getId()).team.size();
        boolean zmove = this.msg.length == 3 && (this.msg[1].equals("zmove") || this.msg[1].equals("z")) && isNumeric(2) && this.getInt(2) > 0 && this.getInt(2) < 5;
        boolean dynamax = this.msg.length == 3 && (this.msg[1].equals("dynamax") || this.msg[1].equals("d")) && isNumeric(2) && this.getInt(2) > 0 && this.getInt(2) < 5;

        if(!normal && !swap && !zmove && !dynamax)
        {
            this.response = "Invalid Turn Action! Valid formats are: `p!use <num>`, `p!use <s:swap> <index>`, `p!use <z:zmove> <num>`, and `p!use <d:dynamax> <num>`, where `num` is the move number and `index` is the index of the Pokemon in your team";
            return this;
        }

        if(zmove && this.insufficientMasteryLevel(Feature.USE_Z_MOVES)) return this.invalidMasteryLevel(Feature.USE_Z_MOVES);
        if(dynamax && this.insufficientMasteryLevel(Feature.DYNAMAX_POKEMON)) return this.invalidMasteryLevel(Feature.DYNAMAX_POKEMON);

        if(c.checkFailed(NORMAL_MOVESUBMITTED))
        {
            this.response = NORMAL_MOVESUBMITTED.getInvalidMessage();
            return this;
        }
        else if(c.checkFailed(NORMAL_FAINTED) && !this.msg[1].equals("swap") && !d.isComplete())
        {
            this.response = NORMAL_FAINTED.getInvalidMessage();
            return this;
        }

        //Normal Move
        if(normal)
        {
            this.playerData.getStatistics().incr(PlayerStatistic.MOVES_USED);

            d.submitMove(this.player.getId(), this.getInt(1), 'm');
            this.deleteOriginal();
        }
        else if(c.checkFailed(NORMAL_WILDDUEL))
        {
            this.response = NORMAL_WILDDUEL.getInvalidMessage();
            return this;
        }

        //Swap
        if(swap)
        {
            if(c.checkFailed(SWAP_ISABLE))
            {
                this.response = SWAP_ISABLE.getInvalidMessage();
                return this;
            }

            if(c.checkFailed(SWAP_BOUND))
            {
                this.response = SWAP_BOUND.getInvalidMessage();
                return this;
            }

            if(c.checkFailed(SWAP_DYNAMAXED))
            {
                this.response = SWAP_DYNAMAXED.getInvalidMessage();
                return this;
            }

            d.submitMove(this.player.getId(), this.getInt(2), 's');
            this.deleteOriginal();
        }

        //Z Move
        if(zmove)
        {
            if(!this.serverData.areZMovesEnabled())
            {
                this.response = "Z-Moves are not enabled in this server!";
                return this;
            }

            if(c.checkFailed(ZMOVE_CRYSTAL))
            {
                this.response = ZMOVE_CRYSTAL.getInvalidMessage();
                return this;
            }

            if(c.checkFailed(ZMOVE_USED))
            {
                this.response = ZMOVE_USED.getInvalidMessage();
                return this;
            }

            if(c.checkFailed(ZMOVE_DYNAMAXED))
            {
                this.response = ZMOVE_DYNAMAXED.getInvalidMessage();
                return this;
            }

            c.setMove(p.active.getMove(this.getInt(2) - 1));

            if(c.checkFailed(ZMOVE_MOVE))
            {
                this.response = ZMOVE_MOVE.getInvalidMessage();
                return this;
            }

            this.playerData.getStatistics().incr(PlayerStatistic.MOVES_USED);
            this.playerData.getStatistics().incr(PlayerStatistic.ZMOVES_USED);

            d.submitMove(this.player.getId(), this.getInt(2), 'z');
            this.deleteOriginal();
        }

        //Dynamax
        if(dynamax)
        {
            if(!this.serverData.isDynamaxEnabled())
            {
                this.response = "Dynamaxing is not enabled in this server!";
                return this;
            }

            if(c.checkFailed(DYNAMAX_USED))
            {
                this.response = DYNAMAX_USED.getInvalidMessage();
                return this;
            }

            if(c.checkFailed(DYNAMAX_MEGA))
            {
                this.response = DYNAMAX_MEGA.getInvalidMessage();
                return this;
            }

            if(c.checkFailed(DYNAMAX_BANLIST))
            {
                this.response = DYNAMAX_BANLIST.getInvalidMessage();
                return this;
            }

            this.playerData.getStatistics().incr(PlayerStatistic.MOVES_USED);
            this.playerData.getStatistics().incr(PlayerStatistic.MAX_MOVES_USED);

            d.submitMove(this.player.getId(), this.getInt(2), 'd');
            this.deleteOriginal();
        }

        d.checkReady();

        return this;
    }
}
