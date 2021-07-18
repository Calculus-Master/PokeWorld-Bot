package com.calculusmaster.pokecord.mongo;

import com.calculusmaster.pokecord.Pokecord;
import com.calculusmaster.pokecord.game.Achievements;
import com.calculusmaster.pokecord.game.PokePass;
import com.calculusmaster.pokecord.game.Pokemon;
import com.calculusmaster.pokecord.game.PokemonSkin;
import com.calculusmaster.pokecord.game.bounties.components.Bounty;
import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;
import com.calculusmaster.pokecord.util.Mongo;
import com.calculusmaster.pokecord.util.enums.PlayerStatistic;
import com.calculusmaster.pokecord.util.helpers.CacheHelper;
import com.calculusmaster.pokecord.util.helpers.LoggerHelper;
import com.calculusmaster.pokecord.util.helpers.SettingsHelper;
import com.calculusmaster.pokecord.util.helpers.ThreadPoolHandler;
import com.mongodb.client.model.Updates;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
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
                .append("owned_megas", new JSONArray())
                .append("bounties", new JSONArray())
                .append("pursuit", new JSONArray());

        Mongo.PlayerData.insertOne(data);

        SettingsHelper.register(player.getId());
        PlayerStatisticsQuery.register(player.getId());
    }

    private void update()
    {
        this.document = Mongo.PlayerData.find(this.query).first();
    }

    private void update(Bson update)
    {
        Mongo.PlayerData.updateOne(this.query, update);

        this.update();
    }

    public void directMessage(String msg)
    {
        try
        {
            Pokecord.BOT_JDA.openPrivateChannelById(this.getID()).flatMap(channel -> channel.sendMessage(msg)).queue();
        }
        catch (Exception e)
        {
            LoggerHelper.error(this.getClass(), "Failed to DM " + this.getUsername() + " (ID: " + this.getID() + ")!");
        }
    }

    //Get the SettingsHelper object
    public SettingsHelper getSettings()
    {
        return new SettingsHelper(this.getID());
    }

    //Get the PlayerStatisticsQuery object
    public PlayerStatisticsQuery getStats()
    {
        return new PlayerStatisticsQuery(this.getID());
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

        if(amount < 0) this.getStats().incr(PlayerStatistic.CREDITS_SPENT, Math.abs(amount));
        else if(amount > 0) this.getStats().incr(PlayerStatistic.CREDITS_EARNED, Math.abs(amount));

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
        int selected = this.json().getInt("selected");
        int pokemonListSize = this.getPokemonList().size();

        if(selected > pokemonListSize) this.setSelected(pokemonListSize);
        else if(selected <= 0) this.setSelected(1);

        return this.json().getInt("selected") - 1;
    }

    public void setSelected(int num)
    {
        Mongo.PlayerData.updateOne(this.query, Updates.set("selected", num));

        this.update();
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
    }

    public void removePokemon(int index)
    {
        removePokemon(this.getPokemonList().get(index - 1));
    }

    //key: "team"
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

        this.updateBountyProgression(b -> {
            if(b.getType().equals(ObjectiveType.EARN_XP_POKEPASS)) b.update(amount);
        });

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

    //key: "bounties"
    public List<String> getBountyIDs()
    {
        return this.json().getJSONArray("bounties").toList().stream().map(s -> (String)s).collect(Collectors.toList());
    }

    public List<Bounty> getBounties()
    {
        return this.getBountyIDs().stream().map(Bounty::fromDB).collect(Collectors.toList());
    }

    public void updateBountyProgression(final Consumer<Bounty> checker)
    {
        ThreadPoolHandler.BOUNTY.execute(() -> this.updateBounty(checker));
    }

    private void updateBounty(Consumer<Bounty> checker)
    {
        if(this.document == null) this.update();

        //Basic Bounties
        for(String ID : this.getBountyIDs())
        {
            Bounty b = Bounty.fromDB(ID);

            if(!b.getObjective().isComplete())
            {
                checker.accept(b);

                b.updateProgression();

                if(b.getObjective().isComplete()) this.directMessage("You have unclaimed bounties!");
            }
        }

        //PursuitBuilder Bounty
        if(this.hasPursuit() && this.getPursuitLevel() <= this.getPursuitIDs().size())
        {
            Bounty pursuit = this.getCurrentPursuitBounty();

            if(!pursuit.getObjective().isComplete())
            {
                checker.accept(pursuit);

                pursuit.updateProgression();

                if(pursuit.getObjective().isComplete()) this.directMessage("Your active Pursuit bounty is complete!");
            }
        }
    }

    public void updateBountyProgression(ObjectiveType type, Consumer<Bounty> checker)
    {
        this.updateBountyProgression((b) -> {
            if(b.getType().equals(type)) checker.accept(b);
        });
    }

    public void updateBountyProgression(ObjectiveType type)
    {
        this.updateBountyProgression(type, Bounty::update);
    }

    public void addBounty(String ID)
    {
        Mongo.PlayerData.updateOne(this.query, Updates.push("bounties", ID));

        this.update();
    }

    public void removeBounty(String ID)
    {
        Mongo.PlayerData.updateOne(this.query, Updates.pull("bounties", ID));

        this.update();
    }

    //key: "pursuit"
    public List<String> getPursuitIDs()
    {
        return this.json().getJSONArray("pursuit").toList().stream().map(s -> (String)s).collect(Collectors.toList());
    }

    public void setPursuit(List<String> IDs)
    {
        Mongo.PlayerData.updateOne(this.query, Updates.pushEach("pursuit", IDs));

        this.update();
    }

    public Bounty getCurrentPursuitBounty()
    {
        return Bounty.fromDB(this.getPursuitIDs().get(this.getPursuitLevel() - 1));
    }

    public boolean hasPursuit()
    {
        return !this.getPursuitIDs().isEmpty();
    }

    public void removePursuit()
    {
        for(String s : this.getPursuitIDs()) Bounty.delete(s);

        Mongo.PlayerData.updateOne(this.query, Updates.set("pursuit", new JSONArray()));

        this.resetPursuitLevel();
    }

    //key: "pursuit_level"
    public int getPursuitLevel()
    {
        return this.json().getInt("pursuit_level");
    }

    public void increasePursuitLevel()
    {
        Mongo.PlayerData.updateOne(this.query, Updates.inc("pursuit_level", 1));

        this.update();
    }

    public void resetPursuitLevel()
    {
        Mongo.PlayerData.updateOne(this.query, Updates.set("pursuit_level", 0));

        this.update();
    }

    //key: "skins"
    public List<PokemonSkin> getOwnedSkins()
    {
        return this.json().getJSONArray("skins").toList().stream().map(s -> (String)s).map(PokemonSkin::cast).collect(Collectors.toList());
    }

    public List<PokemonSkin> getOwnedSkins(String pokemon)
    {
        return this.getOwnedSkins().stream().filter(s -> s.pokemon.equals(pokemon)).collect(Collectors.toList());
    }

    public boolean hasSkin(String pokemon)
    {
        return this.getOwnedSkins().stream().anyMatch(s -> s.pokemon.equals(pokemon));
    }

    public void addSkin(PokemonSkin skin)
    {
        this.update(Updates.push("skins", skin.toString()));
    }

    //key: "equipped_skins"
    public List<PokemonSkin> getEquippedSkins()
    {
        return this.json().getJSONArray("equipped_skins").toList().stream().map(s -> (String)s).map(PokemonSkin::cast).collect(Collectors.toList());
    }

    public PokemonSkin getEquippedSkin(String pokemon)
    {
        return this.getEquippedSkins().stream().filter(s -> s.pokemon.equals(pokemon)).collect(Collectors.toList()).get(0);
    }

    public boolean hasEquippedSkin(String pokemon)
    {
        return this.getEquippedSkins().stream().anyMatch(s -> s.pokemon.equals(pokemon));
    }

    public void equipSkin(PokemonSkin skin)
    {
        this.update(Updates.push("equipped_skins", skin.toString()));
    }

    public void unequipSkin(PokemonSkin skin)
    {
        this.update(Updates.pull("equipped_skins", skin.toString()));
    }
}
