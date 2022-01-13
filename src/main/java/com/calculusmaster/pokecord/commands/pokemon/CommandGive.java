package com.calculusmaster.pokecord.commands.pokemon;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.commands.CommandInvalid;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.enums.items.Item;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
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
        if(this.insufficientMasteryLevel(Feature.GIVE_POKEMON_ITEMS)) return this.invalidMasteryLevel(Feature.GIVE_POKEMON_ITEMS);

        if(this.msg.length == 1)
        {
            this.embed.setDescription("Specify the item you want to give! Use p!inventory to check what items you have.");
            return this;
        }
        else if(!isNumeric(1) || this.playerData.getItemList().size() <= (Integer.parseInt(this.msg[1]) - 1))
        {
            this.embed.setDescription(CommandInvalid.getShort());
            return this;
        }

        Item item = Item.cast(this.playerData.getItemList().get(this.getInt(1) - 1));
        Pokemon s = this.playerData.getSelectedPokemon();

        if(item != null && !item.isFunctionalItem())
        {
            if(s.hasItem()) this.playerData.addItem(s.getItem().toString());
            s.setItem(item);

            this.playerData.removeItem(item.getName());
            s.updateItem();

            this.event.getChannel().sendMessage(this.playerData.getMention() + ": Gave " + s.getName() + " a `" + item.getStyledName() + "`!").queue();
            this.embed = null;
        }
        else if(item != null)
        {
            this.event.getChannel().sendMessage(this.playerData.getMention() + ": `" + item.getStyledName() + "` cannot be given to a Pokemon! Use p!activate instead!").queue();
            this.embed = null;
        }
        else
        {
            this.event.getChannel().sendMessage(this.playerData.getMention() + ": You don't have any `" + item.getStyledName() + "`!").queue();
            this.embed = null;
        }

        return this;
    }
}
