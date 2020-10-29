package com.calculusmaster.pokecord.mongo;

import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.util.Mongo;
import com.mongodb.client.model.Updates;
import net.dv8tion.jda.api.entities.User;
import org.bson.Document;
import org.json.JSONArray;

public class PlayerDataQuery extends MongoQuery
{
    public PlayerDataQuery(String playerID)
    {
        super("playerID", playerID, Mongo.PlayerData);
    }

    //Core

    public static boolean isRegistered(User player)
    {
        return PlayerDataQuery.isRegistered(player.getId());
    }

    public static boolean isRegistered(String id)
    {
        return !new PlayerDataQuery(id).isNull();
    }

    public static void register(User player)
    {
        Document playerData = new Document()
                .append("playerID", player.getId())
                .append("username", player.getName())
                .append("credits", 1000)
                .append("selected", 1);

        Mongo.PlayerData.insertOne(playerData);
    }

    private void update()
    {
        this.document = Mongo.PlayerData.find(this.query).first();
    }

    //Gets

    public String getUsername()
    {
        return this.json().getString("username");
    }

    public int getCredits()
    {
        return this.json().getInt("credits");
    }

    public JSONArray getPokemonList()
    {
        return !this.json().has("pokemon") ? null : this.json().getJSONArray("pokemon");
    }

    public int getSelected()
    {
        return this.json().getInt("selected") - 1;
    }

    public Pokemon getSelectedPokemon()
    {
        return Pokemon.build(this.getPokemonList().getString(this.getSelected()));
    }

    //Updates - run this.update() after each method so the query is up to date

    public void changeCredits(int amount)
    {
        Mongo.PlayerData.updateOne(this.query, Updates.set("credits", this.json().getInt("credits") + amount));

        this.update();
    }

    //This is to add a pokemon to the player's list, assumes that the pokemon has been added to the Pokemon database already
    public void addPokemon(String UUID)
    {
        if(this.getPokemonList() != null && this.getPokemonList().toList().contains(UUID)) throw new IllegalStateException("Pokemon already in Player's List!");
        Mongo.PlayerData.updateOne(this.query, Updates.push("pokemon", UUID));

        this.update();
    }

    //Removes all instances (should be 1 instance only) of the UUID from the player's list, but does not delete the pokemon from the database (to allow for trading)
    public void removePokemon(String UUID)
    {
        Mongo.PlayerData.updateOne(this.query, Updates.pull("pokemon", UUID));

        this.update();
    }

    //Assumes index is given counted from 1, so the method will decrement the argument
    public void removePokemon(int index)
    {
        index--;
        this.removePokemon(this.getPokemonList().getString(index));
    }

    //Sets the selected pokemon
    public void setSelected(int num)
    {
        Mongo.PlayerData.updateOne(this.query, Updates.set("selected", num));

        this.update();
    }

    //Updates the 'selected' field to avoid IndexOutOfBounds errors
    public void updateSelected()
    {
        if(this.getSelected() >= this.getPokemonList().length()) this.setSelected(this.getPokemonList().length());
    }

}
