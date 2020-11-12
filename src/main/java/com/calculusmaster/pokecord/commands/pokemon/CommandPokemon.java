package com.calculusmaster.pokecord.commands.pokemon;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.commands.CommandInvalid;
import com.calculusmaster.pokecord.game.Pokemon;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

public class CommandPokemon extends Command
{
    List<Pokemon> pokemon = new ArrayList<>();
    public CommandPokemon(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg, "pokemon <flags>");
    }

    @Override
    public Command runCommand()
    {
        boolean noFlags = this.msg.length == 1 || (this.msg.length == 2 && this.msg[1].chars().allMatch(Character::isDigit));

        this.buildList();

        if(!noFlags)
        {
            this.embed.setDescription(CommandInvalid.getShort());
            return this;
        }
        else if(noFlags) this.runCommand_NoFlags();

        return this;
    }

    private void sortByNumber()
    {
        StringBuilder sb = new StringBuilder();
    }

    private void runCommand_NoFlags()
    {
        StringBuilder sb = new StringBuilder();
        boolean hasPage = this.msg.length == 2;
        int perPage = 20;
        int startIndex = hasPage ? (Integer.parseInt(this.msg[1]) > this.pokemon.size() ? 0 : Integer.parseInt(this.msg[1])) : 0;
        if(startIndex != 0) startIndex--;

        startIndex *= perPage;
        for(int i = startIndex; i < startIndex + perPage; i++)
        {
            if(i > this.pokemon.size() - 1) break;
            sb.append(this.getLine(this.pokemon.get(i)));
        }

        this.embed.setDescription(sb.toString());
        this.embed.setTitle(this.player.getName() + "'s Pokemon");
    }

    private void buildList()
    {
        for(int i = 0; i < this.playerData.getPokemonList().length(); i++) this.pokemon.add(Pokemon.buildCore(this.playerData.getPokemonList().getString(i)));
    }

    private String getLine(Pokemon p)
    {
        return "**" + p.getName() + "** | Number: " + (this.jsonIndexOf(p.getUUID()) + 1) + " | Level " + p.getLevel() + " | Total IV: " + p.getTotalIV() + "\n";
    }

    private int jsonIndexOf(String UUID)
    {
        for(int i = 0; i < this.playerData.getPokemonList().length(); i++) if(this.playerData.getPokemonList().getString(i).equals(UUID)) return i;
        return -1;
    }
}
