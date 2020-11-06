package com.calculusmaster.pokecord.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandInventory extends Command
{
    public CommandInventory(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg, "inventory <page>");
    }

    @Override
    public Command runCommand()
    {
        StringBuilder s = new StringBuilder();

        //TODO: Items like Magmarizer, etc
        s.append("`Items (NYI)`: \n");
        
        s.append("`TMs`: \n");
        if(this.playerData.getOwnedTMs() != null)
        {
            for(int i = 0; i < this.playerData.getOwnedTMs().length(); i++) s.append(this.playerData.getOwnedTMs().getString(i)).append(", ");
            s.delete(s.length() - 2, s.length()).append("\n\n");
        }
        else s.append("You don't own any Technical Machines!\n\n");

        s.append("`TRs`: \n");
        if(this.playerData.getOwnedTRs() != null)
        {
            for (int i = 0; i < this.playerData.getOwnedTRs().length(); i++) s.append(this.playerData.getOwnedTRs().getString(i)).append(", ");
            s.delete(s.length() - 2, s.length()).append("\n\n");
        }
        else s.append("You don't own any Technical Records!\n\n");


        this.embed.setDescription(s.toString());
        this.embed.setTitle(this.player.getName() + "'s Inventory");
        return this;
    }
}
