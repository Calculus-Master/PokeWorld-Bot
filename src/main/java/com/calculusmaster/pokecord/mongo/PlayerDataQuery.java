package com.calculusmaster.pokecord.mongo;

import com.calculusmaster.pokecord.game.Achievements;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.enums.items.TM;
import com.calculusmaster.pokecord.game.enums.items.TR;
import com.calculusmaster.pokecord.game.enums.items.XPBooster;
import com.calculusmaster.pokecord.util.Global;
import com.calculusmaster.pokecord.util.Mongo;
import com.mongodb.client.model.Updates;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.bson.Document;
import org.json.JSONArray;

import java.time.OffsetDateTime;
import java.util.Random;

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
                .append("selected", 1)
                .append("tms", TM.values()[new Random().nextInt(TM.values().length)].toString())
                .append("trs", TR.values()[new Random().nextInt(TR.values().length)].toString());

        Mongo.PlayerData.insertOne(playerData);
    }

    private void update()
    {
        this.document = Mongo.PlayerData.find(this.query).first();
        this.updatePokemonList();
    }

    private void updatePokemonList()
    {
        new Thread(() -> Global.updatePokemonList(this.json().getString("playerID"))).start();
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

    public JSONArray getOwnedTMs()
    {
        return !this.json().has("tms") ? null : this.json().getJSONArray("tms");
    }

    public JSONArray getOwnedTRs()
    {
        return !this.json().has("trs") ? null : this.json().getJSONArray("trs");
    }

    public boolean hasXPBooster()
    {
        return this.json().has("xp");
    }

    public int getXPBoosterLength()
    {
        return this.json().getJSONObject("xp").getInt("length");
    }

    public String getXPBoosterTimeStamp()
    {
        return this.json().getJSONObject("xp").getString("timestamp");
    }

    public JSONArray getItemList()
    {
        return !this.json().has("items") ? null : this.json().getJSONArray("items");
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

    //Adds a TM to the player's owned TMs
    public void addTM(String TM)
    {
        Mongo.PlayerData.updateOne(this.query, Updates.push("tms", TM));

        this.update();
    }

    //Removes a TM from the player's owned TMs
    public void removeTM(String TM)
    {
        int counts = 0;
        for (int i = 0; i < this.getOwnedTMs().length(); i++) if(this.getOwnedTMs().getString(i).equals(TM)) counts++;

        Mongo.PlayerData.updateOne(this.query, Updates.pull("tms", TM));

        for(int i = 0; i < counts - 1; i++) this.addTM(TM);

        this.update();
    }

    //Adds a TR to the player's owned TRs
    public void addTR(String TR)
    {
        Mongo.PlayerData.updateOne(this.query, Updates.push("trs", TR));

        this.update();
    }

    //Removes a TR from the player's owned TRs
    public void removeTR(String TR)
    {
        int counts = 0;
        for (int i = 0; i < this.getOwnedTRs().length(); i++) if(this.getOwnedTRs().getString(i).equals(TR)) counts++;

        Mongo.PlayerData.updateOne(this.query, Updates.pull("trs", TR));

        for(int i = 0; i < counts - 1; i++) this.addTR(TR);

        this.update();
    }

    public void addXPBooster(XPBooster booster, MessageReceivedEvent event)
    {
        if(this.hasXPBooster()) this.removeXPBooster();

        OffsetDateTime t = event.getMessage().getTimeCreated();
        String msgTime = t.getDayOfYear() + "-" + t.getHour() + "-" + t.getMinute();
        Document xpBooster = new Document("length", booster.time()).append("timestamp", msgTime);
        Mongo.PlayerData.updateOne(this.query, Updates.set("xp", xpBooster));

        this.update();
    }

    public void removeXPBooster()
    {
        Mongo.PlayerData.updateOne(this.query, Updates.unset("xp"));

        this.update();
    }

    public void addItem(String item)
    {
        Mongo.PlayerData.updateOne(this.query, Updates.push("items", item));

        this.update();
    }

    public void removeItem(String item)
    {
        Mongo.PlayerData.updateOne(this.query, Updates.pull("items", item));

        this.update();
    }

    public void addAchievement(Achievements a)
    {
        Mongo.PlayerData.updateOne(this.query, Updates.push("achievements", a.toString()));

        this.update();
    }
}
