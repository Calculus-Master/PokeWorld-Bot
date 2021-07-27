package com.calculusmaster.pokecord.commands.pokemon;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.commands.CommandInvalid;
import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.duel.core.DuelHelper;
import com.calculusmaster.pokecord.game.enums.functional.Achievements;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandEquip extends Command
{
    public CommandEquip(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        if(this.msg.length != 2 || !this.isNumeric(1) || this.playerData.getZCrystalList() == null)
        {
            this.embed.setDescription(CommandInvalid.getShort());
            return this;
        }
        else if(this.getInt(1) > this.playerData.getZCrystalList().size() || this.getInt(1) <= 0)
        {
            this.embed.setDescription(CommandInvalid.getShort());
            return this;
        }
        else if(!this.serverData.canEquipZCrystalDuel() && DuelHelper.isInDuel(this.player.getId()))
        {
            this.sendMsg("You can't equip Z Crystals while in a Duel!");
            return this;
        }

        String z = this.playerData.getZCrystalList().get(this.getInt(1) - 1);

        if(DuelHelper.isInDuel(this.player.getId()))
        {
            Duel d = DuelHelper.instance(this.player.getId());
            d.getPlayers()[d.indexOf(this.player.getId())].data.equipZCrystal(z);
        }
        else this.playerData.equipZCrystal(z);

        Achievements.grant(this.player.getId(), Achievements.EQUIP_FIRST_ZCRYSTAL, this.event);

        this.event.getChannel().sendMessage(this.playerData.getMention() + ": Equipped " + z + "!").queue();

        this.embed = null;
        return this;
    }
}
