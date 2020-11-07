package com.calculusmaster.pokecord.commands.pokemon;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.commands.CommandInvalid;
import com.calculusmaster.pokecord.game.Pokemon;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

public class CommandPokemon extends Command
{
    public CommandPokemon(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg, "pokemon <flags>");
    }

    @Override
    public Command runCommand()
    {
        boolean noFlags = this.msg.length == 1 || (this.msg.length == 2 && this.msg[1].chars().allMatch(Character::isDigit));

        if(!noFlags)
        {
            this.embed.setDescription(CommandInvalid.getShort());
            return this;
        }
        else if(noFlags) this.runCommand_NoFlags();

        return this;
    }

    private List<String> getPlayerPokemon()
    {
        List<String> pokemon = new ArrayList<>();
        for(int i = 0; i < this.playerData.getPokemonList().length(); i++) pokemon.add(this.playerData.getPokemonList().getString(i));
        return pokemon;
    }

    private void runCommand_NoFlags()
    {
        System.out.println("STARTING P!POKEMON: " + System.currentTimeMillis());
        StringBuilder sb = new StringBuilder();
        boolean hasPage = this.msg.length == 2;
        Pokemon p;
        List<String> list = this.getPlayerPokemon();
        int startIndex = hasPage ? (Integer.parseInt(this.msg[1]) > list.size() ? 0 : Integer.parseInt(this.msg[1])) : 0;

        for(int i = 0; i < startIndex + 20; i++)
        {
            if(i > list.size() - 1) break;
            p = Pokemon.buildCore(list.get(i));
            sb.append("**" + p.getName() + "** | Number: " + (i + 1) + " | Level " + p.getLevel() + " | Total IV: " + p.getTotalIV() + "\n");
        }

        this.embed.setDescription(sb.toString());
        this.embed.setTitle(this.player.getName() + "'s Pokemon");
        System.out.println("FINISHED P!POKEMON: " + System.currentTimeMillis());
    }
}
