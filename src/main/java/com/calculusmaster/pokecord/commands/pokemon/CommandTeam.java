package com.calculusmaster.pokecord.commands.pokemon;

import com.calculusmaster.pokecord.commands.Command;
import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.duel.core.DuelHelper;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.pokemon.PokemonRarity;
import com.calculusmaster.pokecord.game.tournament.Tournament;
import com.calculusmaster.pokecord.game.tournament.TournamentHelper;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class CommandTeam extends Command
{
    public CommandTeam(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    public static int MAX_TEAM_SIZE;

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
        //p!team clear
        boolean clear = this.msg.length == 2 && this.msg[1].equals("clear");

        if(TournamentHelper.isInTournament(this.player.getId()))
        {
            Tournament t = TournamentHelper.instance(this.player.getId());

            if(t.getStatus().equals(TournamentHelper.TournamentStatus.DUELING) && !t.isPlayerEliminated(this.player.getId()))
            {
                this.response = "You can't change your team while in a Tournament!";
                return this;
            }
        }

        if(set || add)
        {
            int teamIndex = add ? MAX_TEAM_SIZE : this.getInt(2);
            int pokemonIndex = this.getInt(add ? 2 : 3);

            if((teamIndex < 1 || (set && teamIndex > MAX_TEAM_SIZE)) || (pokemonIndex < 1 || pokemonIndex > this.playerData.getPokemonList().size()))
            {
                return this.invalid();
            }
            else if(add && this.playerData.getTeam().size() == MAX_TEAM_SIZE)
            {
                this.response = "Your team is full! Use p!team set to change certain slots!";
                return this;
            }
            else
            {
                String UUID = this.playerData.getPokemonList().get(pokemonIndex - 1);

                if(this.playerData.getTeam().contains(UUID))
                {
                    this.response = "This Pokemon is already in your team!";
                    return this;
                }
                else
                {
                    this.playerData.addPokemonToTeam(UUID, teamIndex);

                    Pokemon p = Pokemon.build(UUID);
                    this.response = "Added " + p.getName() + " to your team!";
                }
            }
        }
        else if(remove)
        {
            int teamIndex = this.getInt(2);

            if(teamIndex < 1 || teamIndex > MAX_TEAM_SIZE || teamIndex > this.playerData.getTeam().size())
            {
                return this.invalid();
            }
            else
            {
                Pokemon p = Pokemon.build(this.playerData.getTeam().get(teamIndex - 1));

                this.playerData.removePokemonFromTeam(teamIndex);

                this.response = "Removed " + p.getName() + " from your team!";
            }
        }
        else if(swap)
        {
            int fromIndex = this.getInt(2);
            int toIndex = this.getInt(3);

            if(fromIndex < 1 || fromIndex > this.playerData.getTeam().size() || toIndex < 1 || toIndex > this.playerData.getTeam().size())
            {
                return this.invalid();
            }
            else
            {
                this.playerData.swapPokemonInTeam(fromIndex, toIndex);

                this.response = "Swapped pokemon number " + fromIndex + " and " + toIndex + " in your team!";
            }
        }
        else if(clear)
        {
            this.playerData.clearTeam();

            this.response = this.playerData.getMention() + ": Your team was successfully cleared!";
        }
        else
        {
            if(this.playerData.getTeam().isEmpty())
            {
                this.response = "You don't have any Pokemon in your team! Add Pokemon using `p!team add <number>`!";
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
                List<String> teamUUIDs = List.copyOf(this.playerData.getTeam());

                Pokemon p;
                for(int i = 0; i < MAX_TEAM_SIZE; i++)
                {
                    team.append(i + 1).append(": ");

                    if(i < teamUUIDs.size())
                    {
                        p = Pokemon.build(teamUUIDs.get(i));
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
        if(PokemonRarity.LEGENDARY.contains(name)) return " (L)";
        else if(PokemonRarity.MYTHICAL.contains(name)) return " (M)";
        else if(PokemonRarity.ULTRA_BEAST.contains(name)) return " (UB)";
        else if(name.contains("Mega") || name.contains("Primal")) return " (M|P)";
        else return "";
    }
}
