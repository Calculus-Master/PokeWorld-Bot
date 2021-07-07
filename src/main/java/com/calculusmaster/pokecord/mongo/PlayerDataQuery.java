package com.calculusmaster.pokecord.mongo;

import com.calculusmaster.pokecord.Pokecord;
import com.calculusmaster.pokecord.game.Achievements;
import com.calculusmaster.pokecord.game.PokePass;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.util.Mongo;
import com.calculusmaster.pokecord.util.helpers.CacheHelper;
import com.mongodb.client.model.Updates;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
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
                .append("gym_progress", new JSONArray())
                .append("pokepass_exp", 0)
                .append("pokepass_tier", 0)
                .append("favorites", new JSONArray())
                .append("owned_forms", new JSONArray())
                .append("owned_megas", new JSONArray());

        Mongo.PlayerData.insertOne(data);
    }

    private void update()
    {
        this.document = Mongo.PlayerData.find(this.query).first();
    }

    public void directMessage(String msg)
    {
        Pokecord.BOT_JDA.openPrivateChannelById(this.getID()).flatMap(channel -> channel.sendMessage(msg)).queue();
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
        this.updateSelected();

        return this.json().getInt("selected") - 1;
    }

    public void setSelected(int num)
    {
        Mongo.PlayerData.updateOne(this.query, Updates.set("selected", num));

        this.update();
    }

    public void updateSelected()
    {
        if(this.getSelected() > this.getPokemonList().size()) this.setSelected(this.getPokemonList().size());
        else if(this.getSelected() <= 0) this.setSelected(1);
    }

    public Pokemon getSelectedPokemon()
    {
        return Pokemon.build(this.getPokemonList().get(this.getSelected()));
    }

    //key: "pokemon"
    public List<String> getPokemonList()
    {
        if(CacheHelper.DYNAMIC_CACHING_ACTIVE && (!CacheHelper.POKEMON_LISTS.containsKey(this.getID()) || CacheHelper.UUID_LISTS.get(this.getID()).size() != this.getPokemonList().size()))
        {
            this.directMessage("Initializing your Pokemon List! This may take a while, depending on how many Pokemon you have. This will only happen once!");

            CacheHelper.createPokemonList(this.getID());

            this.directMessage("Your pokemon list has been initialized! Running command...");
        }

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

        if(this.getTeam().contains(UUID)) this.removePokemonFromTeam(this.getTeam().indexOf(UUID));
        if(this.getFavorites().contains(UUID)) this.removePokemonFromFavorites(UUID);

        this.update();
        this.updateSelected();
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
        int counts = 0;
        for(String s : this.getItemList()) if(s.equalsIgnoreCase(item)) counts++;

        Mongo.PlayerData.updateOne(this.query, Updates.pull("items", item));

        for(int i = 0; i < counts - 1; i++) this.addItem(item);

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

        this.update();
    }

    //key: "pokepass_exp"
    public int getPokePassExp()
    {
        return this.json().getInt("pokepass_exp");
    }

    public void addPokePassExp(int amount, MessageReceivedEvent event)
    {
        Mongo.PlayerData.updateOne(this.query, Updates.inc("pokepass_exp", amount));

        this.update();

        if(this.getPokePassExp() >= PokePass.TIER_EXP && PokePass.tierExists(this.getPokePassTier() + 1))
        {
            Mongo.PlayerData.updateOne(this.query, Updates.set("pokepass_exp", this.getPokePassExp() - PokePass.TIER_EXP));
            this.increasePokePassTier();

            event.getChannel().sendMessage(PokePass.reward(this.getPokePassTier() + 1, this)).queue();

            this.update();
        }
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

    //key: "favorites"
    public List<String> getFavorites()
    {
        return this.json().getJSONArray("favorites").toList().stream().map(s -> (String)s).collect(Collectors.toList());
    }

    public void addPokemonToFavorites(String UUID)
    {
        Mongo.PlayerData.updateOne(this.query, Updates.push("favorites", UUID));

        this.update();
    }

    public void removePokemonFromFavorites(String UUID)
    {
        Mongo.PlayerData.updateOne(this.query, Updates.pull("favorites", UUID));

        this.update();
    }

    public void clearFavorites()
    {
        Mongo.PlayerData.updateOne(this.query, Updates.set("favorites", new JSONArray()));

        this.update();
    }

    //key: "owned_forms"
    public List<String> getOwnedForms()
    {
        return this.json().getJSONArray("owned_forms").toList().stream().map(s -> (String)s).collect(Collectors.toList());
    }

    public void addOwnedForm(String form)
    {
        Mongo.PlayerData.updateOne(this.query, Updates.push("owned_forms", form));

        this.update();
    }

    //key: "owned_megas"
    public List<String> getOwnedMegas()
    {
        return this.json().getJSONArray("owned_megas").toList().stream().map(s -> (String)s).collect(Collectors.toList());
    }

    public void addOwnedMegas(String mega)
    {
        Mongo.PlayerData.updateOne(this.query, Updates.push("owned_megas", mega));

        this.update();
    }
}
