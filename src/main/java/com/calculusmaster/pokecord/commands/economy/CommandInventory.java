package com.calculusmaster.pokecord.commands.economy;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.game.enums.items.PokeItem;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandInventory extends Command
{
    public CommandInventory(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        StringBuilder s = new StringBuilder();

        s.append("`Items`: \n");
        if(this.playerData.getItemList() != null)
        {
            for(int i = 0; i < this.playerData.getItemList().size(); i++) s.append(i + 1).append(": ").append(PokeItem.asItem(this.playerData.getItemList().get(i)).getStyledName()).append("\n");
            s.append("\n");
        }
        else s.append("You don't own any Items!\n\n");

        s.append("`Z-Crystals`: \n");
        if(this.playerData.getZCrystalList() != null)
        {
            for(int i = 0; i < this.playerData.getZCrystalList().size(); i++) s.append(i + 1).append(": ").append(this.playerData.getZCrystalList().get(i)).append("\n");
            s.append("\n");
            s.append("Equipped: ").append(this.playerData.getEquippedZCrystal() != null && !this.playerData.getEquippedZCrystal().isEmpty() ? this.playerData.getEquippedZCrystal() : "None");
            s.append("\n\n");
        }
        else s.append("You don't own any Z-Crystals!\n\n");
        
        s.append("`TMs`: \n");
        if(this.playerData.getTeam() != null)
        {
            for(int i = 0; i < this.playerData.getTMList().size(); i++) s.append(this.playerData.getTMList().get(i)).append(", ");
            s.delete(s.length() - 2, s.length()).append("\n\n");
        }
        else s.append("You don't own any Technical Machines (TMs)!\n\n");

        s.append("`TRs`: \n");
        if(this.playerData.getTRList() != null)
        {
            for (int i = 0; i < this.playerData.getTRList().size(); i++) s.append(this.playerData.getTRList().get(i)).append(", ");
            s.delete(s.length() - 2, s.length()).append("\n\n");
        }
        else s.append("You don't own any Technical Records (TRs)!\n\n");


        this.embed.setDescription(s.toString());
        this.embed.setTitle(this.player.getName() + "'s Inventory");
        return this;
    }
}
