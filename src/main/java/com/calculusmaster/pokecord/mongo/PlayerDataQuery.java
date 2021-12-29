package com.calculusmaster.pokecord.mongo;

import com.calculusmaster.pokecord.Pokecord;
import com.calculusmaster.pokecord.game.bounties.components.Bounty;
import com.calculusmaster.pokecord.game.bounties.enums.ObjectiveType;
import com.calculusmaster.pokecord.game.enums.functional.Achievements;
import com.calculusmaster.pokecord.game.player.level.MasteryLevelManager;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.pokemon.PokemonEgg;
import com.calculusmaster.pokecord.util.Mongo;
import com.calculusmaster.pokecord.util.enums.PlayerStatistic;
import com.calculusmaster.pokecord.util.helpers.CacheHelper;
import com.calculusmaster.pokecord.util.helpers.LoggerHelper;
import com.calculusmaster.pokecord.util.helpers.ThreadPoolHandler;
import com.mongodb.client.model.Updates;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.bson.Document;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class PlayerDataQuery extends MongoQuery
{
    private Optional<PlayerSettingsQuery> settings = Optional.empty();
    private Optional<PlayerStatisticsQuery> stats = Optional.empty();

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
                .append("level", 0)
                .append("exp", 0)
                .append("credits", 1000)
                .append("redeems", 0)
                .append("selected", 1)
                .append("pokemon", new JSONArray())
                .append("team", new JSONArray())
                .append("favorites", new JSONArray())
                .append("items", new JSONArray())
                .append("tms", new JSONArray())
                .append("trs", new JSONArray())
                .append("zcrystals", new JSONArray())
                .append("active_zcrystal", "")
                .append("achievements", new JSONArray())
                .append("owned_forms", new JSONArray())
                .append("owned_megas", new JSONArray())
                .append("bounties", new JSONArray())
                .append("pursuit", new JSONArray())
                .append("owned_eggs", new JSONArray())
                .append("active_egg", "");

        Mongo.PlayerData.insertOne(data);

        PlayerSettingsQuery.register(player.getId());
        PlayerStatisticsQuery.register(player.getId());

        CacheHelper.addPlayer(player.getId());
    }

    public void directMessage(String msg)
    {
        try
        {
            Pokecord.BOT_JDA.openPrivateChannelById(this.getID()).flatMap(channel -> channel.sendMessage(msg)).queue();
        }
        catch (Exception e)
        {
            LoggerHelper.reportError(PlayerDataQuery.class, "Failed to DM " + this.getUsername() + " (ID: " + this.getID() + ")!", e);
        }
    }

    //Get the SettingsHelper object
    public PlayerSettingsQuery getSettings()
    {
        if(this.settings.isEmpty()) this.settings = Optional.of(new PlayerSettingsQuery(this.getID()));
        return this.settings.get();
    }

    //Get the PlayerStatisticsQuery object
    public PlayerStatisticsQuery getStats()
    {
        if(this.stats.isEmpty()) this.stats = Optional.of(new PlayerStatisticsQuery(this.getID()));
        return this.stats.get();
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

    //key: "level"
    public int getLevel()
    {
        return this.json().getInt("level");
    }

    public void increaseLevel()
    {
        this.update(Updates.inc("level", 1));

        this.clearExp();
    }

    //key: "exp"
    public int getExp()
    {
        return this.json().getInt("exp");
    }

    public void clearExp()
    {
        this.update(Updates.set("exp", 0));
    }

    public void addExp(int amount)
    {
        this.addExp(amount, 100);
    }

    public void addExp(int amount, int chance)
    {
        if(new Random().nextInt(100) < chance)
        {
            this.update(Updates.inc("exp", amount));

            if(!MasteryLevelManager.isMax(this) && MasteryLevelManager.MASTERY_LEVELS.get(this.getLevel() + 1).canLevelUp(this))
            {
                this.increaseLevel();

                if(this.getLevel() == 20) Achievements.grant(this.getID(), Achievements.REACH_MASTERY_LEVEL_20, null);

                this.directMessage("You are now **Pokemon Mastery Level " + this.getLevel() + "**! You've unlocked the following features:\n" + MasteryLevelManager.MASTERY_LEVELS.get(this.getLevel()).getUnlockedFeaturesOverview());
            }
        }
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
        this.update(Updates.set("redeems", this.getRedeems() + amount));
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
        this.update(Updates.set("selected", num));
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
        this.update(Updates.set("team", new JSONArray()));
    }

    public void addPokemonToTeam(String UUID, int index)
    {
        this.clearTeam();
        List<String> team = new ArrayList<>(this.getTeam());

        index--;

        if(index >= team.size()) team.add(UUID);
        else team.set(index, UUID);

        this.update(Updates.pushEach("team", team));
    }

    public void removePokemonFromTeam(int index)
    {
        this.clearTeam();
        List<String> team = new ArrayList<>(this.getTeam());

        index--;

        team.remove(index);

        this.update(Updates.pushEach("team", team));
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

        this.update(Updates.pushEach("team", team));
    }

    //key: "favorites"
    public List<String> getFavorites()
    {
        return this.json().getJSONArray("favorites").toList().stream().map(s -> (String)s).collect(Collectors.toList());
    }

    public void addPokemonToFavorites(String UUID)
    {
        this.update(Updates.push("favorites", UUID));
    }

    public void removePokemonFromFavorites(String UUID)
    {
        this.update(Updates.pull("favorites", UUID));
    }

    public void clearFavorites()
    {
        this.update(Updates.set("favorites", new JSONArray()));
    }


    //key: "items"
    public List<String> getItemList()
    {
        return this.json().getJSONArray("items").toList().stream().map(o -> (String)o).collect(Collectors.toList());
    }

    public void addItem(String item)
    {
        this.update(Updates.push("items", item));
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
        this.update(Updates.push("tms", TM));
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
        this.update(Updates.push("trs", TR));
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
        this.update(Updates.push("zcrystals", z));
    }

    //key: "active_zcrystal"
    public String getEquippedZCrystal()
    {
        return this.json().getString("active_zcrystal");
    }

    public void equipZCrystal(String z)
    {
        this.update(Updates.set("active_zcrystal", z));
    }

    //key: "achievements"
    public List<String> getAchievementsList()
    {
        return this.json().getJSONArray("achievements").toList().stream().map(o -> (String)o).collect(Collectors.toList());
    }

    public void addAchievement(Achievements a)
    {
        this.update(Updates.push("achievements", a.toString()));
    }

    public void grantAchievement(Achievements a, MessageReceivedEvent event)
    {
        Achievements.grant(this.getID(), a, event);
    }

    //key: "owned_forms"
    public List<String> getOwnedForms()
    {
        return this.json().getJSONArray("owned_forms").toList().stream().map(s -> (String)s).collect(Collectors.toList());
    }

    public void addOwnedForm(String form)
    {
        this.update(Updates.push("owned_forms", form));
    }

    //key: "owned_megas"
    public List<String> getOwnedMegas()
    {
        return this.json().getJSONArray("owned_megas").toList().stream().map(s -> (String)s).collect(Collectors.toList());
    }

    public void addOwnedMegas(String mega)
    {
        this.update(Updates.push("owned_megas", mega));
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

    public void updateBountyProgression(final Consumer<Bounty> checker)
    {
        ThreadPoolHandler.BOUNTY.execute(() -> this.updateBounty(checker));
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

    public void updateBountyProgression(ObjectiveType type, int amount)
    {
        this.updateBountyProgression(type, b -> b.update(amount));
    }

    public void addBounty(String ID)
    {
        this.update(Updates.push("bounties", ID));
    }

    public void removeBounty(String ID)
    {
        this.update(Updates.pull("bounties", ID));
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

        this.update(Updates.set("pursuit", new JSONArray()));

        this.resetPursuitLevel();
    }

    //key: "pursuit_level"
    public int getPursuitLevel()
    {
        return this.json().getInt("pursuit_level");
    }

    public void increasePursuitLevel()
    {
        this.update(Updates.inc("pursuit_level", 1));
    }

    public void resetPursuitLevel()
    {
        this.update(Updates.set("pursuit_level", 0));
    }

    //key: "owned_eggs"
    public List<String> getOwnedEggIDs()
    {
        return this.json().getJSONArray("owned_eggs").toList().stream().map(s -> (String)s).collect(Collectors.toList());
    }

    public List<PokemonEgg> getOwnedEggs()
    {
        return this.getOwnedEggIDs().stream().map(PokemonEgg::fromDB).collect(Collectors.toList());
    }

    public boolean hasEggs()
    {
        return !this.getOwnedEggIDs().isEmpty();
    }

    public void addEgg(String eggID)
    {
        this.update(Updates.push("owned_eggs", eggID));
    }

    public void removeEgg(String eggID)
    {
        this.update(Updates.pull("owned_eggs", eggID));
    }

    //key: "active_egg"
    public String getActiveEggID()
    {
        return this.json().getString("active_egg");
    }

    public PokemonEgg getActiveEgg()
    {
        return PokemonEgg.fromDB(this.getActiveEggID());
    }

    public boolean hasActiveEgg()
    {
        return !this.json().getString("active_egg").equals("");
    }

    public void setActiveEgg(String eggID)
    {
        this.update(Updates.set("active_egg", eggID));
    }

    public void removeActiveEgg()
    {
        this.update(Updates.set("active_egg", ""));
    }
}
