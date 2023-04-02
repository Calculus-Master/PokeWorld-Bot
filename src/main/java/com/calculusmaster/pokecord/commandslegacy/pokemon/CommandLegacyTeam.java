package com.calculusmaster.pokecord.commandslegacy.pokemon;

import com.calculusmaster.pokecord.commandslegacy.CommandLegacy;
import com.calculusmaster.pokecord.game.duel.Duel;
import com.calculusmaster.pokecord.game.duel.core.DuelHelper;
import com.calculusmaster.pokecord.game.duel.tournament.Tournament;
import com.calculusmaster.pokecord.game.duel.tournament.TournamentHelper;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.enums.elements.Stat;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonEntity;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonRarity;
import com.calculusmaster.pokecord.game.pokemon.evolution.MegaEvolutionRegistry;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CommandLegacyTeam extends CommandLegacy
{
    public CommandLegacyTeam(MessageReceivedEvent event, String[] msg)
    {
        super(event, msg);
    }

    public static int MAX_TEAM_SIZE;
    public static int MAX_SLOTS;

    @Override
    public CommandLegacy runCommand()
    {
        if(this.insufficientMasteryLevel(Feature.CREATE_POKEMON_TEAMS)) return this.invalidMasteryLevel(Feature.CREATE_POKEMON_TEAMS);

        //p!team set <index> <number>
        boolean set = this.msg.length == 4 && this.msg[1].equals("set") && isNumeric(2) && isNumeric(3);
        //p!team add <number>
        boolean add = this.msg.length == 3 && this.msg[1].equals("add") && isNumeric(2);
        //p!team remove <index>
        boolean remove = this.msg.length == 3 && this.msg[1].equals("remove") && isNumeric(2);
        //p!team swap <index> <index>
        boolean swap = this.msg.length == 4 && this.msg[1].equals("swap") && isNumeric(2) && isNumeric(3);
        //p!team clear
        boolean clear = this.msg.length == 2 && this.msg[1].equals("clear");

        //Save Slots

        //p!team save <num>
        boolean save = this.msg.length == 3 && this.msg[1].equals("save") && isNumeric(2);
        //p!team load <num>
        boolean load = this.msg.length == 3 && this.msg[1].equals("load") && isNumeric(2);
        //p!team reset <num>
        boolean reset = this.msg.length == 3 && this.msg[1].equals("reset") && isNumeric(2);
        //p!team rename <num> <name+>
        boolean rename = this.msg.length >= 4 && this.msg[1].equals("rename") && isNumeric(2);
        //p!team saved
        boolean saved = this.msg.length == 2 && this.msg[1].equals("saved");

        if(TournamentHelper.isInTournament(this.player.getId()))
        {
            Tournament t = TournamentHelper.instance(this.player.getId());

            if(t.getStatus().equals(TournamentHelper.TournamentStatus.DUELING) && !t.isPlayerEliminated(this.player.getId()))
            {
                this.response = "You can't change your team while in a Tournament!";
                return this;
            }
        }
        else if(DuelHelper.isInDuel(this.player.getId()))
        {
            this.response = "You cannot edit your team while in a Duel!";
            return this;
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
        else if(save)
        {
            int slot = this.getInt(2) - 1;

            if(slot < 0 && slot >= MAX_SLOTS) this.response = "Invalid slot number!";
            else if(this.playerData.getTeam().isEmpty()) this.response = "Your team is empty!";
            else
            {
                List<String> currentTeam = this.playerData.getTeam();
                this.playerData.setSavedTeam(slot, currentTeam);

                this.response = "Saved your current team to slot " + (slot + 1) + "!";
            }
        }
        else if(load)
        {
            int slot = this.getInt(2) - 1;

            if(slot < 0 && slot >= MAX_SLOTS) this.response = "Invalid slot number!";
            else if(this.playerData.getSavedTeam(slot).isEmpty()) this.response = "The saved team in that slot is empty!";
            else
            {
                List<String> savedTeam = this.playerData.getSavedTeam(slot);
                this.playerData.setTeam(savedTeam);

                this.response = "Loaded your saved team from slot " + (slot + 1) + "!";
            }
        }
        else if(reset)
        {
            int slot = this.getInt(2) - 1;

            if(slot < 0 && slot >= MAX_SLOTS) this.response = "Invalid slot number!";
            else if(this.playerData.getSavedTeam(slot).isEmpty()) this.response = "The saved team in that slot is already empty!";
            else
            {
                this.playerData.setSavedTeam(slot, new ArrayList<>());

                this.response = "Cleared slot " + (slot + 1) + "!";
            }
        }
        else if(rename)
        {
            int slot = this.getInt(2) - 1;

            if(slot < 0 && slot >= MAX_SLOTS) this.response = "Invalid slot number!";
            else
            {
                String oldName = this.playerData.getSavedTeamName(slot);

                String[] raw = this.event.getMessage().getContentRaw().split("\\s+");
                String[] name = Arrays.copyOfRange(raw, 3, raw.length);
                String newName = String.join(" ", List.of(name));

                this.playerData.renameSavedTeam(slot, newName);

                this.response = "Renamed slot " + (slot + 1) + (!oldName.isEmpty() ? " from `" + oldName + "` " : " ") + "to `" + newName + "`!";
            }
        }
        else if(saved)
        {
            this.embed.setTitle("Saved Teams");
            for(int i = 0; i < MAX_SLOTS; i++)
            {
                String name = this.playerData.getSavedTeamName(i);
                if(name.isEmpty()) name = "UNNAMED";

                List<String> team = this.playerData.getSavedTeam(i);
                this.embed.addField("Slot " + (i + 1) + ": " + name, team.isEmpty() ? "Empty" : IntStream.range(0, team.size()).mapToObj(k -> (k + 1) + ": " + Pokemon.build(team.get(k)).getName()).collect(Collectors.joining("\n")), false);
            }
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
                        team.append("Level ").append(p.getLevel()).append(" ").append(p.getName()).append(this.getTag(p.getEntity()));
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

    private String getTag(PokemonEntity entity)
    {
        if(MegaEvolutionRegistry.isMegaLegendary(entity)) return " (ML)";
        else if(PokemonRarity.isLegendary(entity)) return " (L)";
        else if(PokemonRarity.isMythical(entity)) return " (Myth)";
        else if(PokemonRarity.isUltraBeast(entity)) return " (UB)";
        else if(MegaEvolutionRegistry.isMega(entity)) return " (Mega)";
        else return "";
    }
}
