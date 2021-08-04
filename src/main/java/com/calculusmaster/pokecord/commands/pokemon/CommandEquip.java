package com.calculusmaster.pokecord.commands.pokemon;

import com.calculusmaster.pokecord.commands.Command;
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
        boolean equip = this.msg.length == 2 && this.isNumeric(1);

        if(equip)
        {
            int num = this.getInt(1);

            if(this.playerData.getZCrystalList().isEmpty()) this.sendMsg("You do not have any Z Crystals!");
            else if(num < 1 || num > this.playerData.getZCrystalList().size()) this.sendMsg("Invalid number!");
            else if(!this.serverData.canEquipZCrystalDuel() && DuelHelper.isInDuel(this.player.getId())) this.sendMsg("You can't equip Z Crystals while in a Duel!");
            else
            {
                String z = this.playerData.getZCrystalList().get(this.getInt(1) - 1);

                if(DuelHelper.isInDuel(this.player.getId()))
                    DuelHelper.instance(this.player.getId()).getPlayer(this.player.getId()).data.equipZCrystal(z);
                else this.playerData.equipZCrystal(z);

                this.playerData.grantAchievement(Achievements.EQUIP_FIRST_ZCRYSTAL, this.event);

                this.sendMsg("Equipped `" + z + "`!");
            }
        }

        return this;
    }
}
