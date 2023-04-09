package com.calculusmaster.pokecord.mongo;

import com.calculusmaster.pokecord.Pokecord;
import com.calculusmaster.pokecord.game.bounties.Bounty;
import com.calculusmaster.pokecord.game.bounties.ObjectiveType;
import com.calculusmaster.pokecord.game.duel.trainer.TrainerData;
import com.calculusmaster.pokecord.game.duel.trainer.TrainerManager;
import com.calculusmaster.pokecord.game.enums.elements.Feature;
import com.calculusmaster.pokecord.game.enums.functional.Achievement;
import com.calculusmaster.pokecord.game.player.PlayerInventory;
import com.calculusmaster.pokecord.game.player.PlayerPokedex;
import com.calculusmaster.pokecord.game.player.PlayerTeam;
import com.calculusmaster.pokecord.game.player.level.MasteryLevelManager;
import com.calculusmaster.pokecord.game.player.level.PMLExperience;
import com.calculusmaster.pokecord.game.pokemon.Pokemon;
import com.calculusmaster.pokecord.game.pokemon.augments.PokemonAugment;
import com.calculusmaster.pokecord.game.pokemon.data.PokemonEntity;
import com.calculusmaster.pokecord.game.pokemon.evolution.PokemonEgg;
import com.calculusmaster.pokecord.util.cacheold.PlayerDataCache;
import com.calculusmaster.pokecord.util.cacheold.PokemonDataCache;
import com.calculusmaster.pokecord.util.enums.PlayerStatistic;
import com.calculusmaster.pokecord.util.helpers.LoggerHelper;
import com.calculusmaster.pokecord.util.helpers.ThreadPoolHandler;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class PlayerDataQuery extends MongoQuery
{
    private Optional<PlayerSettingsQuery> settings = Optional.empty();
    private Optional<PlayerStatisticsQuery> statistics = Optional.empty();

    private PlayerPokedex pokedex;
    private PlayerInventory inventory;
    private PlayerTeam team;

    public PlayerDataQuery(String playerID)
    {
        super("playerID", playerID, Mongo.PlayerData);
    }

    //Cache
    public static PlayerDataQuery of(String playerID)
    {
        if(PlayerDataCache.CACHE.containsKey(playerID)) return PlayerDataCache.CACHE.get(playerID).data();
        else
        {
            try { LoggerHelper.info(PlayerDataQuery.class, "Cache does not contain " + playerID + "! Searching for: " + Objects.requireNonNull(Mongo.PlayerData.find(Filters.eq("playerID", playerID)).first())); }
            catch(NullPointerException e) { return null; }

            PlayerDataCache.addCache(playerID);
            return PlayerDataQuery.of(playerID);
        }
    }

    @NotNull
    public static PlayerDataQuery ofNonNull(String playerID)
    {
        return Objects.requireNonNull(PlayerDataQuery.of(playerID));
    }

    //Registered

    public static boolean isRegistered(String id)
    {
        return PlayerDataCache.CACHE.containsKey(id);
    }

    public static void register(User player)
    {
        Document data = new Document()
                .append("playerID", player.getId())
                .append("username", player.getName())
                .append("level", 0)
                .append("exp", 0)
                .append("credits", 100)
                .append("redeems", 0)
                .append("selected", 1)
                .append("pokemon", new ArrayList<>())
                .append("favorites", new ArrayList<>())
                .append("active_zcrystal", "")
                .append("achievements", new ArrayList<>())
                .append("owned_forms", new ArrayList<>())
                .append("owned_megas", new ArrayList<>())
                .append("bounties", new ArrayList<>())
                .append("owned_eggs", new ArrayList<>())
                .append("active_egg", "")
                .append("owned_augments", new ArrayList<>())
                .append("defeated_trainers", new ArrayList<>())
                .append("pokedex", new Document())
                .append("inventory", new PlayerInventory().serialize())
                .append("team", new PlayerTeam().serialize())

                ;

        LoggerHelper.logDatabaseInsert(PlayerDataQuery.class, data);

        Mongo.PlayerData.insertOne(data);

        PlayerSettingsQuery.register(player.getId());
        PlayerStatisticsQuery.register(player.getId());

        PlayerDataCache.addCache(player.getId());
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

    public void directMessage(MessageEmbed embed)
    {
        try
        {
            Pokecord.BOT_JDA.openPrivateChannelById(this.getID()).flatMap(channel -> channel.sendMessageEmbeds(embed)).queue();
        }
        catch (Exception e)
        {
            LoggerHelper.reportError(PlayerDataQuery.class, "Failed to DM " + this.getUsername() + " (ID: " + this.getID() + ")!", e);
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

    //Get the PlayerStatisticsQuery object
    public PlayerStatisticsQuery getStatistics()
    {
        if(this.statistics.isEmpty()) this.statistics = Optional.of(new PlayerStatisticsQuery(this.getID()));
        return this.statistics.get();
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
        if(this.inventory == null) this.inventory = new PlayerInventory(this.document.get("inventory", Document.class));
        return this.inventory;
    }

    public void updateInventory()
    {
        this.update(Updates.set("inventory", this.inventory.serialize()));
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

        if(new SplittableRandom().nextInt(100) < chance)
        {
            this.update(Updates.inc("exp", amount));

            this.getStatistics().incr(PlayerStatistic.MASTERY_EXP_EARNED, amount);

            if(!MasteryLevelManager.isMax(this) && MasteryLevelManager.MASTERY_LEVELS.get(this.getLevel() + 1).canLevelUp(this))
            {
                this.increaseLevel();

                //Starting Augments
                if(MasteryLevelManager.MASTERY_LEVELS.get(this.getLevel()).getFeatures().contains(Feature.AUGMENT_POKEMON))
                {
                    EnumSet.of(PokemonAugment.HP_BOOST, PokemonAugment.ATK_BOOST, PokemonAugment.DEF_BOOST, PokemonAugment.SPATK_BOOST, PokemonAugment.SPDEF_BOOST, PokemonAugment.SPD_BOOST)
                            .forEach(p -> this.addAugment(p.getAugmentID()));
                }

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

        if(amount < 0) this.getStatistics().incr(PlayerStatistic.CREDITS_SPENT, Math.abs(amount));
        else if(amount > 0) this.getStatistics().incr(PlayerStatistic.CREDITS_EARNED, Math.abs(amount));
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
        List<Pokemon> out = new ArrayList<>();
        for(int i = 0; i < in.size(); i++) out.add(Pokemon.build(in.get(i), i + 1));
        return out;
    }

    public void addPokemon(String UUID)
    {
        this.update(Updates.push("pokemon", UUID));
    }

    public void removePokemon(String UUID)
    {
        this.update(Updates.pull("pokemon", UUID));
        PokemonDataCache.removeCache(UUID);

        if(this.getTeam().contains(UUID)) this.getTeam().remove(UUID);
        if(this.getFavorites().contains(UUID)) this.removePokemonFromFavorites(UUID);
    }

    //key: "favorites"
    public List<String> getFavorites()
    {
        return this.document.getList("favorites", String.class);
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
        this.update(Updates.set("favorites", new ArrayList<>()));
    }

    //key: "active_zcrystal"
    public String getEquippedZCrystal()
    {
        return this.document.getString("active_zcrystal");
    }

    public void equipZCrystal(String z)
    {
        this.update(Updates.set("active_zcrystal", z));
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

    //key: "owned_forms"
    public List<PokemonEntity> getOwnedForms()
    {
        return this.document.getList("owned_forms", String.class).stream().map(PokemonEntity::cast).toList();
    }

    public void addOwnedForm(PokemonEntity form)
    {
        this.update(Updates.push("owned_forms", form.toString()));
    }

    //key: "owned_megas"
    public List<PokemonEntity> getOwnedMegas()
    {
        return this.document.getList("owned_megas", String.class).stream().map(PokemonEntity::cast).toList();
    }

    public void addOwnedMegas(PokemonEntity mega)
    {
        this.update(Updates.push("owned_megas", mega.toString()));
    }

    //key: "bounties"
    public List<String> getBountyIDs()
    {
        return this.document.getList("bounties", String.class);
    }

    public List<Bounty> getBounties()
    {
        return this.getBountyIDs().stream().map(Bounty::fromDB).collect(Collectors.toList());
    }

    private void updateBounty(Consumer<Bounty> checker)
    {
        if(this.document == null) this.update(null);

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

    //key: "owned_eggs"
    public List<String> getOwnedEggIDs()
    {
        return this.document.getList("owned_eggs", String.class);
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
        return this.document.getString("active_egg");
    }

    public PokemonEgg getActiveEgg()
    {
        return PokemonEgg.fromDB(this.getActiveEggID());
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

    //key: "owned_augments"
    public List<String> getOwnedAugmentIDs()
    {
        return this.document.getList("owned_augments", String.class);
    }

    public List<PokemonAugment> getOwnedAugments()
    {
        return this.getOwnedAugmentIDs().stream().map(PokemonAugment::fromID).collect(Collectors.toList());
    }

    public boolean isAugmentUnlocked(String augmentID)
    {
        return this.getOwnedAugmentIDs().contains(augmentID);
    }

    public void addAugment(String augmentID)
    {
        this.update(Updates.push("owned_augments", augmentID));
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
