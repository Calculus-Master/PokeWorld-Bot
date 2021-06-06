package com.calculusmaster.pokecord.commands.pokemon;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.commands.CommandInvalid;
import com.calculusmaster.pokecord.game.Pokemon;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class CommandFavorites extends Command
{
    public CommandFavorites(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    @Override
    public Command runCommand()
    {
        //p!fav
        boolean view = this.msg.length == 1;
        //p!fav add number
        boolean add = this.msg.length == 3 && this.msg[1].equals("add") && this.isNumeric(2) && this.getInt(2) >= 1 && this.getInt(2) <= this.playerData.getPokemonList().size();
        //p!fav remove number
        boolean remove = this.msg.length == 3 && this.msg[1].equals("remove") && this.isNumeric(2) && this.getInt(2) >= 1 && this.getInt(2) <= this.playerData.getPokemonList().size();
        //p!fav clear
        boolean clear = this.msg.length == 2 && this.msg[1].equals("clear");

        List<String> favorites = this.playerData.getFavorites();

        if(view)
        {
            return new CommandPokemon(this.event, new String[]{"p!pokemon", "--fav"}).runCommand();
        }
        else if(add)
        {
            Pokemon p = Pokemon.build(this.playerData.getPokemonList().get(this.getInt(2) - 1));

            if(favorites.contains(p.getUUID()))
            {
                this.event.getChannel().sendMessage(this.playerData.getMention() + ": " + p.getName() + " is already in your favorites!").queue();
                this.embed = null;
                return this;
            }

            this.playerData.addPokemonToFavorites(p.getUUID());

            this.event.getChannel().sendMessage(this.playerData.getMention() + ": Added **Level " + p.getLevel() + " " + p.getName() + "** to your favorites!").queue();
            this.embed = null;
            return this;
        }
        else if(remove)
        {
            Pokemon p = Pokemon.build(this.playerData.getPokemonList().get(this.getInt(2) - 1));

            if(!favorites.contains(p.getUUID()))
            {
                this.event.getChannel().sendMessage(this.playerData.getMention() + ": " + p.getName() + " is not in your favorites!").queue();
                this.embed = null;
                return this;
            }

            this.playerData.removePokemonFromFavorites(p.getUUID());

            this.event.getChannel().sendMessage(this.playerData.getMention() + ": Removed **Level " + p.getLevel() + " " + p.getName() + "** from your favorites!").queue();
            this.embed = null;
            return this;
        }
        else if(clear)
        {
            this.playerData.clearFavorites();

            this.event.getChannel().sendMessage(this.playerData.getMention() + ": Your Favorites list was successfully cleared!").queue();
            this.embed = null;
            return this;
        }
        else this.embed.setDescription(CommandInvalid.getShort());

        return this;
    }
}
