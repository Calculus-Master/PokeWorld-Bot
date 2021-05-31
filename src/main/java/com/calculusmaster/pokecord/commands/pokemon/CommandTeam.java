package com.calculusmaster.pokecord.commands.pokemon;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.commands.CommandInvalid;
import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.duel.DuelHelper;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.util.PokemonRarity;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;
import java.util.stream.Collectors;

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
        //p!team set index number
        boolean set = this.msg.length == 4 && this.msg[1].equals("set") && isNumeric(2) && isNumeric(3);
        //p!team add number
        boolean add = this.msg.length == 3 && this.msg[1].equals("add") && isNumeric(2);
        //p!team remove index
        boolean remove = this.msg.length == 3 && this.msg[1].equals("remove") && isNumeric(2);
        //p!team swap index index
        boolean swap = this.msg.length == 4 && this.msg[1].equals("swap") && isNumeric(2) && isNumeric(3);

        if(set || add)
        {
            int teamIndex = add ? MAX_TEAM_SIZE : this.getInt(2);
            int pokemonIndex = this.getInt(add ? 2 : 3);

            if((teamIndex < 1 || (set && teamIndex > MAX_TEAM_SIZE)) || (pokemonIndex < 1 || pokemonIndex > this.playerData.getPokemonList().size()))
            {
                this.embed.setDescription(CommandInvalid.getShort());
                return this;
            }
            else if(add && this.playerData.getTeam().size() == MAX_TEAM_SIZE)
            {
                this.embed.setDescription("Your team is full! Use p!team set to change certain slots!");
                return this;
            }

            String UUID = this.playerData.getPokemonList().get(pokemonIndex - 1);

            if(this.playerData.isInTeam(UUID))
            {
                this.embed.setDescription("This Pokemon is already in your team!");
                return this;
            }

            this.playerData.addPokemonToTeam(UUID, teamIndex);

            Pokemon p = Pokemon.buildCore(UUID, pokemonIndex);
            this.embed.setDescription("Added " + p.getName() + " to your team!");
        }
        else if(remove)
        {
            int teamIndex = this.getInt(2);

            if(teamIndex < 1 || teamIndex > MAX_TEAM_SIZE || teamIndex > this.playerData.getTeam().size())
            {
                this.embed.setDescription(CommandInvalid.getShort());
                return this;
            }

            Pokemon p = Pokemon.buildCore(this.playerData.getTeam().get(teamIndex - 1), -1);

            this.playerData.removePokemonFromTeam(teamIndex);

            this.embed.setDescription("Removed " + p.getName() + " from your team!");
        }
        else if(swap)
        {
            int fromIndex = this.getInt(2);
            int toIndex = this.getInt(3);

            if(fromIndex < 1 || fromIndex > this.playerData.getTeam().size() || toIndex < 1 || toIndex > this.playerData.getTeam().size())
            {
                this.embed.setDescription(CommandInvalid.getShort());
                return this;
            }

            this.playerData.swapPokemonInTeam(fromIndex, toIndex);

            this.embed.setDescription("Swapped pokemon number " + fromIndex + " and " + toIndex + " in your team!");
        }
        else
        {
            if(this.playerData.getTeam() == null)
            {
                this.embed.setDescription("You don't have any Pokemon in your team!");
                return this;
            }

            StringBuilder team = new StringBuilder();

            if(DuelHelper.isInDuel(this.player.getId()))
            {
                Duel d = DuelHelper.instance(this.player.getId());
                List<Pokemon> teamPokemon = d.getPlayers()[d.indexOf(this.player.getId())].team;

                for(int i = 0; i < teamPokemon.size(); i++)
                {
                    team.append(i + 1).append(": ").append(teamPokemon.get(i).getName()).append(teamPokemon.get(i).isFainted() ? " (Fainted)" : " (" + teamPokemon.get(i).getHealth() + " / " + teamPokemon.get(i).getStat(Stat.HP) + " HP)").append("\n");
                }
            }
            else
            {
                Pokemon p;
                for(int i = 0; i < MAX_TEAM_SIZE; i++)
                {
                    team.append(i + 1).append(": ");

                    if(i < this.playerData.getTeam().size())
                    {
                        p = Pokemon.buildCore(this.playerData.getTeam().get(i), -1);
                        team.append("Level ").append(p.getLevel()).append(" ").append(p.getName()).append(this.getTag(p.getName()));
                    }
                    else team.append("None");

                    team.append("\n");
                }
            }

            this.embed.setDescription(team.toString());
            this.embed.setTitle(this.player.getName() + "'s Pokemon Team");
        }
        return this;
    }

    private String getTag(String name)
    {
        if(DuelHelper.isInDuel(this.player.getId()))
        {
            Duel d = DuelHelper.instance(this.player.getId());
            List<Pokemon> team = d.getPlayers()[d.indexOf(this.player.getId())].team;
            if(team.stream().filter(p -> p.getName().equals(name)).collect(Collectors.toList()).get(0).isFainted()) return " (Fainted)";
        }

        if(PokemonRarity.LEGENDARY.contains(name)) return " (L)";
        if(PokemonRarity.MYTHICAL.contains(name)) return " (MY)";
        if(PokemonRarity.ULTRA_BEAST.contains(name)) return " (UB)";
        if(PokemonRarity.MEGA.contains(name)) return " (M)";

        return "";
    }
}
