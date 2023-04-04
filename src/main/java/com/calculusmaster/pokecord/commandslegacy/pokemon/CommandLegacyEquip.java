package com.calculusmaster.pokecord.commandslegacy.pokemon;

import com.calculusmaster.pokecord.commandslegacy.CommandLegacy;
import com.calculusmaster.pokecord.game.duel.core.DuelHelper;
import com.calculusmaster.pokecord.game.duel.players.UserPlayer;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.enums.functional.Achievements;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;

public class CommandLegacyEquip extends CommandLegacy
{
    public CommandLegacyEquip(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public CommandLegacy runCommand()
    {
        if(this.insufficientMasteryLevel(Feature.EQUIP_Z_CRYSTALS)) return this.invalidMasteryLevel(Feature.EQUIP_Z_CRYSTALS);

        boolean equip = this.msg.length == 2 && this.isNumeric(1);

        if(equip)
        {
            int num = this.getInt(1);

            if(this.playerData.getInventory().getZCrystals().isEmpty()) this.response = "You do not have any Z Crystals!";
            else if(num < 1 || num > this.playerData.getInventory().getZCrystals().size()) this.response = "Invalid number!";
            else if(!this.serverData.canEquipZCrystalDuel() && DuelHelper.isInDuel(this.player.getId())) this.response = "You can't equip Z Crystals while in a Duel!";
            else
            {
                String z = new ArrayList<>(this.playerData.getInventory().getZCrystals()).get(this.getInt(1) - 1).toString();

                if(DuelHelper.isInDuel(this.player.getId()))
                    ((UserPlayer)(DuelHelper.instance(this.player.getId()).getPlayer(this.player.getId()))).data.equipZCrystal(z);
                else this.playerData.equipZCrystal(z);

                this.playerData.grantAchievement(Achievements.EQUIP_FIRST_ZCRYSTAL, this.event);

                this.response = "Equipped `" + z + "`!";
            }
        }

        return this;
    }
}
