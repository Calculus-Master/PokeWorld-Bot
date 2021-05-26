package com.calculusmaster.pokecord.commands.pokemon;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.commands.CommandInvalid;
import com.calculusmaster.pokecord.game.Pokemon;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandTeam extends Command
{
    public CommandTeam(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    public static final int MAX_TEAM_SIZE = 12;

    @Override
    public Command runCommand()
    {
        if(this.msg.length == 4 && this.msg[1].equals("set") && isNumeric(2) && isNumeric(3))
        {
            int teamIndex = this.getInt(2);
            int pokemonIndex = this.getInt(3);

            if((teamIndex < 1 || teamIndex > MAX_TEAM_SIZE) || (pokemonIndex < 1 || pokemonIndex > this.playerData.getPokemonList().length()))
            {
                this.embed.setDescription(CommandInvalid.getShort());
                return this;
            }

            String UUID = this.playerData.getPokemonList().getString(pokemonIndex - 1);

            if(this.playerData.isInTeam(UUID))
            {
                this.embed.setDescription("This Pokemon is already in your team!");
                return this;
            }

            this.playerData.addPokemonToTeam(UUID, teamIndex);

            Pokemon p = Pokemon.buildCore(UUID, pokemonIndex);
            this.embed.setDescription("Added " + p.getName() + " to your team!");
        }
        else
        {
            if(this.playerData.getTeam() == null)
            {
                this.embed.setDescription("You don't have any Pokemon in your team!");
                return this;
            }

            StringBuilder team = new StringBuilder();

            Pokemon p;
            for(int i = 0; i < MAX_TEAM_SIZE; i++)
            {
                team.append(i + 1).append(": ");

                if(i < this.playerData.getTeam().length())
                {
                    p = Pokemon.buildCore(this.playerData.getTeam().getString(i), -1);
                    team.append("Level ").append(p.getLevel()).append(" ").append(p.getName());
                }
                else team.append("None");

                team.append("\n");
            }

            this.embed.setDescription(team.toString());
            this.embed.setTitle(this.player.getName() + "'s Pokemon Team");
        }
        return this;
    }
}
