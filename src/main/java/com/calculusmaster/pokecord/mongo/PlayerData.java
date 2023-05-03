package com.calculusmaster.pokecord.mongo;

import com.calculusmaster.pokecord.Pokeworld;
import com.calculusmaster.pokecord.game.duel.trainer.TrainerData;
import com.calculusmaster.pokecord.game.duel.trainer.TrainerManager;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.enums.functional.Achievement;
import com.calculusmaster.pokecord.game.objectives.ObjectiveType;
import com.calculusmaster.pokecord.game.objectives.types.AbstractObjective;
import com.calculusmaster.pokecord.game.player.components.*;
import com.calculusmaster.pokecord.game.player.leaderboard.PokeWorldLeaderboard;
import com.calculusmaster.pokecord.game.player.level.MasteryLevelManager;
import com.calculusmaster.pokecord.game.player.level.PMLExperience;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.pokemon.augments.PokemonAugment;
import com.calculusmaster.pokecord.game.pokemon.evolution.PokemonEgg;
import com.calculusmaster.pokecord.mongo.cache.CacheHandler;
import com.calculusmaster.pokecord.util.enums.StatisticType;
import com.calculusmaster.pokecord.util.helpers.LoggerHelper;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.model.Updates;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;

public class PlayerData extends MongoQuery
{
    private static final ExecutorService UPDATER = Executors.newThreadPerTaskExecutor(Thread.ofVirtual().name("PlayerData Updates-", 0).factory());

    private Optional<PlayerSettingsQuery> settings = Optional.empty();

    private final Random random = new Random();

    private final Bson query;
    private Document document;

    private PlayerPokedex pokedex;
    private PlayerInventory inventory;
    private PlayerTeam team;
    private PlayerStatisticsRecord statistics;
    private PlayerResearchTasks tasks;

    private PlayerData(Document data)
    {
        super("playerID", data.getString("playerID"), Mongo.PlayerData);
        this.query = Filters.eq("playerID", data.getString("playerID"));
        this.document = data;
    }

    public static PlayerData build(String playerID)
    {
        return CacheHandler.PLAYER_DATA.get(playerID, id ->
        {
            LoggerHelper.info(PlayerData.class, "Loading new PlayerData into Cache for ID: " + id + ".");
            Document data = Mongo.PlayerData.find(Filters.eq("playerID", playerID)).first();

            return new PlayerData(Objects.requireNonNull(data, "Null PlayerData for ID: " + playerID));
        });
    }

    private synchronized void updateDocument(Document document)
    {
        this.document = document;
    }

    //Registered

    public static void register(User player)
    {
        Document data = new Document()
                .append("playerID", player.getId())
                .append("username", player.getName())
                .append("join", OffsetDateTime.now().toEpochSecond())
                .append("level", 0)
                .append("exp", 0)
                .append("credits", 100)
                .append("redeems", 0)
                .append("selected", 1)
                .append("pokemon", new ArrayList<>())
                .append("favorites", new ArrayList<>())
                .append("achievements", new ArrayList<>())
                .append("eggs", new ArrayList<>())
                .append("active_egg", "")
                .append("defeated_trainers", new ArrayList<>())
                .append("pokedex", new PlayerPokedex().serialize())
                .append("inventory", new PlayerInventory(null).serialize())
                .append("team", new PlayerTeam().serialize())
                .append("statistics", new PlayerStatisticsRecord(null).serialize())
                .append("tasks", new PlayerResearchTasks(null).serialize())

                ;

        LoggerHelper.logDatabaseInsert(PlayerData.class, data);

        Mongo.PlayerData.insertOne(data);

        PlayerSettingsQuery.register(player.getId());

        PlayerData playerData = new PlayerData(data);

        CacheHandler.PLAYER_DATA.put(player.getId(), playerData);
    }

    public static boolean isRegistered(String playerID)
    {
        PlayerData data = CacheHandler.PLAYER_DATA.getIfPresent(playerID);

        return data != null || Mongo.PlayerData.find(Filters.eq("playerID", playerID)).first() != null;
    }

    public Bson getQuery()
    {
        return this.query;
    }

    @Override
    protected void update(Bson update)
    {
        UPDATER.submit(() -> {
            Document d = this.database.findOneAndUpdate(this.query, update, new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER));
            this.updateDocument(d);
            LoggerHelper.logDatabaseUpdate(this.getClass(), this.query, update);
            PokeWorldLeaderboard.addUpdatedPlayer(this.getID());
        });
    }

    public void directMessage(String msg)
    {
        try
        {
            Pokeworld.BOT_JDA.openPrivateChannelById(this.getID()).flatMap(channel -> channel.sendMessage(msg)).queue();
        }
        catch (Exception e)
        {
            LoggerHelper.reportError(PlayerData.class, "Failed to DM " + this.getUsername() + " (ID: " + this.getID() + ")!", e);
        }
    }

    public void directMessage(MessageEmbed embed)
    {
        try
        {
            Pokeworld.BOT_JDA.openPrivateChannelById(this.getID()).flatMap(channel -> channel.sendMessageEmbeds(embed)).queue();
        }
        catch (Exception e)
        {
            LoggerHelper.reportError(PlayerData.class, "Failed to DM " + this.getUsername() + " (ID: " + this.getID() + ")!", e);
        }
    }

    public void dmMasteryLevel()
    {
        this.directMessage(MasteryLevelManager.MASTERY_LEVELS.get(this.getLevel()).getEmbed().build());
    }

    //Get the SettingsHelper object
    public PlayerSettingsQuery getSettings()
    {
        if(this.settings.isEmpty()) this.settings = Optional.of(new PlayerSettingsQuery(this.getID()));
        return this.settings.get();
    }

    //Pokedex (key: "pokedex")
    public PlayerPokedex getPokedex()
    {
        if(this.pokedex == null) this.pokedex = new PlayerPokedex(this.document.get("pokedex", Document.class));
        return this.pokedex;
    }

    public void updatePokedex()
    {
        this.update(Updates.set("pokedex", this.pokedex.serialize()));
    }

    //Inventory (key: "inventory")
    public PlayerInventory getInventory()
    {
        PokeWorldLeaderboard.addUpdatedPlayer(this.getID());

        if(this.inventory == null) this.inventory = new PlayerInventory(this, this.document.get("inventory", Document.class));
        return this.inventory;
    }

    //Team (key: "team")
    public PlayerTeam getTeam()
    {
        if(this.team == null) this.team = new PlayerTeam(this.document.get("team", Document.class));
        return this.team;
    }

    public void updateTeam()
    {
        this.update(Updates.set("team", this.team.serialize()));
    }

    //Statistics (key: "statistics")
    public PlayerStatisticsRecord getStatistics()
    {
        PokeWorldLeaderboard.addUpdatedPlayer(this.getID());

        if(this.statistics == null) this.statistics = new PlayerStatisticsRecord(this, this.document.get("statistics", Document.class));
        return this.statistics;
    }

    //Bounties (key: "bounties")
    public PlayerResearchTasks getResearchTasks()
    {
        if(this.tasks == null) this.tasks = new PlayerResearchTasks(this, this.document.getList("tasks", Document.class));
        return this.tasks;
    }

    public void updateObjective(ObjectiveType type, int amount)
    {
        this.updateObjective(type, o -> true, amount);
    }

    public void updateObjective(ObjectiveType type, Predicate<AbstractObjective> checker, int amount)
    {
        if(this.getResearchTasks().hasObjective(type))
            this.tasks.checkAndUpdateObjectives(type, checker, amount);
    }

    //key: "playerID"
    public String getID()
    {
        return this.document.getString("playerID");
    }

    public String getMention()
    {
        return "<@" + this.getID() + ">";
    }

    //key: "username"
    public String getUsername()
    {
        return this.document.getString("username");
    }

    //key: "join"
    public long getJoinTime()
    {
        return this.document.getLong("join");
    }

    //key: "level"
    public int getLevel()
    {
        return this.document.getInteger("level");
    }

    public void increaseLevel()
    {
        this.update(Updates.inc("level", 1));

        this.clearExp();
    }

    //key: "exp"
    public int getExp()
    {
        return this.document.getInteger("exp");
    }

    public void clearExp()
    {
        this.update(Updates.set("exp", 0));
    }

    public void addExp(PMLExperience experienceEnum, int chance)
    {
        int amount = experienceEnum.experience;

        if(this.random.nextInt(100) < chance)
        {
            this.update(Updates.inc("exp", amount));

            if(!MasteryLevelManager.isMax(this) && MasteryLevelManager.MASTERY_LEVELS.get(this.getLevel() + 1).canLevelUp(this))
            {
                this.increaseLevel();

                //Starting Augments
                if(MasteryLevelManager.MASTERY_LEVELS.get(this.getLevel()).getFeatures().contains(Feature.AUGMENT_POKEMON))
                    this.getInventory().addAugments(EnumSet.of(PokemonAugment.HP_BOOST, PokemonAugment.ATK_BOOST, PokemonAugment.DEF_BOOST, PokemonAugment.SPATK_BOOST, PokemonAugment.SPDEF_BOOST, PokemonAugment.SPD_BOOST));

                this.directMessage("You are now **Pokemon Mastery Level " + this.getLevel() + "**! You've unlocked the following features:\n" + MasteryLevelManager.MASTERY_LEVELS.get(this.getLevel()).getUnlockedFeaturesOverview());
                this.dmMasteryLevel();
            }
        }
    }

    //key: "credits"
    public int getCredits()
    {
        return this.document.getInteger("credits");
    }

    public void changeCredits(int amount)
    {
        this.update(Updates.set("credits", this.getCredits() + amount));

        if(amount < 0) this.getStatistics().increase(StatisticType.CREDITS_SPENT, Math.abs(amount));
        else if(amount > 0) this.getStatistics().increase(StatisticType.CREDITS_EARNED, Math.abs(amount));
    }

    //key: "redeems"
    public int getRedeems()
    {
        return this.document.getInteger("redeems");
    }

    public void changeRedeems(int amount)
    {
        this.update(Updates.set("redeems", this.getRedeems() + amount));
    }

    //key: "selected"
    public int getSelected()
    {
        int selected = this.document.getInteger("selected");
        int pokemonListSize = this.getPokemonList().size();

        if(selected > pokemonListSize) this.setSelected(pokemonListSize);
        else if(selected <= 0) this.setSelected(1);

        return this.document.getInteger("selected") - 1;
    }

    public void setSelected(int num)
    {
        this.update(Updates.set("selected", num));
    }

    public Pokemon getSelectedPokemon()
    {
        return Pokemon.build(this.getPokemonList().get(this.getSelected()), this.getSelected() + 1);
    }

    //key: "pokemon"
    public List<String> getPokemonList()
    {
        return this.document.getList("pokemon", String.class);
    }

    public List<Pokemon> getPokemon()
    {
        List<String> in = this.getPokemonList();
        List<Pokemon> out = Collections.synchronizedList(new ArrayList<>());

        try(ExecutorService es = Executors.newFixedThreadPool(5 + in.size() / 250))
        {
            for(int i = 0; i < in.size(); i++)
            {
                final int number = i + 1;
                es.submit(() -> out.add(Pokemon.build(in.get(number - 1), number)));
            }
        }

        return out;
    }

    public void addPokemon(String UUID)
    {
        this.update(Updates.push("pokemon", UUID));
    }

    public void removePokemon(String UUID)
    {
        this.update(Updates.pull("pokemon", UUID));

        if(this.getTeam().contains(UUID)) this.getTeam().remove(UUID);
        if(this.getFavorites().contains(UUID)) this.removeFavorite(UUID);
    }

    //key: "favorites"
    public List<String> getFavorites()
    {
        return this.document.getList("favorites", String.class);
    }

    public boolean isFavorite(String UUID)
    {
        return this.getFavorites().contains(UUID);
    }

    public void addFavorite(String UUID)
    {
        this.update(Updates.push("favorites", UUID));
    }

    public void removeFavorite(String UUID)
    {
        this.update(Updates.pull("favorites", UUID));
    }

    public void clearFavorites()
    {
        this.update(Updates.set("favorites", new ArrayList<>()));
    }

    //key: "achievements"
    public List<Achievement> getAchievements()
    {
        return this.document.getList("achievements", String.class).stream().map(Achievement::valueOf).toList();
    }

    public void addAchievement(Achievement a)
    {
        this.update(Updates.push("achievements", a.toString()));
    }

    public boolean hasAchievement(Achievement a)
    {
        return this.getAchievements().contains(a);
    }

    //key: "owned_eggs"
    public List<String> getOwnedEggIDs()
    {
        return this.document.getList("eggs", String.class);
    }

    public List<PokemonEgg> getOwnedEggs()
    {
        return this.getOwnedEggIDs().stream().map(PokemonEgg::build).toList();
    }

    public boolean hasEggs()
    {
        return !this.getOwnedEggIDs().isEmpty();
    }

    public void addEgg(String eggID)
    {
        this.update(Updates.push("eggs", eggID));
    }

    public void removeEgg(String eggID)
    {
        this.update(Updates.pull("eggs", eggID));
    }

    //key: "active_egg"
    public String getActiveEggID()
    {
        return this.document.getString("active_egg");
    }

    public PokemonEgg getActiveEgg()
    {
        return PokemonEgg.build(this.getActiveEggID());
    }

    public boolean hasActiveEgg()
    {
        return !this.document.getString("active_egg").equals("");
    }

    public void setActiveEgg(String eggID)
    {
        this.update(Updates.set("active_egg", eggID));
    }

    public void removeActiveEgg()
    {
        this.update(Updates.set("active_egg", ""));
    }

    //key: "defeated_trainers"
    public List<String> getDefeatedTrainers()
    {
        return this.document.getList("defeated_trainers", String.class);
    }

    public boolean hasDefeatedTrainer(String trainerID)
    {
        return this.getDefeatedTrainers().contains(trainerID);
    }

    public void addDefeatedTrainer(String trainerID)
    {
        this.update(Updates.push("defeated_trainers", trainerID));
    }

    public boolean hasDefeatedAllTrainersOfClass(int clazz)
    {
        return TrainerManager.getTrainersOfClass(clazz).stream().map(TrainerData::getTrainerID).allMatch(s -> this.getDefeatedTrainers().contains(s));
    }

    public boolean hasDefeatedAllTrainerClasses()
    {
        return TrainerManager.REGULAR_TRAINERS.stream().map(TrainerData::getTrainerClass).distinct().allMatch(this::hasDefeatedAllTrainersOfClass);
    }
}
