package com.calculusmaster.pokecord.game.trade;

import com.calculusmaster.pokecord.game.enums.items.Item;
import com.calculusmaster.pokecord.game.enums.items.TM;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.mongo.PlayerDataQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TradeOffer
{
    private int credits;
    private int redeems;
    private List<String> pokemon;
    private Map<Item, Integer> items;
    private Map<TM, Integer> tms;

    public TradeOffer()
    {
        this.clear();
    }

    public void clear()
    {
        this.credits = 0;
        this.redeems = 0;
        this.pokemon = new ArrayList<>();
        this.items = new HashMap<>();
        this.tms = new HashMap<>();
    }

    public boolean isValid(PlayerDataQuery playerData)
    {
        if(this.hasCredits())
            if(playerData.getCredits() < this.credits) return false;

        if(this.hasRedeems())
            if(playerData.getRedeems() < this.redeems) return false;

        if(hasPokemon())
            for(String s : this.pokemon) if(!playerData.getPokemonList().contains(s)) return false;

        if(hasItems())
            for(Item item : this.items.keySet()) if(!playerData.getInventory().hasItem(item) || playerData.getInventory().getItems().get(item) < this.items.get(item)) return false;

        if(hasTMs())
            for(TM tm : this.tms.keySet()) if(!playerData.getInventory().hasTM(tm) || playerData.getInventory().getTMs().get(tm) < this.tms.get(tm)) return false;

        return true;
    }

    public void transfer(PlayerDataQuery source, PlayerDataQuery target)
    {
        if(this.credits > 0)
        {
            source.changeCredits(-this.credits);
            target.changeCredits(this.credits);
        }

        if(this.redeems > 0)
        {
            source.changeRedeems(-this.redeems);
            target.changeRedeems(this.redeems);
        }

        if(!this.pokemon.isEmpty())
        {
            for(String p : this.pokemon)
            {
                source.removePokemon(p);
                target.addPokemon(p);
            }
        }

        if(!this.items.isEmpty())
        {
            for(Map.Entry<Item, Integer> e : this.items.entrySet())
            {
                for(int i = 0; i < e.getValue(); i++) source.getInventory().removeItem(e.getKey());
                for(int i = 0; i < e.getValue(); i++) target.getInventory().addItem(e.getKey());
            }
        }

        if(!this.tms.isEmpty())
        {
            for(Map.Entry<TM, Integer> e : this.tms.entrySet())
            {
                for(int i = 0; i < e.getValue(); i++) source.getInventory().removeTM(e.getKey());
                for(int i = 0; i < e.getValue(); i++) target.getInventory().addTM(e.getKey());
            }
        }
    }

    public String getOverview()
    {
        List<String> contents = new ArrayList<>();

        if(this.credits > 0)
            contents.add("Credits: " + this.credits);

        if(this.redeems > 0)
            contents.add("Redeems: " + this.redeems);

        if(!this.pokemon.isEmpty())
            contents.add("Pokemon: " + this.pokemon.stream().map(Pokemon::build).map(p -> "Level " + p.getLevel() + " " + p.getName()).collect(Collectors.joining(", ")));

        if(!this.tms.isEmpty())
            contents.add("TMs: " + this.tms.entrySet().stream().map(e -> e.getKey() + " (" + e.getValue() + ")").collect(Collectors.joining(", ")));

        if(!this.items.isEmpty())
            contents.add("Items: " + this.items.entrySet().stream().map(e -> e.getKey().getStyledName() + " (" + e.getValue() + ")").collect(Collectors.joining(", ")));

        if(contents.isEmpty()) contents.add("\u200B");

        //Code Block formatting
        contents.add(0, "```");
        contents.add("```");

        return String.join("\n", contents);
    }

    //Accessors and Components

    public boolean isEmpty()
    {
        return !this.hasCredits() && !this.hasRedeems() && !this.hasPokemon() && !this.hasItems() && !this.hasTMs();
    }

    public void addCredits(int amount) { this.credits += amount; }
    public void removeCredits(int amount) { this.credits = Math.max(this.credits - amount, 0); }
    public int getCredits() { return this.credits; }
    public boolean hasCredits() { return this.credits > 0; }
    public void clearCredits() { this.credits = 0; }

    public void addRedeems(int amount) { this.redeems += amount; }
    public void removeRedeems(int amount) { this.redeems = Math.max(this.redeems - amount, 0); }
    public int getRedeems() { return this.redeems; }
    public boolean hasRedeems() { return this.redeems > 0; }
    public void clearRedeems() { this.redeems = 0; }

    public void addPokemon(String UUID) { this.pokemon.add(UUID); }
    public void removePokemon(String UUID) { this.pokemon.remove(UUID); }
    public List<String> getPokemon() { return this.pokemon; }
    public boolean hasPokemon() { return !this.pokemon.isEmpty(); }
    public void clearPokemon() { this.pokemon = new ArrayList<>(); }

    public void addItem(Item item, int amount) { this.items.put(item, this.items.getOrDefault(item, 0) + amount); }
    public void removeItem(Item item, int amount) { this.items.put(item, Math.max(this.items.getOrDefault(item, 0) - amount, 0)); }
    public Map<Item, Integer> getItems() { return this.items; }
    public boolean hasItems() { return !this.items.isEmpty(); }
    public void clearItems() { this.items = new HashMap<>(); }

    public void addTM(TM tm, int amount) { this.tms.put(tm, this.tms.getOrDefault(tm, 0) + amount); }
    public void removeTM(TM tm, int amount) { this.tms.put(tm, Math.max(this.tms.getOrDefault(tm, 0) - amount, 0)); }
    public Map<TM, Integer> getTMs() { return this.tms; }
    public boolean hasTMs() { return !this.tms.isEmpty(); }
    public void clearTMs() { this.tms = new HashMap<>(); }
}
