package com.calculusmaster.pokecord.commands.pokemon;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.commands.CommandInvalid;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.enums.items.PokeItem;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandGive extends Command
{
    public CommandGive(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        if(this.msg.length == 1)
        {
            this.embed.setDescription("Specify the item you want to give! Use p!inventory to check what items you have.");
        }
        else if(!isNumeric(1) || this.playerData.getItemList().length() <= (Integer.parseInt(this.msg[1]) - 1))
        {
            this.embed.setDescription(CommandInvalid.getShort());
            return this;
        }

        String itemRaw = this.playerData.getItemList().getString(Integer.parseInt(this.msg[1]) - 1);
        PokeItem item = PokeItem.isItem(itemRaw) ? PokeItem.asItem(itemRaw) : null;
        Pokemon s = this.playerData.getSelectedPokemon();

        if(item != null && this.playerHasItem(item.getName()))
        {
            if(s.hasItem()) this.playerData.addItem(s.getItem());
            s.setItem(item);

            Pokemon.updateItem(s);

            this.embed.setDescription("Gave " + s.getName() + " a `" + item.getStyledName() + "`!");

            if(s.specialCanEvolve()) s.evolve();
        }
        else this.embed.setDescription(item == null ? "Invalid item name!" : "You don't have any `" + item.getStyledName() + "`!");

        return this;
    }

    public boolean playerHasItem(String name)
    {
        for(int i = 0; i < this.playerData.getItemList().length(); i++)
        {
            if(this.playerData.getItemList().getString(i).toLowerCase().equals(name.toLowerCase())) return true;
        }
        return false;
    }
}
