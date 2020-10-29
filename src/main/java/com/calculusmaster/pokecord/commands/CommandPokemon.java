package com.calculusmaster.pokecord.commands;

import com.calculusmaster.pokecord.game.Pokemon;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.json.JSONArray;

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

    private void runCommand_NoFlags()
    {
        StringBuilder sb = new StringBuilder();
        boolean hasPage = this.msg.length == 2;
        Pokemon p;
        JSONArray list = this.playerData.getPokemonList();
        int startIndex = hasPage ? (Integer.parseInt(this.msg[1]) > list.length() ? 0 : Integer.parseInt(this.msg[1])) : 0;

        for(int i = 0; i < startIndex + 20; i++)
        {
            if(i > list.length() - 1) break;
            p = Pokemon.build(list.getString(i));
            sb.append("**" + p.getName() + "** | Number: " + (i + 1) + " | Level " + p.getLevel() + " | Total IV: " + p.getTotalIV() + "\n");
        }

        this.embed.setDescription(sb.toString());
        this.embed.setTitle(this.player.getName() + "'s Pokemon");
    }
}
