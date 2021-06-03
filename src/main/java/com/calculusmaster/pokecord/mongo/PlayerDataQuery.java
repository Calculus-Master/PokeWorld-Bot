package com.calculusmaster.pokecord.mongo;

import com.calculusmaster.pokecord.game.Achievements;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.util.CacheHelper;
import com.calculusmaster.pokecord.util.Mongo;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import net.dv8tion.jda.api.entities.User;
import org.bson.Document;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerDataQuery extends MongoQuery
{
    public PlayerDataQuery(String playerID)
    {
        super("playerID", playerID, Mongo.PlayerData);
    }

    //Registered

    public static boolean isRegistered(String id)
    {
        return !new PlayerDataQuery(id).isNull();
    }

    public static void register(User player)
    {
        Document data = new Document()
                .append("playerID", player.getId())
                .append("username", player.getName())
                .append("credits", 1000)
                .append("redeems", 0)
                .append("selected", 1)
                .append("pokemon", new JSONArray())
                .append("team", new JSONArray())
                .append("items", new JSONArray())
                .append("tms", new JSONArray())
                .append("trs", new JSONArray())
                .append("zcrystals", new JSONArray())
                .append("active_zcrystal", "")
                .append("achievements", new JSONArray())
                .append("gym_level", 1)
                .append("gym_progress", new JSONArray());

        Mongo.PlayerData.insertOne(data);
    }

    private void update()
    {
        this.document = Mongo.PlayerData.find(this.query).first();
    }

    //key: "playerID"
    public String getID()
    {
        return this.json().getString("playerID");
    }

    public String getMention()
    {
        return "<@" + this.getID() + ">";
    }

    //key: "username"
    public String getUsername()
    {
        return this.json().getString("username");
    }

    //key: "credits"
    public int getCredits()
    {
        return this.json().getInt("credits");
    }

    public void changeCredits(int amount)
    {
        Mongo.PlayerData.updateOne(this.query, Updates.set("credits", this.getCredits() + amount));

        this.update();
    }

    //key: "redeems"
    public int getRedeems()
    {
        return this.json().getInt("redeems");
    }

    public void changeRedeems(int amount)
    {
        Mongo.PlayerData.updateOne(this.query, Updates.set("redeems", this.getRedeems() + amount));

        this.update();
    }

    //key: "selected"
    public int getSelected()
    {
        return this.json().getInt("selected") - 1;
    }

    public void setSelected(int num)
    {
        Mongo.PlayerData.updateOne(this.query, Updates.set("selected", num));

        this.update();
    }

    public void updateSelected()
    {
        if(this.getSelected() >= this.getPokemonList().size()) this.setSelected(this.getPokemonList().size());
    }

    public Pokemon getSelectedPokemon()
    {
        return Pokemon.build(this.getPokemonList().get(this.getSelected()));
    }

    //key: "pokemon"
    public List<String> getPokemonList()
    {
        return CacheHelper.UUID_LISTS.get(this.getID());
    }

    public void addPokemon(String UUID)
    {
        Mongo.PlayerData.updateOne(this.query, Updates.push("pokemon", UUID));
        CacheHelper.addPokemon(this.getID(), UUID);

        this.update();
    }

    public void removePokemon(String UUID)
    {
        Mongo.PlayerData.updateOne(this.query, Updates.pull("pokemon", UUID));
        CacheHelper.removePokemon(this.getID(), UUID);

        this.update();
    }

    public void removePokemon(int index)
    {
        removePokemon(this.getPokemonList().get(index - 1));
    }

    //key: "team"       TODO: Team Caching
    public List<String> getTeam()
    {
        return this.json().getJSONArray("team").toList().stream().map(o -> (String)o).collect(Collectors.toList());
    }

    public boolean isInTeam(String UUID)
    {
        return this.getTeam().contains(UUID);
    }

    public void clearTeam()
    {
        Mongo.PlayerData.updateOne(this.query, Updates.set("team", new JSONArray()));
    }

    public void addPokemonToTeam(String UUID, int index)
    {
        this.clearTeam();
        List<String> team = new ArrayList<>(this.getTeam());

        index--;

        if(index >= team.size()) team.add(UUID);
        else team.set(index, UUID);

        Mongo.PlayerData.updateOne(this.query, Updates.pushEach("team", team));
        this.update();
    }

    public void removePokemonFromTeam(int index)
    {
        this.clearTeam();
        List<String> team = new ArrayList<>(this.getTeam());

        index--;

        team.remove(index);

        Mongo.PlayerData.updateOne(this.query, Updates.pushEach("team", team));
        this.update();
    }

    public void swapPokemonInTeam(int from, int to)
    {
        this.clearTeam();
        List<String> team = new ArrayList<>(this.getTeam());

        from--;
        to--;

        String temp = team.get(from);
        team.set(from, team.get(to));
        team.set(to, temp);

        Mongo.PlayerData.updateOne(this.query, Updates.pushEach("team", team));
        this.update();
    }

    //key: "items"
    public List<String> getItemList()
    {
        return this.json().getJSONArray("items").toList().stream().map(o -> (String)o).collect(Collectors.toList());
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

    //key: "tms"
    public List<String> getTMList()
    {
        return this.json().getJSONArray("tms").toList().stream().map(o -> (String)o).collect(Collectors.toList());
    }

    public void addTM(String TM)
    {
        Mongo.PlayerData.updateOne(this.query, Updates.push("tms", TM));

        this.update();
    }

    public void removeTM(String TM)
    {
        int counts = 0;
        for (int i = 0; i < this.getTMList().size(); i++) if(this.getTMList().get(i).equals(TM)) counts++;

        Mongo.PlayerData.updateOne(this.query, Updates.pull("tms", TM));

        for(int i = 0; i < counts - 1; i++) this.addTM(TM);

        this.update();
    }

    //key: "trs"
    public List<String> getTRList()
    {
        return this.json().getJSONArray("trs").toList().stream().map(o -> (String)o).collect(Collectors.toList());
    }

    public void addTR(String TR)
    {
        Mongo.PlayerData.updateOne(this.query, Updates.push("trs", TR));

        this.update();
    }

    //Removes a TR from the player's owned TRs
    public void removeTR(String TR)
    {
        int counts = 0;
        for (int i = 0; i < this.getTRList().size(); i++) if(this.getTRList().get(i).equals(TR)) counts++;

        Mongo.PlayerData.updateOne(this.query, Updates.pull("trs", TR));

        for(int i = 0; i < counts - 1; i++) this.addTR(TR);

        this.update();
    }

    //key: "zcrystals"
    public List<String> getZCrystalList()
    {
        return this.json().getJSONArray("zcrystals").toList().stream().map(o -> (String)o).collect(Collectors.toList());
    }

    public boolean hasZCrystal(String z)
    {
        return this.getZCrystalList().contains(z);
    }

    public void addZCrystal(String z)
    {
        Mongo.PlayerData.updateOne(this.query, Updates.push("zcrystals", z));

        this.update();
    }

    //key: "active_zcrystal"
    public String getEquippedZCrystal()
    {
        return this.json().getString("active_zcrystal");
    }

    public void equipZCrystal(String z)
    {
        Mongo.PlayerData.updateOne(this.query, Updates.set("active_zcrystal", z));

        this.update();
    }

    //key: "achievements"
    public List<String> getAchievementsList()
    {
        return this.json().getJSONArray("achievements").toList().stream().map(o -> (String)o).collect(Collectors.toList());
    }

    public void addAchievement(Achievements a)
    {
        Mongo.PlayerData.updateOne(this.query, Updates.push("achievements", a.toString()));

        this.update();
    }

    //key: "gym_level"
    public int getGymLevel()
    {
        return this.json().getInt("gym_level");
    }

    public void increaseGymLevel()
    {
        Mongo.PlayerData.updateOne(this.query, Updates.inc("gym_level", 1));
    }

    //key: "pokepass_exp"
    public int getPokePassExp()
    {
        return this.json().getInt("pokepass_exp");
    }

    public void addPokePassExp(int amount)
    {
        Mongo.PlayerData.updateOne(this.query, Updates.inc("pokepass_exp", amount));
    }

    //key: "pokepass_tier"
    public int getPokePassTier()
    {
        return this.json().getInt("pokepass_tier");
    }

    public void increasePokePassTier()
    {
        Mongo.PlayerData.updateOne(this.query, Updates.inc("pokepass_tier", 1));
    }
}
