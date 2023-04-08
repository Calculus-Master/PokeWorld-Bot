package com.calculusmaster.pokecord.game.player;

import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class PlayerTeam
{
    public static int MAX_TEAM_SIZE;
    public static int MAX_SLOTS;

    private List<String> activeTeam;
    private final List<SavedTeam> savedTeams;

    public PlayerTeam()
    {
        this.activeTeam = new ArrayList<>();
        this.savedTeams = IntStream.range(0, MAX_SLOTS).mapToObj(i -> new SavedTeam(i + 1)).toList();
    }

    public PlayerTeam(Document data)
    {
        this.activeTeam = data.getList("active", String.class);
        this.savedTeams = new ArrayList<>();
        data.getList("saved", Document.class).forEach(d -> this.savedTeams.add(new SavedTeam(d)));
    }

    public Document serialize()
    {
        return new Document("active", this.activeTeam)
                .append("saved", this.savedTeams.stream().map(SavedTeam::serialize).toList());
    }

    //Active Team - Modifiers/Updaters
    public void add(String UUID)
    {
        this.activeTeam.add(UUID);
    }

    public void remove(String UUID)
    {
        this.activeTeam.remove(UUID);
    }

    public void remove(int index)
    {
        this.activeTeam.remove(index);
    }

    public void swap(int i1, int i2)
    {
        String temp = this.activeTeam.get(i1);
        this.activeTeam.set(i1, this.activeTeam.get(i2));
        this.activeTeam.set(i2, temp);
    }

    public void clear()
    {
        this.activeTeam.clear();
    }

    //Saved Team - Modifiers/Updaters
    public void save(int slot)
    {
        this.savedTeams.get(slot).setTeam(this.activeTeam);
    }

    public void load(int slot)
    {
        this.activeTeam = new ArrayList<>(this.savedTeams.get(slot).getTeam());
    }

    public void clear(int slot)
    {
        this.savedTeams.get(slot).setTeam(new ArrayList<>());
    }

    //Misc Accessors
    public boolean contains(String UUID)
    {
        return this.activeTeam.contains(UUID);
    }

    public boolean isMaxSize()
    {
        return this.activeTeam.size() == MAX_TEAM_SIZE;
    }

    public boolean isEmpty()
    {
        return this.activeTeam.isEmpty();
    }

    public boolean isSlotEmpty(int slot)
    {
        return this.savedTeams.get(slot).isEmpty();
    }

    public int size()
    {
        return this.activeTeam.size();
    }

    //Getters
    public void setSlotName(int slot, String name)
    {
        this.savedTeams.get(slot).setTeamName(name);
    }

    public List<String> getActiveTeam()
    {
        return this.activeTeam;
    }

    public List<Pokemon> getActiveTeamPokemon()
    {
        return this.activeTeam.stream().map(Pokemon::build).toList();
    }

    public SavedTeam getSavedTeam(int slot)
    {
        return this.savedTeams.get(slot);
    }

    public List<SavedTeam> getSavedTeams()
    {
        return this.savedTeams;
    }

    //Saved Team object
    public static class SavedTeam
    {
        private String teamName;
        private List<String> teamPokemon;

        SavedTeam(int slot)
        {
            this.teamName = "Unnamed Slot #" + slot;
            this.teamPokemon = new ArrayList<>();
        }

        SavedTeam(Document data)
        {
            this.teamName = data.getString("name");
            this.teamPokemon = data.getList("pokemon", String.class);
        }

        Document serialize()
        {
            return new Document("name", this.teamName).append("pokemon", this.teamPokemon);
        }

        void setTeamName(String name)
        {
            this.teamName = name;
        }

        void setTeam(List<String> team)
        {
            this.teamPokemon = new ArrayList<>(team);
        }

        public List<String> getTeam()
        {
            return this.teamPokemon;
        }

        public String getName()
        {
            return this.teamName;
        }

        public boolean isEmpty()
        {
            return this.teamPokemon.isEmpty();
        }
    }
}
